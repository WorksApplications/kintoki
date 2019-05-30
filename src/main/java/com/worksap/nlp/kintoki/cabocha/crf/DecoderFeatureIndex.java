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

package com.worksap.nlp.kintoki.cabocha.crf;

import com.worksap.nlp.kintoki.cabocha.util.ByteUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DecoderFeatureIndex extends FeatureIndex {
    private static final int MODEL_VERSION = 100;
    private DoubleArrayTrie dat;

    private DecoderFeatureIndex() {
    }

    @Override
    protected int getID(String key) {
        return dat.exactMatchSearch(key);
    }

    static DecoderFeatureIndex openBinaryModel(String path) throws IOException {
        DecoderFeatureIndex featureIndex = new DecoderFeatureIndex();
        ByteBuffer bytes = ByteUtil.readAsByteBuffer(path);
        int version = bytes.getInt(); // unsigned int
        if (MODEL_VERSION / 100 != version / 100) {
            throw new IllegalArgumentException("Invalid model");
        }
        int type = bytes.getInt();
        featureIndex.costFactor = bytes.getDouble();
        featureIndex.maxId = bytes.getInt(); // unsigned int
        featureIndex.xsize = bytes.getInt(); // unsigned int
        int dsize = bytes.getInt(); // unsigned int

        int yStrSize = bytes.getInt(); // unsigned int
        String yStr = ByteUtil.getString(bytes, yStrSize, StandardCharsets.UTF_8);
        featureIndex.y = Arrays.asList(yStr.split("\0"));

        int tmplStrSize = bytes.getInt(); // unsigned int
        String[] tmplStr = ByteUtil.getString(bytes, tmplStrSize, StandardCharsets.UTF_8).split("\0");
        List<String> unigramTempls = new ArrayList<>();
        List<String> bigramTempls = new ArrayList<>();
        for (String tmpl : tmplStr) {
            if (tmpl.startsWith("U")) {
                unigramTempls.add(tmpl);
            } else if (tmpl.startsWith("B")) {
                bigramTempls.add(tmpl);
            }
        }
        featureIndex.unigramTempls = unigramTempls;
        featureIndex.bigramTempls = bigramTempls;

        featureIndex.dat = new DoubleArrayTrie(bytes, dsize);

        featureIndex.alpha = new double[featureIndex.maxId];
        for (int i = 0; i < featureIndex.maxId; i++) {
            featureIndex.alpha[i] = bytes.getFloat();
        }

        if (bytes.position() != bytes.limit()) {
            throw new IOException("The offset is not equal to the length of byte array.");
        }

        return featureIndex;
    }

}
