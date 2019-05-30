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

package com.worksap.nlp.kintoki.cabocha.crf;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import com.worksap.nlp.kintoki.cabocha.Utils;

public class TaggerTest {

    private Tagger tagger;
    private String modelFileName;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before
    public void setUp() throws IOException {
        Utils.copyResources(temporaryFolder.getRoot().toPath());
        modelFileName = temporaryFolder.getRoot().toPath().resolve("chunk.bccwj.model").toString();
        tagger = Tagger.openBinaryModel(modelFileName, 1.0);
    }

    @Test
    public void openBinaryModel() {
        assertNotNull(tagger);
    }

    @Test(expected = IllegalArgumentException.class)
    public void openBinaryModelWithNegativeCostFactor() throws IOException {
        tagger = Tagger.openBinaryModel(modelFileName, -1.0);
    }

    @Test
    public void add() {
        tagger.add("太郎", "名詞-固有名詞-人名-名");
        assertEquals(1, tagger.size());
        tagger.add("は", "助詞-係助詞");
        assertEquals(2, tagger.size());
        tagger.clear();
        assertEquals(0, tagger.size());
        tagger.add("太郎", "名詞-固有名詞-人名-名");
        assertEquals(1, tagger.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void addWithTooFewColumns() {
        tagger.add("太郎");
    }

    @Test
    public void parse() {
        tagger.add("太郎", "名詞-固有名詞-人名-名");
        tagger.add("は", "助詞-係助詞");
        tagger.add("花子", "名詞-固有名詞-人名-名");
        tagger.add("が", "助詞-格助詞");
        tagger.parse();
     
        assertEquals(0, tagger.y(0));
        assertEquals(1, tagger.y(1));
        assertEquals(0, tagger.y(2));
        assertEquals(1, tagger.y(3));
    }

    @Test
    public void parseWithEmpty() {
        tagger.parse();
        assertEquals(0, tagger.size());
    }

    @Test
    public void yname() {
        assertEquals("B", tagger.yname(0));
        assertEquals("I", tagger.yname(1));
    }
}