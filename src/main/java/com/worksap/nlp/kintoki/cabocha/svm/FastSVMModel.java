/*
 * Copyright 2019 Works Applications Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.worksap.nlp.kintoki.cabocha.svm;

import com.worksap.nlp.dartsclone.DoubleArray;
import com.worksap.nlp.kintoki.cabocha.util.ByteUtil;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FastSVMModel implements SVMModel {

  private static final int SVM_MODEL_VERSION = 102;
  private static final int DICTIONARY_MAGIC_ID = 0xef522177;
  private static final int PKE_BASE = 0xfffff; // 1048575

  private int bias;
  private float normalizeFactor;
  private int freqFeatureSize;
  private List<Integer> nodePosList;
  private List<Integer> weight1;
  private List<Integer> weight2;
  private DoubleArray dicDa;
  private DoubleArray featureDa;

  public static SVMModel openBinaryModel(String path) throws IOException {
    ByteBuffer bytes = ByteUtil.readAsByteBuffer(path);
    int magic = bytes.getInt(); // unsigned int
    if ((magic ^ DICTIONARY_MAGIC_ID) != bytes.limit()) {
      throw new IOException("dictionary file is broken");
    }
    int version = bytes.getInt(); // unsigned int
    if (version != SVM_MODEL_VERSION) {
      throw new IOException("Invalid model");
    }
    readParameter(bytes);

    FastSVMModel model = new FastSVMModel();
    model.normalizeFactor = bytes.getFloat();
    model.bias = bytes.getInt();
    int featureSize = bytes.getInt(); // unsigned int
    model.freqFeatureSize = bytes.getInt(); // unsigned int
    int dicDaSize = bytes.getInt(); // unsigned int
    int featureDaSize = bytes.getInt(); // unsigned int

    int size = dicDaSize / 4;
    IntBuffer array = ByteUtil.getIntBuffer(bytes, dicDaSize);
    model.dicDa = new DoubleArray();
    model.dicDa.setArray(array, size);

    size = featureDaSize / 4;
    array = ByteUtil.getIntBuffer(bytes, featureDaSize);
    model.featureDa = new DoubleArray();
    model.featureDa.setArray(array, size);

    model.nodePosList = new ArrayList<>();
    for (int i = 0; i < featureSize; i++) {
      model.nodePosList.add(bytes.getInt()); // unsigned int
    }
    model.weight1 = new ArrayList<>();
    for (int i = 0; i < featureSize; i++) {
      model.weight1.add(bytes.getInt());
    }
    model.weight2 = new ArrayList<>();
    long len = (model.freqFeatureSize * (model.freqFeatureSize - 1)) / 2;
    for (int i = 0; i < len; i++) {
      model.weight2.add(bytes.getInt());
    }
    if (bytes.position() != bytes.limit()) {
      throw new IOException("The offset is not equal to the length of byte array.");
    }

    return model;
  }

  private static Map<String, String> readParameter(ByteBuffer bytes) {
    int allPsize = bytes.getInt(); // unsigned int
    String[] params = ByteUtil.getString(bytes, allPsize, StandardCharsets.UTF_8).split("\t");

    Map<String, String> parameters = new HashMap<>();
    for (int i = 0; i < params.length; i += 2) {
      parameters.put(params[i], params[i + 1]);
    }

    return parameters;
  }

  class FeatureKey {
    byte[] id = new byte[7];
    byte len;
  }

  private FeatureKey encodeBER(int value) {
    FeatureKey featureKey = new FeatureKey();
    byte length = 0;
    ++value;
    byte[] array = featureKey.id;
    array[length++] = (byte) (value & 0x7f);
    while ((value >>= 7) > 0) {
      ++value;
      array[length - 1] |= 0x80;
      array[length++] = (byte) (value & 0x7f);
    }
    featureKey.len = length;
    return featureKey;
  }

  @Override
  public int id(String key) {
    return dicDa.exactMatchSearch(key.getBytes(StandardCharsets.UTF_8))[0];
  }

  @Override
  public double classify(List<Integer> x) {
    int size = x.size();
    int score = -bias;
    List<FeatureKey> keys = new ArrayList<>(size);
    int freqSize = encodeKeys(x, keys) + 1;

    score += classify1(x, freqSize);
    score += classify2(x, keys, freqSize);

    return score * normalizeFactor;
  }

  private int encodeKeys(List<Integer> x, List<FeatureKey> keys) {
    int freqSize = 0;
    for (int i = 0; i < x.size(); ++i) {
      if (x.get(i) < freqFeatureSize) {
        freqSize = i;
      }
      keys.add(encodeBER(x.get(i)));
    }
    return freqSize;
  }

  private int classify1(List<Integer> x, int freqSize) {
    int score = 0;
    int kOffset = 2 * freqFeatureSize - 3;
    for (int i1 = 0; i1 < freqSize; ++i1) {
      score += weight1.get(x.get(i1));
      int pos = x.get(i1) * (kOffset - x.get(i1)) / 2 - 1;
      for (int i2 = i1 + 1; i2 < freqSize; ++i2) {
        score += weight2.get(pos + x.get(i2));
      }
    }
    return score;
  }

  private int classify2(List<Integer> x, List<FeatureKey> keys, int freqSize) {
    int score = 0;
    for (int i1 = 0; i1 < x.size(); ++i1) {
      if (i1 >= freqSize) {
        score += weight1.get(x.get(i1));
      }
      int nodePos = nodePosList.get(x.get(i1));
      if (nodePos == 0) {
        continue;
      }
      for (int i2 = (i1 < freqSize) ? freqSize : i1 + 1; i2 < x.size(); ++i2) {
        int keyPos = 0;
        FeatureKey k = keys.get(i2);
        DoubleArray.TraverseResult result = featureDa.traverse(k.id, keyPos, k.len, nodePos);
        if (result.result >= 0) {
          score += (result.result - PKE_BASE);
        }
      }
    }
    return score;
  }
}
