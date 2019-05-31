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

import java.util.ArrayList;
import java.util.List;

public class Chunk {

    private int link;
    private int headPos;
    private int funcPos;
    private int tokenPos;
    private List<Token> tokens = new ArrayList<>();
    private double score;
    private List<String> featureList = new ArrayList<>();

    public int getLink() {
        return link;
    }

    public void setLink(int link) {
        this.link = link;
    }

    public int getHeadPos() {
        return headPos;
    }

    public void setHeadPos(int headPos) {
        this.headPos = headPos;
    }

    public int getFuncPos() {
        return funcPos;
    }

    public void setFuncPos(int funcPos) {
        this.funcPos = funcPos;
    }

    public int getTokenSize() {
        return tokens.size();
    }

    public boolean isEmpty() {
        return tokens.isEmpty();
    }

    public Token token(int index) {
        return tokens.get(index);
    }

    public List<Token> getTokens() {
        return tokens;
    }

    public void setTokens(List<Token> tokens) {
        this.tokens = tokens;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public List<String> getFeatureList() {
        return featureList;
    }

    public void setFeatureList(List<String> featureList) {
        this.featureList = featureList;
    }

    public int getFeatureListSize() {
        return this.featureList.size();
    }

    public int getTokenPos() {
        return tokenPos;
    }

    public void setTokenPos(int tokenPos) {
        this.tokenPos = tokenPos;
    }
}
