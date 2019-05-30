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

import com.worksap.nlp.kintoki.cabocha.crf.Tagger;
import com.worksap.nlp.kintoki.cabocha.model.CRFModelFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Chunker extends Analyzer {

    private Tagger tagger = null;

    @Override
    public void open(Param param) throws IOException {
        if (getActionMode() == Constant.PARSING_MODE) {
            loadBinModel(param);
        }
    }

    private void loadBinModel(Param param) throws IOException {
        this.tagger = CRFModelFactory.createTagger(param);
    }

    @Override
    public void parse(Tree tree) {
        int tokenSize = tree.getTokenSize();
        for (int i = 0; i < tokenSize; i++) {
            String line = "";
            String pos = getPos(tree.token(i).getFeatureList());
            line += tree.token(i).getNormalizedSurface();
            line += "\t" + pos;
            tagger.add(line);
        }

        tagger.parse();

        int tokenPos = 0;
        List<Token> tokens = new ArrayList<>();
        for (int i = 0; i < tokenSize; i++) {
            String tag = tagger.yname(tagger.y(i));
            if ((i > 0 && tag.equals("B"))) {
                Chunk chunk = new Chunk();
                chunk.setTokenPos(tokenPos);
                chunk.getTokens().addAll(tokens);
                tree.getChunks().add(chunk);
                tokenPos = i;
                tokens = new ArrayList<>();
            }
            tokens.add(tree.getTokens().get(i));
            if (i == tokenSize - 1) {
                Chunk chunk = new Chunk();
                chunk.setTokenPos(tokenPos);
                chunk.getTokens().addAll(tokens);
                tree.getChunks().add(chunk);
            }
        }

        tagger.clear();

        tree.setOutputLayer(OutputLayerType.OUTPUT_CHUNK);
    }

    private String getPos(List<String> featureList) {
        StringBuilder pos = new StringBuilder();
        for (int j = 0; j < featureList.size(); j++) {
            if (("*").equals(featureList.get(j))) {
                break;
            }
            if (j > 0) {
                pos.append("-");
            }
            pos.append(featureList.get(j));
        }
        return pos.toString();
    }

}
