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
import java.util.Arrays;

public class DecoderFeatureIndex extends FeatureIndex {
    private DoubleArrayTrie dat;

    public DecoderFeatureIndex() {
        dat = new DoubleArrayTrie();
    }

    public int getID(String key) {
        return dat.exactMatchSearch(key);
    }

    public void openBinModel(String path) throws IOException {
        ByteBuffer bytes = ByteUtil.readAsByteBuffer(path);
        int version = bytes.getInt(); // unsigned int
        int type = bytes.getInt();
        costFactor = bytes.getDouble();
        maxId = bytes.getInt(); // unsigned int
        xsize = bytes.getInt(); // unsigned int
        int dsize = bytes.getInt(); // unsigned int

        int yStrSize = bytes.getInt(); // unsigned int
        String yStr = ByteUtil.getString(bytes, yStrSize, StandardCharsets.UTF_8);
        y.addAll(Arrays.asList(yStr.split("\0")));

        int tmplStrSize = bytes.getInt(); // unsigned int
        String[] tmplStr = ByteUtil.getString(bytes, tmplStrSize, StandardCharsets.UTF_8).split("\0");
        for (String tmpl:tmplStr) {
            if (tmpl.startsWith("U")) {
                unigramTempls.add(tmpl);
            } else if (tmpl.startsWith("B")) {
                bigramTempls.add(tmpl);
            }
        }

        dat.setArray(bytes, dsize);

        alpha = new double[maxId];
        for (int i = 0; i< maxId; i++) {
            alpha[i] = bytes.getFloat();
        }

        if (bytes.position() != bytes.limit()) {
            throw new IOException("The offset is not equal to the length of byte array.");
        }
    }

}
