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

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import java.io.IOException;
import static org.junit.Assert.assertArrayEquals;


public class MorphAnalyzerTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private static Param param;
    private static Analyzer morpher;
    private static Tree tree;

    @Before
    public void setUp() throws IOException {
        Utils.copyResources(temporaryFolder.getRoot().toPath());
        String configPath = Utils.buildConfig(temporaryFolder.getRoot().toPath());

        param = new Param();
        param.loadConfig(configPath);

        morpher = new MorphAnalyzer();
        morpher.open(param);

        tree = new Tree();
    }

    @Test
    public void parse() {
        final String sent = "太郎は花子が読んでいる本を次郎に渡した。";

        tree.setSentence(sent);
        morpher.parse(tree);

        assertArrayEquals(new String[]{"太郎", "は", "花子", "が", "読ん", "で", "いる", "本", "を", "次郎", "に",
                "渡し", "た", "。"}, tree.getTokens().stream().map(t -> t.getSurface()).toArray(String[]::new));
    }

}
