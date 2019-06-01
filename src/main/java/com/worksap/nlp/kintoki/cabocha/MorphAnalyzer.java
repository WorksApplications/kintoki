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

import com.worksap.nlp.sudachi.Morpheme;

import java.io.IOException;
import java.util.List;

public class MorphAnalyzer extends Analyzer {

    private SudachiTokenizer tokenizer;

    @Override
    public void open(Param param) throws IOException {
        this.tokenizer = SudachiTokenizer.getInstance(param.getString(Param.SUDACHI_DICT));
    }

    @Override
    public void parse(Tree tree) {
        List<Morpheme> morphemes = tokenizer.parse(tree.getSentence());
        tree.read(morphemes);
        tree.setOutputLayer(OutputLayerType.OUTPUT_POS);
    }
}
