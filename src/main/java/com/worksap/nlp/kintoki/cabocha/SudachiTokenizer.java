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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SudachiTokenizer {

    private static Dictionary dictionary;
    private static final int MAX_LENGTH = 5000;
    private static final String[] spliters = { "\n", "。", "、", " " };
    private Tokenizer tokenizer;

    private SudachiTokenizer() {
        tokenizer = dictionary.create();
    }

    private static synchronized void load(String dictPath) {
        if (dictionary == null) {
            try {
                dictionary = new DictionaryFactory().create(dictPath, null);
            } catch (IOException e) {
                throw new IllegalStateException("check dict:" + dictPath, e);
            }
        }
    }

    public static SudachiTokenizer getInstance(String dictPath) {
        load(dictPath);
        return new SudachiTokenizer();
    }

    public List<String> tokenize(String text) {
        if (!Utils.check((text))) {
            return Collections.emptyList();
        }
        List<String> tokens = new ArrayList<>();
        List<String> sents = splitText(text);
        for (String sent : sents) {
            tokens.addAll(
                    tokenizer.tokenize(SplitMode.A, sent).stream().map(Morpheme::surface).collect(Collectors.toList()));
        }
        return tokens;
    }

    public List<Integer> listId(String text) {
        if (!Utils.check((text))) {
            return Collections.emptyList();
        }
        return tokenizer.tokenize(SplitMode.A, text).stream().map(Morpheme::getWordId).collect(Collectors.toList());
    }

    public List<Morpheme> parse(String text) {
        if (!Utils.check((text))) {
            return Collections.emptyList();
        }
        return tokenizer.tokenize(SplitMode.A, text);
    }

    private List<String> splitText(String text) {
        List<String> allSents = new ArrayList<>();
        if (text.length() > MAX_LENGTH) {
            boolean isSplited = false;
            for (String spliter : spliters) {
                String[] sents = text.split(spliter);
                if (sents != null && sents.length > 1) {
                    for (String sent : sents) {
                        if (sent.length() > MAX_LENGTH) {
                            List<String> subSents = splitText(sent);
                            allSents.addAll(subSents);
                        } else {
                            allSents.add(sent);
                        }
                    }
                    isSplited = true;
                    break;
                }
            }
            if (!isSplited) {
                while (text.length() > MAX_LENGTH) {
                    String subSent = text.substring(0, MAX_LENGTH);
                    allSents.add(subSent);
                    text = text.substring(MAX_LENGTH);
                }
                allSents.add(text);
            }
        } else {
            allSents.add(text);
        }
        return allSents;
    }

}
