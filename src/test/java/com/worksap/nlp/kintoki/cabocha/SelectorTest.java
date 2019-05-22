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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class SelectorTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private static Param param;
    private static Analyzer morpher;
    private static Analyzer chunker;
    private static Analyzer selecter;
    private static Tree tree;

    @Before
    public void setUp() throws IOException {
        Utils.copyResources(temporaryFolder.getRoot().toPath());
        String configPath = Utils.buildConfig(temporaryFolder.getRoot().toPath());
        param = new Param();
        param.loadConfig(configPath);

        morpher = new MorphAnalyzer();
        morpher.open(param);

        chunker = new Chunker();
        chunker.open(param);

        selecter = new Selector();
        selecter.open(param);

        tree = new Tree();
    }

    @Test
    public void parse() {
        final String sent = "太郎は花子が読んでいる本を次郎に渡した。";

        tree.setSentence(sent);
        morpher.parse(tree);
        chunker.parse(tree);
        selecter.parse(tree);

        assertNotNull(tree.getChunks());
        assertEquals(6, tree.getChunkSize());

        Chunk chunk = tree.chunk(0);
        assertEquals(0, chunk.getHeadPos());
        assertEquals(1, chunk.getFuncPos());

        chunk = tree.chunk(1);
        assertEquals(0, chunk.getHeadPos());
        assertEquals(1, chunk.getFuncPos());

        chunk = tree.chunk(2);
        assertEquals(2, chunk.getHeadPos());
        assertEquals(2, chunk.getFuncPos());

        chunk = tree.chunk(3);
        assertEquals(0, chunk.getHeadPos());
        assertEquals(1, chunk.getFuncPos());

        chunk = tree.chunk(4);
        assertEquals(0, chunk.getHeadPos());
        assertEquals(1, chunk.getFuncPos());

        chunk = tree.chunk(5);
        assertEquals(0, chunk.getHeadPos());
        assertEquals(1, chunk.getFuncPos());
    }

}
