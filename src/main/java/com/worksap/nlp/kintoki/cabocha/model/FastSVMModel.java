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

package com.worksap.nlp.kintoki.cabocha.model;

import com.worksap.nlp.dartsclone.DoubleArray;
import com.worksap.nlp.kintoki.cabocha.util.ByteUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FastSVMModel extends SVMModel {

    private static final int DICTIONARY_MAGIC_ID = 0xef522177;

    private int bias;
    private float normalizeFactor;
    private int freqFeatureSize;
    private List<Integer> nodePosList;
    private List<Integer> weight1;
    private List<Integer> weight2;
    private DoubleArray dicDa;
    private DoubleArray featureDa;

    private static final int PKE_BASE = 0xfffff; // 1048575

    @Override
    public int id(String key) {
        return dicDa.exactMatchSearch(key.getBytes(StandardCharsets.UTF_8))[0];
    }

    @Override
    public void open(String path) throws IOException {
        openBinModel(path);
    }

    public void openBinModel(String path) throws IOException {
        ByteBuffer bytes = ByteUtil.readAsByteBuffer(path);
        int magic = bytes.getInt(); // unsigned int
        if ((magic ^ DICTIONARY_MAGIC_ID) != bytes.limit()) {
            throw new IOException("dictionary file is broken");
        }

        int version = bytes.getInt(); // unsigned int
        int allPsize = bytes.getInt(); // unsigned int
        String parameter = ByteUtil.getString(bytes, allPsize, StandardCharsets.UTF_8);
        normalizeFactor = bytes.getFloat();
        bias = bytes.getInt();
        int featureSize = bytes.getInt(); // unsigned int
        freqFeatureSize = bytes.getInt(); // unsigned int
        int dicDaSize = bytes.getInt(); // unsigned int
        int featureDaSize = bytes.getInt(); // unsigned int

        int size = dicDaSize / 4;
        IntBuffer array = ByteUtil.getIntBuffer(bytes, dicDaSize);
        dicDa = new DoubleArray();
        dicDa.setArray(array, size);

        size = featureDaSize / 4;
        array = ByteUtil.getIntBuffer(bytes, featureDaSize);
        featureDa = new DoubleArray();
        featureDa.setArray(array, size);

        nodePosList = new ArrayList<>();
        for (int i = 0; i < featureSize; i++) {
            nodePosList.add(bytes.getInt()); // unsigned int
        }
        this.weight1 = new ArrayList<>();
        for (int i = 0; i < featureSize; i++) {
            this.weight1.add(bytes.getInt());
        }
        this.weight2 = new ArrayList<>();
        long len = (freqFeatureSize * (freqFeatureSize - 1)) / 2;
        for (int i = 0; i < len; i++) {
            this.weight2.add(bytes.getInt());
        }
        if (bytes.position() != bytes.limit()) {
            throw new IOException("The offset is not equal to the length of byte array.");
        }
    }

    class FeatureKey {
        byte[] id = new byte[7];
        byte len;
    }

    private void encodeBER(int value, FeatureKey featureKey) {
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
    }

    @Override
    public double classify(List<Integer> x) {
        int size = x.size();
        int score = -bias;
        int freqSize = 0;
        List<FeatureKey> key = new ArrayList<>(size);
        for (int i = 0; i < size; ++i) {
            if (x.get(i) < freqFeatureSize) {
                freqSize = i;
            }
            FeatureKey featureKey = new FeatureKey();
            encodeBER(x.get(i), featureKey);
            key.add(featureKey);
        }
        ++freqSize;

        int kOffset = 2 * freqFeatureSize - 3;
        for (int i1 = 0; i1 < freqSize; ++i1) {
            score += weight1.get(x.get(i1));
            int pos = x.get(i1) * (kOffset - x.get(i1)) / 2 - 1;
            for (int i2 = i1 + 1; i2 < freqSize; ++i2) {
                score += weight2.get(pos + x.get(i2));
            }
        }

        for (int i1 = 0; i1 < freqSize; ++i1) {
            int nodePos = nodePosList.get(x.get(i1));
            if (nodePos == 0) {
                continue;
            }
            for (int i2 = freqSize; i2 < size; ++i2) {
                int nodePos2 = nodePos;
                int keyPos = 0;
                FeatureKey k = key.get(i2);
                DoubleArray.TraverseResult result = featureDa.traverse(k.id, keyPos, k.len, nodePos2);
                if (result.result >= 0) {
                    score += (result.result - PKE_BASE);
                }
            }
        }

        for (int i1 = freqSize; i1 < size; ++i1) {
            score += weight1.get(x.get(i1));
            int nodePos = nodePosList.get(x.get(i1));
            if (nodePos == 0) {
                continue;
            }
            for (int i2 = i1 + 1; i2 < size; ++i2) {
                int nodePos2 = nodePos;
                int keyPos = 0;
                FeatureKey k = key.get(i2);
                DoubleArray.TraverseResult result = featureDa.traverse(k.id, keyPos, k.len, nodePos2);
                if (result.result >= 0) {
                    score += (result.result - PKE_BASE);
                }
            }
        }

        return score * normalizeFactor;
    }

    @Override
    public void close() {
        // do nothing
    }

}
