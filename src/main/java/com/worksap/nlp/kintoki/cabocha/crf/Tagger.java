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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tagger {

    private static Map<String, FeatureIndex> featureIndexCache = new HashMap<>();

    private int ysize;
    private int featureId;
    private FeatureIndex featureIndex;
    private List<List<String>> x;
    private List<List<Node>> lattice;
    private List<Integer> result;
    private List<List<Integer>> featureCache;

    private Tagger(FeatureIndex featureIndex) {
        this.featureIndex = featureIndex;
        ysize = featureIndex.ysize();
        featureId = 0;
        x = new ArrayList<>();
        lattice = new ArrayList<>();
        result = new ArrayList<>();
        featureCache = new ArrayList<>();
    }

    public static Tagger openBinaryModel(String path, double costFactor) throws IOException {
        if (costFactor <= 0.0) {
            throw new IllegalArgumentException("cost factor must be positive");
        }
        FeatureIndex featureIndex;
        synchronized(featureIndexCache) {
            featureIndex = featureIndexCache.get(path);
            if (featureIndex == null) {
                featureIndex = DecoderFeatureIndex.openBinaryModel(path);
                featureIndexCache.put(path, featureIndex);
            }
        }
        featureIndex.setCostFactor(costFactor);
        return new Tagger(featureIndex);
    }

    private void viterbi() {
        double bestc = Double.NEGATIVE_INFINITY;
        Node best = null;
        for (Node node : lattice.get(x.size() - 1)) {
            if (bestc < node.bestCost) {
                best = node;
                bestc = node.bestCost;
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

        for (int position = 0; position < lattice.size(); position++) {
            for (Node rNode : lattice.get(position)) {
                double rNodeCost = featureIndex.calcCost(rNode);
                if (position == 0) {
                    rNode.bestCost = rNodeCost;
                } else {
                    connectNodes(position, rNode, rNodeCost);
                }
            }
        }
    }

    private void connectNodes(int position, Node rNode, double rNodeCost) {
        rNode.bestCost = Double.NEGATIVE_INFINITY;
        for (int ly = 0; ly < ysize; ly++) {
            Node lNode = node(position - 1, ly);
            double c = lNode.bestCost + featureIndex.calcPathCost(lNode, rNode) + rNodeCost;
            if (c > rNode.bestCost) {
                rNode.bestCost = c;
                rNode.prev = lNode;
            }
        }
    }

    Node node(int i, int j) {
        return lattice.get(i).get(j);
    }

    void setNode(Node n, int i, int j) {
        lattice.get(i).set(j, n);
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
        lattice.add(l);
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
        lattice.clear();
        result.clear();
        featureCache.clear();
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

    public List<String> ynames() {
        return Collections.unmodifiableList(featureIndex.getY());
    }

    String x(int i, int j) {
        return x.get(i).get(j);
    }
}
