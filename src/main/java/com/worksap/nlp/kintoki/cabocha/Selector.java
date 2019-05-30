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

import com.worksap.nlp.kintoki.cabocha.util.PropertyUtil;
import com.worksap.nlp.kintoki.cabocha.util.Utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Selector extends Analyzer {

    private PatternMatcher patKutouten, patOpenBracket, patCloseBracket;
    private PatternMatcher patDynA, patCase;
    private PatternMatcher patUnidicFunc, patUnidicHead, patUnidicFunc2, patUnidicHead2, patUnidicHeadPre;

    public Selector() {
        super();
        this.patKutouten = new PatternMatcher();
        this.patOpenBracket = new PatternMatcher();
        this.patCloseBracket = new PatternMatcher();
        this.patDynA = new PatternMatcher();
        this.patCase = new PatternMatcher();
        this.patUnidicFunc = new PatternMatcher();
        this.patUnidicHead = new PatternMatcher();
        this.patUnidicFunc2 = new PatternMatcher();
        this.patUnidicHead2 = new PatternMatcher();
        this.patUnidicHeadPre = new PatternMatcher();
    }

    @Override
    public void open(Param param) throws IOException {
        Map<String, String> patternMap = new HashMap<>();
        PropertyUtil.getPatterns(patternMap);
        this.patKutouten.compile(patternMap.get("KUTOUTEN_PAT"));
        this.patOpenBracket.compile(patternMap.get("OPEN_BRACKET_PAT"));
        this.patCloseBracket.compile(patternMap.get("CLOSE_BRACKET_PAT"));
        this.patDynA.compile(patternMap.get("DYN_A_PAT"));
        this.patCase.compile(patternMap.get("CASE_PAT"));
        this.patUnidicFunc.compile(patternMap.get("UNIDIC_FUNC_PAT"));
        this.patUnidicHead.compile(patternMap.get("UNIDIC_HEAD_PAT"));
        this.patUnidicFunc2.compile(patternMap.get("UNIDIC_FUNC_PAT2"));
        this.patUnidicHead2.compile(patternMap.get("UNIDIC_HEAD_PAT2"));
        this.patUnidicHeadPre.compile(patternMap.get("UNIDIC_HEAD_PRE_PAT"));
    }

    private void findHead(Chunk chunk, Ref<Integer> headIndex, Ref<Integer> funcIndex) {
        headIndex.set(0);
        funcIndex.set(0);
        int tokenSize = chunk.getTokenSize();
        PatternMatcher funcMatcher = null;
        PatternMatcher headMatcher = null;

        headMatcher = this.patUnidicHead2;
        funcMatcher = this.patUnidicFunc2;

        for (int i = 0; i < tokenSize; ++i) {
            Token token = chunk.token(i);
            if (funcMatcher.prefixMatch(token.getFeature())) {
                funcIndex.set(i);
            }
            if (headMatcher.prefixMatch(token.getFeature())) {
                headIndex.set(i);
            }
        }

        if (headIndex.get() > funcIndex.get()) {
            funcIndex.set(headIndex.get());
        }
    }

    @Override
    public void parse(Tree tree) {
        int chunkSize = tree.getChunkSize();
        int posSize = 2;

        for (int i = 0; i < chunkSize; i++) {
            Chunk chunk = tree.chunk(i);
            int tokenSize = chunk.getTokenSize();
            for (int j = 0; j < tokenSize; j++) {
                Token token = chunk.token(j);
                if (this.patKutouten.match(token.getNormalizedSurface())) {
                    chunk.getFeatureList().add("GPUNC:" + token.getNormalizedSurface());
                    chunk.getFeatureList().add("FPUNC:" + token.getNormalizedSurface());
                }
                if (this.patOpenBracket.match(token.getNormalizedSurface())) {
                    chunk.getFeatureList().add("GOB:" + token.getNormalizedSurface());
                    chunk.getFeatureList().add("FOB:" + token.getNormalizedSurface());
                    chunk.getFeatureList().add("GOB:1");
                    chunk.getFeatureList().add("FOB:1");
                }
                if (this.patCloseBracket.match(token.getNormalizedSurface())) {
                    chunk.getFeatureList().add("GCB:" + token.getNormalizedSurface());
                    chunk.getFeatureList().add("FCB:" + token.getNormalizedSurface());
                    chunk.getFeatureList().add("GCB:1");
                    chunk.getFeatureList().add("FCB:1");
                }
                if (this.patCase.prefixMatch(token.getFeature())) {
                    chunk.getFeatureList().add("FCASE:" + token.getNormalizedSurface());
                }
            }

            Ref<Integer> headIndex = new Ref<>(0);
            Ref<Integer> funcIndex = new Ref<>(0);
            findHead(chunk, headIndex, funcIndex);

            chunk.setHeadPos(headIndex.get());
            chunk.setFuncPos(funcIndex.get());

            Token htoken = chunk.token(headIndex.get());
            Token ftoken = chunk.token(funcIndex.get());
            Token ltoken = chunk.token(0);
            Token rtoken = chunk.token(tokenSize - 1);

            // static features
            emitTokenFeatures("FH", htoken, posSize, chunk.getFeatureList());
            emitTokenFeatures("FF", ftoken, posSize, chunk.getFeatureList());
            emitTokenFeatures("FL", ltoken, posSize, chunk.getFeatureList());
            emitTokenFeatures("FR", rtoken, posSize, chunk.getFeatureList());

            // context features
            chunk.getFeatureList().add("LF:" + ftoken.getNormalizedSurface());
            chunk.getFeatureList().add("RL:" + ltoken.getNormalizedSurface());
            chunk.getFeatureList().add("RH:" + htoken.getNormalizedSurface());
            chunk.getFeatureList().add("RF:" + ftoken.getNormalizedSurface());

            if (i == 0) {
                chunk.getFeatureList().add("FBOS:1");
            }
            if (i == chunkSize - 1) {
                chunk.getFeatureList().add("FEOS:1");
            }

            if (this.patCase.prefixMatch(ftoken.getFeature())) {
                chunk.getFeatureList().add("GCASE:" + ftoken.getNormalizedSurface());
            }

            // dynamic features
            String fcform = getToken(ftoken, posSize + 1);
            if (this.patDynA.prefixMatch(ftoken.getFeature())) {
                chunk.getFeatureList().add("A:" + ftoken.getNormalizedSurface());
            } else if (fcform != null) {
                chunk.getFeatureList().add("A:" + fcform);
            } else {
                String output = Utils.concatFeature(ftoken, posSize);
                chunk.getFeatureList().add("A:" + output);
            }
        }

        tree.setOutputLayer(OutputLayerType.OUTPUT_SELECTION);
    }

    private String getToken(Token token, int index) {
        if (token.getFeatureListSize() <= index) {
            return null;
        }
        if (("*").equals(token.getFeatureList().get(index))) {
            return null;
        }
        return token.getFeatureList().get(index);
    }

    private void emitTokenFeatures(String header, Token token, int posSize, List<String> featureList) {
        String surface = token.getNormalizedSurface();
        String cform = getToken(token, posSize + 1);

        featureList.add(header + "S:" + surface);

        int size = Math.min(posSize, token.getFeatureListSize());

        for (int k = 0; k < size; ++k) {
            if (("*").equals(token.getFeatureList().get(k))) {
                break;
            }
            featureList.add(header + "P" + k + ":" + token.getFeatureList().get(k));
        }
        if (cform != null) {
            featureList.add(header + "F:" + cform);
        }
    }

}
