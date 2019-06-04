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

import com.worksap.nlp.kintoki.cabocha.util.Utils;
import com.worksap.nlp.sudachi.Dictionary;
import com.worksap.nlp.sudachi.DictionaryFactory;
import com.worksap.nlp.sudachi.Morpheme;
import com.worksap.nlp.sudachi.Tokenizer;
import com.worksap.nlp.sudachi.Tokenizer.SplitMode;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class SudachiTokenizer {

  private static Dictionary dictionary;
  private Tokenizer tokenizer;

  private SudachiTokenizer() {
    tokenizer = dictionary.create();
  }

  private static synchronized void load(String dictPath) throws IOException {
    if (dictionary == null) {
      dictionary = new DictionaryFactory().create(dictPath, null);
    }
  }

  public static SudachiTokenizer getInstance(String dictPath) throws IOException {
    load(dictPath);
    return new SudachiTokenizer();
  }

  public List<Morpheme> parse(String text) {
    if (!Utils.check((text))) {
      return Collections.emptyList();
    }
    return tokenizer.tokenize(SplitMode.A, text);
  }
}
