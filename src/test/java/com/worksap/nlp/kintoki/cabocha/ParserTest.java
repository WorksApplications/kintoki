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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ParserTest {

    static final int[] INPUT_LAYERS = { Constant.CABOCHA_INPUT_RAW_SENTENCE, Constant.CABOCHA_INPUT_POS,
            Constant.CABOCHA_INPUT_CHUNK, Constant.CABOCHA_INPUT_SELECTION, Constant.CABOCHA_INPUT_DEP };

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();
    String configPath;

    @Before
    public void setUp() throws IOException {
        TestUtils.copyResources(temporaryFolder.getRoot().toPath());
        configPath = TestUtils.buildConfig(temporaryFolder.getRoot().toPath());
    }

    @Test
    public void initialize() throws IOException {
        Parser parser = new Parser();
        assertNotNull(parser);
    }

    @Test
    public void initializeWithString() throws IOException {
        Parser parser = new Parser(configPath);
        assertNotNull(parser);
    }

    @Test(expected = NullPointerException.class)
    public void initializeWithNullString() throws IOException {
        String paramPath = null;
        new Parser(paramPath);
    }

    @Test
    public void parseToRawSentence() throws IOException {
        for (int inputLayer : INPUT_LAYERS) {
            Param param = new Param();
            param.loadConfig(configPath);
            param.set(Param.INPUT_LAYER, inputLayer);
            param.set(Param.OUTPUT_LAYER, Constant.CABOCHA_OUTPUT_RAW_SENTENCE);
            Parser parser = new Parser(param);
            parser.open();
            Tree tree = parser.parse("");
            assertEquals("\n", tree.toString(FormatType.FORMAT_LATTICE));
        }
    }

    @Test
    public void parseToPOS() throws IOException {
        for (int inputLayer : INPUT_LAYERS) {
            Param param = new Param();
            param.loadConfig(configPath);
            param.set(Param.INPUT_LAYER, inputLayer);
            param.set(Param.OUTPUT_LAYER, Constant.CABOCHA_OUTPUT_POS);
            Parser parser = new Parser(param);
            parser.open();
            Tree tree = parser.parse("");
            assertEquals("EOS\n", tree.toString(FormatType.FORMAT_LATTICE));
        }
    }

    @Test
    public void parseToChunk() throws IOException {
        for (int inputLayer : INPUT_LAYERS) {
            Param param = new Param();
            param.loadConfig(configPath);
            param.set(Param.INPUT_LAYER, inputLayer);
            param.set(Param.OUTPUT_LAYER, Constant.CABOCHA_OUTPUT_CHUNK);
            Parser parser = new Parser(param);
            parser.open();
            Tree tree = parser.parse("");
            assertEquals("EOS\n", tree.toString(FormatType.FORMAT_LATTICE));
        }
    }

    @Test
    public void parseToSelection() throws IOException {
        for (int inputLayer : INPUT_LAYERS) {
            Param param = new Param();
            param.loadConfig(configPath);
            param.set(Param.INPUT_LAYER, inputLayer);
            param.set(Param.OUTPUT_LAYER, Constant.CABOCHA_OUTPUT_SELECTION);
            Parser parser = new Parser(param);
            parser.open();
            Tree tree = parser.parse("");
            assertEquals("EOS\n", tree.toString(FormatType.FORMAT_LATTICE));
        }
    }

    @Test
    public void parseToDep() throws IOException {
        for (int inputLayer : INPUT_LAYERS) {
            Param param = new Param();
            param.loadConfig(configPath);
            param.set(Param.INPUT_LAYER, inputLayer);
            param.set(Param.OUTPUT_LAYER, Constant.CABOCHA_OUTPUT_DEP);
            Parser parser = new Parser(param);
            parser.open();
            Tree tree = parser.parse("");
            assertEquals("EOS\n", tree.toString(FormatType.FORMAT_LATTICE));
        }
    }

    @Test
    public void parseToString() throws IOException {
        Param param = new Param();
        param.loadConfig(configPath);
        Parser parser = new Parser(param);
        parser.open();
        assertEquals("EOS\n", parser.parseToString(""));
    }
}