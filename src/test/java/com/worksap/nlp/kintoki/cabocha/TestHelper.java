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

package com.worksap.nlp.kintoki.cabocha;

import java.util.ArrayList;
import java.util.List;

public class TestHelper {

    public static String getResultString(Tree tree) {
        String result = "";
        for (int i = 0; i < tree.getChunkSize(); i++) {
            Chunk chunk = tree.chunk(i);
            result += "* " + i + " " + chunk.getLink() + "D ";
            result += chunk.getHeadPos() + "/" + chunk.getFuncPos() + "\n";
            for (int j = 0; j < chunk.getTokenSize(); j++) {
                Token token = chunk.token(j);
                result += token.getSurface() + "\t" + token.getFeature() + "\n";
            }
        }
        result += "EOS\n";
        return result;
    }

    public static List<String> getChunkStrings(Tree tree) {
        List<String> chunks = new ArrayList<>();
        for (int i = 0; i < tree.getChunkSize(); i++) {
            Chunk chunk = tree.chunk(i);
            String text = "";
            for (int j = 0; j < chunk.getTokenSize(); j++) {
                Token token = chunk.token(j);
                text += token.getSurface();
            }
            chunks.add(text);
        }
        return chunks;
    }

}
