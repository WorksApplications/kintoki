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

import com.worksap.nlp.kintoki.cabocha.model.FastSVMModel;
import com.worksap.nlp.kintoki.cabocha.model.SVMModelFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DependencyParserTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    Tree tree = new Tree();
    Param param = new Param();

    @Before
    public void prepare() throws IOException {
        Utils.copyResources(temporaryFolder.getRoot().toPath());
        String configPath = Utils.buildConfig(temporaryFolder.getRoot().toPath());
        param.loadConfig(configPath);

        List<Chunk> chunks = tree.getChunks();
        Chunk chunk = new Chunk();
        Token token = new Token();
        token.setSurface("太郎");
        token.setNormalizedSurface("太郎");
        token.setPos("名詞");
        token.setFeature("名詞,固有名詞,人名,名,*,*");
        token.setFeatureList(Arrays.asList(token.getFeature().split(",")));
        chunk.getTokens().add(token);
        token = new Token();
        token.setSurface("は");
        token.setNormalizedSurface("は");
        token.setPos("助詞");
        token.setFeature("助詞,係助詞,*,*,*,*");
        token.setFeatureList(Arrays.asList(token.getFeature().split(",")));
        chunk.getTokens().add(token);
        chunks.add(chunk);

        chunk = new Chunk();
        token = new Token();
        token.setSurface("花子");
        token.setNormalizedSurface("花子");
        token.setPos("名詞");
        token.setFeature("名詞,固有名詞,人名,名,*,*");
        token.setFeatureList(Arrays.asList(token.getFeature().split(",")));
        chunk.getTokens().add(token);
        token = new Token();
        token.setSurface("が");
        token.setNormalizedSurface("が");
        token.setPos("助詞");
        token.setFeature("助詞,格助詞,*,*,*,*");
        token.setFeatureList(Arrays.asList(token.getFeature().split(",")));
        chunk.getTokens().add(token);
        chunks.add(chunk);

        chunk = new Chunk();
        token = new Token();
        token.setSurface("読ん");
        token.setNormalizedSurface("読ん");
        token.setPos("動詞");
        token.setFeature("動詞,一般,*,*,五段-マ行,連用形-撥音便");
        token.setFeatureList(Arrays.asList(token.getFeature().split(",")));
        chunk.getTokens().add(token);
        token = new Token();
        token.setSurface("で");
        token.setNormalizedSurface("で");
        token.setPos("助詞");
        token.setFeature("助詞,接続助詞,*,*,*,*");
        token.setFeatureList(Arrays.asList(token.getFeature().split(",")));
        chunk.getTokens().add(token);
        token = new Token();
        token.setSurface("いる");
        token.setNormalizedSurface("いる");
        token.setPos("動詞");
        token.setFeature("動詞,非自立可能,*,*,上一段-ア行,連体形-一般");
        token.setFeatureList(Arrays.asList(token.getFeature().split(",")));
        chunk.getTokens().add(token);
        chunks.add(chunk);

        chunk = new Chunk();
        token = new Token();
        token.setSurface("本");
        token.setNormalizedSurface("本");
        token.setPos("名詞");
        token.setFeature("名詞,普通名詞,一般,*,*,*");
        token.setFeatureList(Arrays.asList(token.getFeature().split(",")));
        chunk.getTokens().add(token);
        token = new Token();
        token.setSurface("を");
        token.setNormalizedSurface("を");
        token.setPos("助詞");
        token.setFeature("助詞,格助詞,*,*,*,*");
        token.setFeatureList(Arrays.asList(token.getFeature().split(",")));
        chunk.getTokens().add(token);
        chunks.add(chunk);

        chunk = new Chunk();
        token = new Token();
        token.setSurface("次郎");
        token.setNormalizedSurface("次郎");
        token.setPos("名詞");
        token.setFeature("名詞,固有名詞,人名,名,*,*");
        token.setFeatureList(Arrays.asList(token.getFeature().split(",")));
        chunk.getTokens().add(token);
        token = new Token();
        token.setSurface("に");
        token.setNormalizedSurface("に");
        token.setPos("助詞");
        token.setFeature("助詞,格助詞,*,*,*,*");
        token.setFeatureList(Arrays.asList(token.getFeature().split(",")));
        chunk.getTokens().add(token);
        chunks.add(chunk);

        chunk = new Chunk();
        token = new Token();
        token.setSurface("渡し");
        token.setNormalizedSurface("渡し");
        token.setPos("動詞");
        token.setFeature("動詞,一般,*,*,五段-サ行,連用形-一般");
        token.setFeatureList(Arrays.asList(token.getFeature().split(",")));
        chunk.getTokens().add(token);
        token = new Token();
        token.setSurface("た");
        token.setNormalizedSurface("た");
        token.setPos("助動詞");
        token.setFeature("助動詞,*,*,*,助動詞-タ,終止形-一般");
        token.setFeatureList(Arrays.asList(token.getFeature().split(",")));
        chunk.getTokens().add(token);
        token = new Token();
        token.setSurface("。");
        token.setNormalizedSurface("。");
        token.setPos("補助記号");
        token.setFeature("補助記号,句点,*,*,*,*");
        token.setFeatureList(Arrays.asList(token.getFeature().split(",")));
        chunk.getTokens().add(token);
        chunks.add(chunk);

        Selector selector = new Selector();
        selector.open(null);
        selector.parse(tree);
    }

    @Test
    public void open() throws IOException {
        String modelFile = param.getString(Param.PARSER_MODEL);
        FastSVMModel svmModel = SVMModelFactory.loadModel(modelFile);
        assertNotNull(svmModel);
    }

    @Test
    public void parse() throws Exception {
        assertEquals(6, tree.getChunkSize());

        DependencyParser parser = new DependencyParser();
        parser.open(param);
        parser.parse(tree);
        assertEquals(5, tree.chunk(0).getLink());
        assertEquals(2, tree.chunk(1).getLink());
        assertEquals(3, tree.chunk(2).getLink());
        assertEquals(5, tree.chunk(3).getLink());
        assertEquals(5, tree.chunk(4).getLink());
        assertEquals(-1, tree.chunk(5).getLink());
    }

}
