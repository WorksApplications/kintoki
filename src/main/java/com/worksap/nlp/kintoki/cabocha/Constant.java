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

public class Constant {

    public final static int CABOCHA_FORMAT_TREE         = 0;
    public final static int CABOCHA_FORMAT_LATTICE      = 1;
    public final static int CABOCHA_FORMAT_TREE_LATTICE = 2;
    public final static int CABOCHA_FORMAT_XML          = 3;
    public final static int CABOCHA_FORMAT_CONLL        = 4;
    public final static int CABOCHA_FORMAT_NONE         = 5;

    public final static int CABOCHA_INPUT_RAW_SENTENCE  = 0;
    public final static int CABOCHA_INPUT_POS           = 1;
    public final static int CABOCHA_INPUT_CHUNK         = 2;
    public final static int CABOCHA_INPUT_SELECTION     = 3;
    public final static int CABOCHA_INPUT_DEP           = 4;

    public final static int CABOCHA_OUTPUT_RAW_SENTENCE = 0;
    public final static int CABOCHA_OUTPUT_POS          = 1;
    public final static int CABOCHA_OUTPUT_CHUNK        = 2;
    public final static int CABOCHA_OUTPUT_SELECTION    = 3;
    public final static int CABOCHA_OUTPUT_DEP          = 4;

    public final static int CABOCHA_TRAIN_NE    = 0;
    public final static int CABOCHA_TRAIN_CHUNK = 1;
    public final static int CABOCHA_TRAIN_DEP   = 2;

    public final static int CABOCHA_MAX_LINE_SIZE = 8192;

    public final static int PARSING_MODE = 0;
    public final static int TRAINING_MODE = 1;
    public final static int MODEL_VERSION = 100;

    public final static String CABOCHA_DEFAULT_POSSET = "UNIDIC";
    public final static String CABOCHA_DEFAULT_CHARSET = "UTF-8";

    public final static String COPYRIGHT = "Kintoki-CaboCha\nCopyright(C) Works Applications, All rights reserved.\n";
    public final static String VERSION = "0.1.1";
    public final static String PACKAGE = "Kintoki-CaboCha";

}
