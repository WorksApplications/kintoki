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


public class CabochaTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();
    String configPath;
    String inputFile;
    String outputFile;

    @Before
    public void setUp() throws IOException {
        Utils.copyResources(temporaryFolder.getRoot().toPath());
        configPath = Utils.buildConfig(temporaryFolder.getRoot().toPath());
        inputFile = Utils.getInput(temporaryFolder.getRoot().toPath());
        outputFile = Utils.getOutput(temporaryFolder.getRoot().toPath());
    }

    @Test
    public void testCabocha() throws IOException {
        final String sent = "太郎は花子が読んでいる本を次郎に渡した。";

        Cabocha cabocha = new Cabocha(configPath);
        Tree tree = cabocha.parse(sent);

        assertNotNull(tree.getChunks());
        assertEquals(6, tree.getChunkSize());

        Chunk chunk = tree.chunk(0);
        assertEquals(5, chunk.getLink());
        assertEquals(0, chunk.getHeadPos());
        assertEquals(1, chunk.getFuncPos());
        assertEquals(2, chunk.getTokenSize());
        assertEquals("太郎", chunk.token(0).getSurface());
        assertEquals("名詞,固有名詞,人名,名,*,*", chunk.token(0).getFeature());
        assertEquals("は", chunk.token(1).getSurface());
        assertEquals("助詞,係助詞,*,*,*,*", chunk.token(1).getFeature());

        chunk = tree.chunk(1);
        assertEquals(2, chunk.getLink());
        assertEquals(0, chunk.getHeadPos());
        assertEquals(1, chunk.getFuncPos());
        assertEquals(2, chunk.getTokenSize());
        assertEquals("花子", chunk.token(0).getSurface());
        assertEquals("名詞,固有名詞,人名,名,*,*", chunk.token(0).getFeature());
        assertEquals("が", chunk.token(1).getSurface());
        assertEquals("助詞,格助詞,*,*,*,*", chunk.token(1).getFeature());

        chunk = tree.chunk(2);
        assertEquals(3, chunk.getLink());
        assertEquals(2, chunk.getHeadPos());
        assertEquals(2, chunk.getFuncPos());
        assertEquals(3, chunk.getTokenSize());
        assertEquals("読ん", chunk.token(0).getSurface());
        assertEquals("動詞,一般,*,*,五段-マ行,連用形-撥音便", chunk.token(0).getFeature());
        assertEquals("で", chunk.token(1).getSurface());
        assertEquals("助詞,接続助詞,*,*,*,*", chunk.token(1).getFeature());
        assertEquals("いる", chunk.token(2).getSurface());
        assertEquals("動詞,非自立可能,*,*,上一段-ア行,連体形-一般", chunk.token(2).getFeature());

        chunk = tree.chunk(3);
        assertEquals(5, chunk.getLink());
        assertEquals(0, chunk.getHeadPos());
        assertEquals(1, chunk.getFuncPos());
        assertEquals(2, chunk.getTokenSize());
        assertEquals("本", chunk.token(0).getSurface());
        assertEquals("名詞,普通名詞,一般,*,*,*", chunk.token(0).getFeature());
        assertEquals("を", chunk.token(1).getSurface());
        assertEquals("助詞,格助詞,*,*,*,*", chunk.token(1).getFeature());

        chunk = tree.chunk(4);
        assertEquals(5, chunk.getLink());
        assertEquals(0, chunk.getHeadPos());
        assertEquals(1, chunk.getFuncPos());
        assertEquals(2, chunk.getTokenSize());
        assertEquals("次郎", chunk.token(0).getSurface());
        assertEquals("名詞,固有名詞,人名,名,*,*", chunk.token(0).getFeature());
        assertEquals("に", chunk.token(1).getSurface());
        assertEquals("助詞,格助詞,*,*,*,*", chunk.token(1).getFeature());

        chunk = tree.chunk(5);
        assertEquals(-1, chunk.getLink());
        assertEquals(0, chunk.getHeadPos());
        assertEquals(1, chunk.getFuncPos());
        assertEquals(3, chunk.getTokenSize());
        assertEquals("渡し", chunk.token(0).getSurface());
        assertEquals("動詞,一般,*,*,五段-サ行,連用形-一般", chunk.token(0).getFeature());
        assertEquals("た", chunk.token(1).getSurface());
        assertEquals("助動詞,*,*,*,助動詞-タ,終止形-一般", chunk.token(1).getFeature());
        assertEquals("。", chunk.token(2).getSurface());
        assertEquals("補助記号,句点,*,*,*,*", chunk.token(2).getFeature());
    }

    public void testCabochaWithTree() throws IOException {
        Tree input = new Tree();
        input.setSentence("太郎は花子が読んでいる本を次郎に渡した。");
        input.read(MockMorpheme.getExampleList());

        Param param = new Param();
        param.loadConfig(configPath);
        param.set(Param.INPUT_LAYER, Constant.CABOCHA_INPUT_POS);
        Parser parser = new Parser(param);
        Tree tree = parser.parse(input);

        assertNotNull(tree.getChunks());
        assertEquals(6, tree.getChunkSize());

        Chunk chunk = tree.chunk(0);
        assertEquals(5, chunk.getLink());
        assertEquals(0, chunk.getHeadPos());
        assertEquals(1, chunk.getFuncPos());
        assertEquals(2, chunk.getTokenSize());
        assertEquals("太郎", chunk.token(0).getSurface());
        assertEquals("名詞,固有名詞,人名,名,*,*", chunk.token(0).getFeature());
        assertEquals("は", chunk.token(1).getSurface());
        assertEquals("助詞,係助詞,*,*,*,*", chunk.token(1).getFeature());

        chunk = tree.chunk(1);
        assertEquals(2, chunk.getLink());
        assertEquals(0, chunk.getHeadPos());
        assertEquals(1, chunk.getFuncPos());
        assertEquals(2, chunk.getTokenSize());
        assertEquals("花子", chunk.token(0).getSurface());
        assertEquals("名詞,固有名詞,人名,名,*,*", chunk.token(0).getFeature());
        assertEquals("が", chunk.token(1).getSurface());
        assertEquals("助詞,格助詞,*,*,*,*", chunk.token(1).getFeature());

        chunk = tree.chunk(2);
        assertEquals(3, chunk.getLink());
        assertEquals(2, chunk.getHeadPos());
        assertEquals(2, chunk.getFuncPos());
        assertEquals(3, chunk.getTokenSize());
        assertEquals("読ん", chunk.token(0).getSurface());
        assertEquals("動詞,一般,*,*,五段-マ行,連用形-撥音便", chunk.token(0).getFeature());
        assertEquals("で", chunk.token(1).getSurface());
        assertEquals("助詞,接続助詞,*,*,*,*", chunk.token(1).getFeature());
        assertEquals("いる", chunk.token(2).getSurface());
        assertEquals("動詞,非自立可能,*,*,上一段-ア行,連体形-一般", chunk.token(2).getFeature());

        chunk = tree.chunk(3);
        assertEquals(5, chunk.getLink());
        assertEquals(0, chunk.getHeadPos());
        assertEquals(1, chunk.getFuncPos());
        assertEquals(2, chunk.getTokenSize());
        assertEquals("本", chunk.token(0).getSurface());
        assertEquals("名詞,普通名詞,一般,*,*,*", chunk.token(0).getFeature());
        assertEquals("を", chunk.token(1).getSurface());
        assertEquals("助詞,格助詞,*,*,*,*", chunk.token(1).getFeature());

        chunk = tree.chunk(4);
        assertEquals(5, chunk.getLink());
        assertEquals(0, chunk.getHeadPos());
        assertEquals(1, chunk.getFuncPos());
        assertEquals(2, chunk.getTokenSize());
        assertEquals("次郎", chunk.token(0).getSurface());
        assertEquals("名詞,固有名詞,人名,名,*,*", chunk.token(0).getFeature());
        assertEquals("に", chunk.token(1).getSurface());
        assertEquals("助詞,格助詞,*,*,*,*", chunk.token(1).getFeature());

        chunk = tree.chunk(5);
        assertEquals(-1, chunk.getLink());
        assertEquals(0, chunk.getHeadPos());
        assertEquals(1, chunk.getFuncPos());
        assertEquals(3, chunk.getTokenSize());
        assertEquals("渡し", chunk.token(0).getSurface());
        assertEquals("動詞,一般,*,*,五段-サ行,連用形-一般", chunk.token(0).getFeature());
        assertEquals("た", chunk.token(1).getSurface());
        assertEquals("助動詞,*,*,*,助動詞-タ,終止形-一般", chunk.token(1).getFeature());
        assertEquals("。", chunk.token(2).getSurface());
        assertEquals("補助記号,句点,*,*,*,*", chunk.token(2).getFeature());
    }

    @Test
    public void testCabochaWithMultiDifferentSentences() throws IOException {
        final String[] sents = new String[3];
        sents[0] = "太郎は花子が読んでいる本を次郎に渡した。";
        sents[1] = "花子は本を太郎に渡した。";
        sents[2] = "太郎が次郎に本を渡した。";

        Cabocha cabocha = new Cabocha(configPath);

        Tree tree1 = cabocha.parse(sents[0]);

        assertNotNull(tree1.getChunks());
        assertEquals(6, tree1.getChunkSize());

        Chunk chunk = tree1.chunk(0);
        assertEquals(5, chunk.getLink());
        assertEquals(0, chunk.getHeadPos());
        assertEquals(1, chunk.getFuncPos());
        assertEquals("-0.733887", String.format("%.6f", chunk.getScore()));
        assertEquals(2, chunk.getTokenSize());
        assertEquals("太郎", chunk.token(0).getSurface());
        assertEquals("太郎", chunk.token(0).getNormalizedSurface());
        assertEquals("名詞,固有名詞,人名,名,*,*", chunk.token(0).getFeature());
        assertEquals("は", chunk.token(1).getSurface());
        assertEquals("は", chunk.token(1).getNormalizedSurface());
        assertEquals("助詞,係助詞,*,*,*,*", chunk.token(1).getFeature());

        chunk = tree1.chunk(1);
        assertEquals(2, chunk.getLink());
        assertEquals(0, chunk.getHeadPos());
        assertEquals(1, chunk.getFuncPos());
        assertEquals("0.879670", String.format("%.6f", chunk.getScore()));
        assertEquals(2, chunk.getTokenSize());
        assertEquals("花子", chunk.token(0).getSurface());
        assertEquals("花子", chunk.token(0).getNormalizedSurface());
        assertEquals("名詞,固有名詞,人名,名,*,*", chunk.token(0).getFeature());
        assertEquals("が", chunk.token(1).getSurface());
        assertEquals("が", chunk.token(1).getNormalizedSurface());
        assertEquals("助詞,格助詞,*,*,*,*", chunk.token(1).getFeature());

        chunk = tree1.chunk(2);
        assertEquals(3, chunk.getLink());
        assertEquals(2, chunk.getHeadPos());
        assertEquals(2, chunk.getFuncPos());
        assertEquals("0.716005", String.format("%.6f", chunk.getScore()));
        assertEquals(3, chunk.getTokenSize());
        assertEquals("読ん", chunk.token(0).getSurface());
        assertEquals("読む", chunk.token(0).getNormalizedSurface());
        assertEquals("動詞,一般,*,*,五段-マ行,連用形-撥音便", chunk.token(0).getFeature());
        assertEquals("で", chunk.token(1).getSurface());
        assertEquals("で", chunk.token(1).getNormalizedSurface());
        assertEquals("助詞,接続助詞,*,*,*,*", chunk.token(1).getFeature());
        assertEquals("いる", chunk.token(2).getSurface());
        assertEquals("居る", chunk.token(2).getNormalizedSurface());
        assertEquals("動詞,非自立可能,*,*,上一段-ア行,連体形-一般", chunk.token(2).getFeature());

        chunk = tree1.chunk(3);
        assertEquals(5, chunk.getLink());
        assertEquals(0, chunk.getHeadPos());
        assertEquals(1, chunk.getFuncPos());
        assertEquals("-0.733887", String.format("%.6f", chunk.getScore()));
        assertEquals(2, chunk.getTokenSize());
        assertEquals("本", chunk.token(0).getSurface());
        assertEquals("本", chunk.token(0).getNormalizedSurface());
        assertEquals("名詞,普通名詞,一般,*,*,*", chunk.token(0).getFeature());
        assertEquals("を", chunk.token(1).getSurface());
        assertEquals("を", chunk.token(1).getNormalizedSurface());
        assertEquals("助詞,格助詞,*,*,*,*", chunk.token(1).getFeature());

        chunk = tree1.chunk(4);
        assertEquals(5, chunk.getLink());
        assertEquals(0, chunk.getHeadPos());
        assertEquals(1, chunk.getFuncPos());
        assertEquals("-0.733887", String.format("%.6f", chunk.getScore()));
        assertEquals(2, chunk.getTokenSize());
        assertEquals("次郎", chunk.token(0).getSurface());
        assertEquals("次郎", chunk.token(0).getNormalizedSurface());
        assertEquals("名詞,固有名詞,人名,名,*,*", chunk.token(0).getFeature());
        assertEquals("に", chunk.token(1).getSurface());
        assertEquals("に", chunk.token(1).getNormalizedSurface());
        assertEquals("助詞,格助詞,*,*,*,*", chunk.token(1).getFeature());

        chunk = tree1.chunk(5);
        assertEquals(-1, chunk.getLink());
        assertEquals(0, chunk.getHeadPos());
        assertEquals(1, chunk.getFuncPos());
        assertEquals("0.000000", String.format("%.6f", chunk.getScore()));
        assertEquals(3, chunk.getTokenSize());
        assertEquals("渡し", chunk.token(0).getSurface());
        assertEquals("渡す", chunk.token(0).getNormalizedSurface());
        assertEquals("動詞,一般,*,*,五段-サ行,連用形-一般", chunk.token(0).getFeature());
        assertEquals("た", chunk.token(1).getSurface());
        assertEquals("た", chunk.token(1).getNormalizedSurface());
        assertEquals("助動詞,*,*,*,助動詞-タ,終止形-一般", chunk.token(1).getFeature());
        assertEquals("。", chunk.token(2).getSurface());
        assertEquals("。", chunk.token(2).getNormalizedSurface());
        assertEquals("補助記号,句点,*,*,*,*", chunk.token(2).getFeature());

        Tree tree2 = cabocha.parse(sents[1]);

        assertNotNull(tree2.getChunks());
        assertEquals(4, tree2.getChunkSize());

        Chunk chunk2 = tree2.chunk(0);
        assertEquals(3, chunk2.getLink());
        assertEquals(0, chunk2.getHeadPos());
        assertEquals(1, chunk2.getFuncPos());
        assertEquals("0.000000", String.format("%.6f", chunk.getScore()));
        assertEquals(2, chunk2.getTokenSize());
        assertEquals("花子", chunk2.token(0).getSurface());
        assertEquals("花子", chunk2.token(0).getNormalizedSurface());
        assertEquals("名詞,固有名詞,人名,名,*,*", chunk2.token(0).getFeature());
        assertEquals("は", chunk2.token(1).getSurface());
        assertEquals("は", chunk2.token(1).getNormalizedSurface());
        assertEquals("助詞,係助詞,*,*,*,*", chunk2.token(1).getFeature());

        chunk2 = tree2.chunk(1);
        assertEquals(3, chunk2.getLink());
        assertEquals(0, chunk2.getHeadPos());
        assertEquals(1, chunk2.getFuncPos());
        assertEquals("0.000000", String.format("%.6f", chunk.getScore()));
        assertEquals(2, chunk2.getTokenSize());
        assertEquals("本", chunk2.token(0).getSurface());
        assertEquals("本", chunk2.token(0).getNormalizedSurface());
        assertEquals("名詞,普通名詞,一般,*,*,*", chunk2.token(0).getFeature());
        assertEquals("を", chunk2.token(1).getSurface());
        assertEquals("を", chunk2.token(1).getNormalizedSurface());
        assertEquals("助詞,格助詞,*,*,*,*", chunk2.token(1).getFeature());

        chunk2 = tree2.chunk(2);
        assertEquals(3, chunk2.getLink());
        assertEquals(0, chunk2.getHeadPos());
        assertEquals(1, chunk2.getFuncPos());
        assertEquals("0.000000", String.format("%.6f", chunk.getScore()));
        assertEquals(2, chunk2.getTokenSize());
        assertEquals("太郎", chunk2.token(0).getSurface());
        assertEquals("太郎", chunk2.token(0).getNormalizedSurface());
        assertEquals("名詞,固有名詞,人名,名,*,*", chunk2.token(0).getFeature());
        assertEquals("に", chunk2.token(1).getSurface());
        assertEquals("に", chunk2.token(1).getNormalizedSurface());
        assertEquals("助詞,格助詞,*,*,*,*", chunk2.token(1).getFeature());

        chunk2 = tree2.chunk(3);
        assertEquals(-1, chunk2.getLink());
        assertEquals(0, chunk2.getHeadPos());
        assertEquals(1, chunk2.getFuncPos());
        assertEquals("0.000000", String.format("%.6f", chunk.getScore()));
        assertEquals(3, chunk2.getTokenSize());
        assertEquals("渡し", chunk2.token(0).getSurface());
        assertEquals("渡す", chunk2.token(0).getNormalizedSurface());
        assertEquals("動詞,一般,*,*,五段-サ行,連用形-一般", chunk2.token(0).getFeature());
        assertEquals("た", chunk2.token(1).getSurface());
        assertEquals("た", chunk2.token(1).getNormalizedSurface());
        assertEquals("助動詞,*,*,*,助動詞-タ,終止形-一般", chunk2.token(1).getFeature());
        assertEquals("。", chunk2.token(2).getSurface());
        assertEquals("。", chunk2.token(2).getNormalizedSurface());
        assertEquals("補助記号,句点,*,*,*,*", chunk2.token(2).getFeature());

        Tree tree3 = cabocha.parse(sents[2]);

        assertNotNull(tree3.getChunks());
        assertEquals(4, tree3.getChunkSize());

        Chunk chunk3 = tree3.chunk(0);
        assertEquals(3, chunk3.getLink());
        assertEquals(0, chunk3.getHeadPos());
        assertEquals(1, chunk3.getFuncPos());
        assertEquals("0.000000", String.format("%.6f", chunk.getScore()));
        assertEquals(2, chunk3.getTokenSize());
        assertEquals("-0.102446", String.format("%.6f", chunk3.getScore()));
        assertEquals("太郎", chunk3.token(0).getSurface());
        assertEquals("太郎", chunk3.token(0).getNormalizedSurface());
        assertEquals("名詞,固有名詞,人名,名,*,*", chunk3.token(0).getFeature());
        assertEquals("が", chunk3.token(1).getSurface());
        assertEquals("が", chunk3.token(1).getNormalizedSurface());
        assertEquals("助詞,格助詞,*,*,*,*", chunk3.token(1).getFeature());

        chunk3 = tree3.chunk(1);
        assertEquals(2, chunk3.getLink());
        assertEquals(0, chunk3.getHeadPos());
        assertEquals(1, chunk3.getFuncPos());
        assertEquals("0.277227", String.format("%.6f", chunk3.getScore()));
        assertEquals(2, chunk3.getTokenSize());
        assertEquals("次郎", chunk3.token(0).getSurface());
        assertEquals("次郎", chunk3.token(0).getNormalizedSurface());
        assertEquals("名詞,固有名詞,人名,名,*,*", chunk3.token(0).getFeature());
        assertEquals("に", chunk3.token(1).getSurface());
        assertEquals("に", chunk3.token(1).getNormalizedSurface());
        assertEquals("助詞,格助詞,*,*,*,*", chunk3.token(1).getFeature());

        chunk3 = tree3.chunk(2);
        assertEquals(3, chunk3.getLink());
        assertEquals(0, chunk3.getHeadPos());
        assertEquals(1, chunk3.getFuncPos());
        assertEquals("-0.102446", String.format("%.6f", chunk3.getScore()));
        assertEquals(2, chunk3.getTokenSize());
        assertEquals("本", chunk3.token(0).getSurface());
        assertEquals("本", chunk3.token(0).getNormalizedSurface());
        assertEquals("名詞,普通名詞,一般,*,*,*", chunk3.token(0).getFeature());
        assertEquals("を", chunk3.token(1).getSurface());
        assertEquals("を", chunk3.token(1).getNormalizedSurface());
        assertEquals("助詞,格助詞,*,*,*,*", chunk3.token(1).getFeature());

        chunk3 = tree3.chunk(3);
        assertEquals(-1, chunk3.getLink());
        assertEquals(0, chunk3.getHeadPos());
        assertEquals(1, chunk3.getFuncPos());
        assertEquals("0.000000", String.format("%.6f", chunk3.getScore()));
        assertEquals(3, chunk3.getTokenSize());
        assertEquals("渡し", chunk3.token(0).getSurface());
        assertEquals("渡す", chunk3.token(0).getNormalizedSurface());
        assertEquals("動詞,一般,*,*,五段-サ行,連用形-一般", chunk3.token(0).getFeature());
        assertEquals("た", chunk3.token(1).getSurface());
        assertEquals("た", chunk3.token(1).getNormalizedSurface());
        assertEquals("助動詞,*,*,*,助動詞-タ,終止形-一般", chunk3.token(1).getFeature());
        assertEquals("。", chunk3.token(2).getSurface());
        assertEquals("。", chunk3.token(2).getNormalizedSurface());
        assertEquals("補助記号,句点,*,*,*,*", chunk3.token(2).getFeature());
    }

    @Test
    public void testCabochaWithParam() throws IOException {
        Param param = new Param();
        param.loadConfig(configPath);
        Cabocha cabocha = new Cabocha(param);
        Tree tree = cabocha.parse("太郎は花子が読んでいる本を次郎に渡した。");

        assertNotNull(tree.getChunks());
        assertEquals(6, tree.getChunkSize());
    }

    @Test
    public void testMain() throws IOException {
        String[] args = {inputFile,
                "-r", configPath,
                "-o", outputFile,
                "-I2", "-O4",
                "-f2"
        };

        Cabocha.main(args);
    }

}
