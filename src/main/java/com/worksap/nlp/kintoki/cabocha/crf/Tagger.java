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

import java.io.IOException;
import java.util.*;

public class Tagger {

    private int ysize;
    private double z;
    private int featureId;
    private FeatureIndex featureIndex;
    private List<List<String>> x;
    private List<List<Node>> node;
    private List<Integer> result;
    private List<List<Double>> penalty;
    private List<List<Integer>> featureCache;

    private Tagger(FeatureIndex featureIndex) {
        this.featureIndex = featureIndex;
        ysize = featureIndex.ysize();
        z = 0;
        featureId = 0;
        x = new ArrayList<>();
        node = new ArrayList<>();
        result = new ArrayList<>();
        penalty = new ArrayList<>();
        featureCache = new ArrayList<>();
    }

    public static Tagger openBinaryModel(String path, double costFactor) throws IOException {
        if (costFactor <= 0.0) {
            throw new IllegalArgumentException("cost factor must be positive");
        }
        FeatureIndex featureIndex = DecoderFeatureIndex.openBinaryModel(path);
        featureIndex.setCostFactor(costFactor);
        Tagger tagger = new Tagger(featureIndex);
        return tagger;
    }

    private void viterbi() {
        for (int i = 0; i < x.size(); i++) {
            for (int j = 0; j < ysize; j++) {
                double bestc = -1e37;
                Node best = null;
                List<Path> lpath = node.get(i).get(j).lpath;
                for (Path p : lpath) {
                    double c = p.lnode.bestCost + p.cost + node.get(i).get(j).cost;
                    if (c > bestc) {
                        bestc = c;
                        best = p.lnode;
                    }
                }
                node.get(i).get(j).prev = best;
                node.get(i).get(j).bestCost = best != null ? bestc : node.get(i).get(j).cost;
            }
        }

        double bestc = -1e37;
        Node best = null;
        int s = x.size() - 1;
        for (int j = 0; j < ysize; j++) {
            if (bestc < node.get(s).get(j).bestCost) {
                best = node.get(s).get(j);
                bestc = node.get(s).get(j).bestCost;
            }
        }

        for (Node n = best; n != null; n = n.prev) {
            result.set(n.x, n.y);
        }
    }

    private void buildLattice() {
        if (x.isEmpty()) {
            return;
        }

        featureIndex.rebuildFeatures(this);

        for (int i = 0; i < x.size(); i++) {
            for (int j = 0; j < ysize; j++) {
                featureIndex.calcCost(node.get(i).get(j));
                List<Path> lpath = node.get(i).get(j).lpath;
                for (Path p : lpath) {
                    featureIndex.calcCost(p);
                }
            }
        }

        // Add penalty for Dual decomposition.
        if (!penalty.isEmpty()) {
            for (int i = 0; i < x.size(); i++) {
                for (int j = 0; j < ysize; j++) {
                    node.get(i).get(j).cost += penalty.get(i).get(j);
                }
            }
        }
    }

    Node node(int i, int j) {
        return node.get(i).get(j);
    }

    void setNode(Node n, int i, int j) {
        node.get(i).set(j, n);
    }

    public void add(String... columns) {
        int xsize = featureIndex.getXsize();
        if (columns.length < xsize) {
            throw new IllegalArgumentException("# x is small: size=" + columns.length + " xsize=" + xsize);
        }
        List<String> tmpX = Arrays.asList(columns);
        x.add(tmpX);
        result.add(0);
        List<Node> l = Arrays.asList(new Node[ysize]);
        node.add(l);
    }

    public void parse() {
        featureIndex.buildFeatures(this);
        if (x.isEmpty()) {
            return;
        }
        buildLattice();
        viterbi();
    }

    public void clear() {
        x.clear();
        node.clear();
        result.clear();
        featureCache.clear();
        z = 0.0;
    }

    int getFeatureId() {
        return featureId;
    }

    void setFeatureId(int featureId) {
        this.featureId = featureId;
    }

    List<List<Integer>> getFeatureCache() {
        return featureCache;
    }

    int size() {
        return x.size();
    }

    int xsize() {
        return featureIndex.getXsize();
    }

    public int y(int i) {
        return result.get(i);
    }

    public String yname(int i) {
        return featureIndex.getY().get(i);
    }

    String x(int i, int j) {
        return x.get(i).get(j);
    }
}
