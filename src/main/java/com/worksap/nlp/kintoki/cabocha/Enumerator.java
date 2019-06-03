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

enum FormatType {
    FORMAT_TREE(Constant.CABOCHA_FORMAT_TREE),
    FORMAT_LATTICE(Constant.CABOCHA_FORMAT_LATTICE),
    FORMAT_TREE_LATTICE(Constant.CABOCHA_FORMAT_TREE_LATTICE),
    FORMAT_XML(Constant.CABOCHA_FORMAT_XML),
    FORMAT_CONLL(Constant.CABOCHA_FORMAT_CONLL),
    FORMAT_NONE(Constant.CABOCHA_FORMAT_NONE);

    private final int value;

    FormatType(int value) {
        this.value = value;
    }

    int getValue() {
        return value;
    }
}

enum InputLayerType {
    INPUT_RAW_SENTENCE(Constant.CABOCHA_INPUT_RAW_SENTENCE),
    INPUT_POS(Constant.CABOCHA_INPUT_POS),
    INPUT_CHUNK(Constant.CABOCHA_INPUT_CHUNK),
    INPUT_SELECTION(Constant.CABOCHA_INPUT_SELECTION),
    INPUT_DEP(Constant.CABOCHA_INPUT_DEP);

    private final int value;

    InputLayerType(int value) {
        this.value = value;
    }

    int getValue() {
        return value;
    }
}

enum OutputLayerType {
    OUTPUT_RAW_SENTENCE(Constant.CABOCHA_OUTPUT_RAW_SENTENCE),
    OUTPUT_POS(Constant.CABOCHA_OUTPUT_POS),
    OUTPUT_CHUNK(Constant.CABOCHA_OUTPUT_CHUNK),
    OUTPUT_SELECTION(Constant.CABOCHA_OUTPUT_SELECTION),
    OUTPUT_DEP(Constant.CABOCHA_OUTPUT_DEP);

    private final int value;

    OutputLayerType(int value) {
        this.value = value;
    }

    int getValue() {
        return value;
    }
}

enum ParserType {
    TRAIN_NE(Constant.CABOCHA_TRAIN_NE),
    TRAIN_CHUNK(Constant.CABOCHA_TRAIN_CHUNK),
    TRAIN_DEP(Constant.CABOCHA_TRAIN_DEP);

    private final int value;

    ParserType(int value) {
        this.value = value;
    }

    int getValue() {
        return value;
    }
}