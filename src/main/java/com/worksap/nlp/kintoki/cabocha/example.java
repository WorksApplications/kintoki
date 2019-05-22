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

import java.io.IOException;

public class example {

    public static void main(String[] args) throws IOException {
        String sent = "太郎は花子が読んでいる本を次郎に渡した。";
        String result = "";

        Cabocha cabocha = new Cabocha();
        Tree tree = cabocha.parse(sent);

        if (tree.getChunks() != null) {
            for (int i=0; i<tree.getChunkSize(); i++) {
                Chunk chunk = tree.chunk(i);
                result += "* "+i+" "+chunk.getLink()+"D ";
                result += chunk.getHeadPos()+"/"+chunk.getFuncPos()+" ";
                result += chunk.getScore()+"\n";
                for (int j=0; j<chunk.getTokenSize();j++) {
                    Token token = chunk.token(j);
                    result += token.getSurface()+"\t"+token.getFeature()+"\n";
                }
            }
            result += "EOS\n";
        }
        System.out.println(result);
    }

}
