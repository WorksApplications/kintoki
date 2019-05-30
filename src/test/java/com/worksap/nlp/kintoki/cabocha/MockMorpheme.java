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
import com.worksap.nlp.sudachi.Tokenizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class MockMorpheme implements Morpheme {
    String surface;
    String normalizedForm;
    List<String> partOfSpeech;

    MockMorpheme(String surface, String normalizedForm, String partOfSpeech) {
        this.surface = surface;
        this.normalizedForm = (normalizedForm != null) ? normalizedForm : surface;
        this.partOfSpeech = Arrays.asList(partOfSpeech.split(","));
    }

    public int begin() {
        return 0;
    }

    public int end() {
        return 0;
    }

    public String surface() {
        return surface;
    }

    public List<String> partOfSpeech() {
        return partOfSpeech;
    }

    public short partOfSpeechId() {
        return 0;
    }

    public String dictionaryForm() {
        return normalizedForm;
    }

    public String normalizedForm() {
        return normalizedForm;
    }

    public String readingForm() {
        return "";
    }

    public List<Morpheme> split(Tokenizer.SplitMode mode) {
        return Collections.emptyList();
    }

    public boolean isOOV() {
        return false;
    }

    public int getWordId() {
        return 0;
    }

    public int getDictionaryId() {
        return 0;
    }

    static List<Morpheme> getExampleList() {
        List<Morpheme> morphemes = new ArrayList<>();

        MockMorpheme m = new MockMorpheme("太郎", null, "名詞,固有名詞,人名,名,*,*");
        morphemes.add(m);
        m = new MockMorpheme("は", null, "助詞,係助詞,*,*,*,*");
        morphemes.add(m);
        m = new MockMorpheme("花子", null, "名詞,固有名詞,人名,名,*,*");
        morphemes.add(m);
        m = new MockMorpheme("が", null, "助詞,格助詞,*,*,*,*");
        morphemes.add(m);
        m = new MockMorpheme("読ん", "読む", "動詞,一般,*,*,五段-マ行,連用形-撥音便");
        morphemes.add(m);
        m = new MockMorpheme("で", null, "助詞,接続助詞,*,*,*,*");
        morphemes.add(m);
        m = new MockMorpheme("いる", "居る", "動詞,非自立可能,*,*,上一段-ア行,連体形-一般");
        morphemes.add(m);
        m = new MockMorpheme("本", null, "名詞,普通名詞,一般,*,*,*");
        morphemes.add(m);
        m = new MockMorpheme("を", null, "助詞,格助詞,*,*,*,*");
        morphemes.add(m);
        m = new MockMorpheme("次郎", null, "名詞,固有名詞,人名,名,*,*");
        morphemes.add(m);
        m = new MockMorpheme("に", null, "助詞,格助詞,*,*,*,*");
        morphemes.add(m);
        m = new MockMorpheme("渡し", "渡す", "動詞,一般,*,*,五段-サ行,連用形-一般");
        morphemes.add(m);
        m = new MockMorpheme("た", null, "助動詞,*,*,*,助動詞-タ,終止形-一般");
        morphemes.add(m);
        m = new MockMorpheme("。", null, "補助記号,句点,*,*,*,*");
        morphemes.add(m);

        return morphemes;
    }
}
