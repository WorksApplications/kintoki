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

import com.worksap.nlp.kintoki.cabocha.Param;
import com.worksap.nlp.kintoki.cabocha.crf.Tagger;

import java.io.IOException;

public class CRFModelFactory {

    private CRFModelFactory() {
    }

    public static synchronized Tagger createTagger(Param param) throws IOException {
        Tagger tagger = new Tagger();
        String path = param.getString(Param.CHUNKER_MODEL);
        int nbest = param.getInt(Param.NBEST);
        int vlevel = param.getInt(Param.VERBOSE);
        double costFactor = param.getDouble(Param.COST_FACTOR);

        tagger.openBinModel(path, nbest, vlevel, costFactor);

        return tagger;
    }

}
