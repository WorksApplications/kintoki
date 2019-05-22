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

    private int vlevel;
    private int nbest;
    private int ysize;
    private double cost;
    private double z;
    private int featureId;
    private int threadId;
    private static FeatureIndex featureIndex;
    private List<List<String>> x;
    private List<List<Node>> node;
    private List<Integer> answer;
    private List<Integer> result;
    private PriorityQueue<QueueElement> agenda;
    private List<List<Double>> penalty;
    private List<List<Integer>> featureCache;

    public Tagger() {
        vlevel = 0;
        nbest = 0;
        ysize = 0;
        z = 0;
        featureId = 0;
        threadId = 0;
        x = new ArrayList<List<String>>();
        node = new ArrayList<List<Node>>();
        answer = new ArrayList<Integer>();
        result = new ArrayList<Integer>();
        agenda = null;
        penalty = new ArrayList<List<Double>>();
        featureCache = new ArrayList<List<Integer>>();
    }

    public void forwardbackward() {
        if (x.isEmpty()) return;

        for (int i = 0; i < x.size(); i++) {
            for (int j = 0; j < ysize; j++) {
                node.get(i).get(j).calcAlpha();
            }
        }

        for (int i = x.size() - 1; i >= 0; i--) {
            for (int j = 0; j < ysize; j++) {
                node.get(i).get(j).calcBeta();
            }
        }

        z = 0.0;
        for (int j = 0; j < ysize; j++) {
            z = Node.logsumexp(z, node.get(0).get(j).beta, j == 0);
        }
    }

    public void viterbi() {
        for (int i = 0; i < x.size(); i++) {
            for (int j = 0; j < ysize; j++) {
                double bestc = -1e37;
                Node best = null;
                List<Path> lpath = node.get(i).get(j).lpath;
                for (Path p : lpath) {
                    double cost = p.lnode.bestCost + p.cost + node.get(i).get(j).cost;
                    if (cost > bestc) {
                        bestc = cost;
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

        cost = -node.get(x.size() - 1).get(result.get(x.size() - 1)).bestCost;
    }

    public void buildLattice() {
        if (x.isEmpty()) return;

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

    public boolean initNbest() {
        if (agenda == null) {
            agenda = new PriorityQueue<QueueElement>(10, new Comparator<QueueElement>() {
                public int compare(QueueElement o1, QueueElement o2) {
                    return (int) (o1.fx - o2.fx);
                }
            });
        }
        agenda.clear();
        int k = x.size() - 1;
        for (int i = 0; i < ysize; i++) {
            QueueElement eos = new QueueElement();
            eos.node = node.get(k).get(i);
            eos.fx = -node.get(k).get(i).bestCost;
            eos.gx = -node.get(k).get(i).cost;
            eos.next = null;
            agenda.add(eos);
        }
        return true;
    }

    public Node node(int i, int j) {
        return node.get(i).get(j);
    }

    public void set_node(Node n, int i, int j) {
        node.get(i).set(j, n);
    }

    public void add(String line) {
        int xsize = featureIndex.getXsize();
        String[] cols = line.split("[\t ]", -1);
        if (cols.length < xsize) {
            throw new IllegalArgumentException("# x is small: size=" + cols.length + " xsize=" + xsize);
        }
        List<String> tmpX = Arrays.asList(cols);
        x.add(tmpX);
        result.add(0);
        int tmpAnswer = 0;
        answer.add(tmpAnswer);
        List<Node> l = Arrays.asList(new Node[ysize]);
        node.add(l);
    }

    public void parse() {
        if (!featureIndex.buildFeatures(this)) {
            throw new IllegalStateException("Failed to build featureIndex.");
        }
        if (x.isEmpty()) {
            return;
        }
        buildLattice();
        if (nbest != 0 || vlevel >= 1) {
            forwardbackward();
        }
        viterbi();
        if (nbest != 0) {
            initNbest();
        }
    }

    public void clear() {
        x.clear();
        node.clear();
        answer.clear();
        result.clear();
        featureCache.clear();
        z = 0.0;
        cost = 0.0;
    }

    public void openBinModel(String path, int nbest, int vlevel, double costFactor) throws IOException {
        if (costFactor <= 0.0) {
            throw new IllegalArgumentException("cost factor must be positive");
        }
        if (featureIndex == null) {
            featureIndex = new DecoderFeatureIndex();
            ((DecoderFeatureIndex) featureIndex).openBinModel(path);
        }
        this.nbest = nbest;
        this.vlevel = vlevel;
        featureIndex.setCostFactor(costFactor);
        this.ysize = featureIndex.ysize();
    }

    class QueueElement {
        Node node;
        QueueElement next;
        double fx;
        double gx;
    }

    public int getVlevel() {
        return vlevel;
    }

    public void setVlevel(int vlevel) {
        this.vlevel = vlevel;
    }

    public int getNbest() {
        return nbest;
    }

    public void setNbest(int nbest) {
        this.nbest = nbest;
    }

    public int getYsize() {
        return ysize;
    }

    public void setYsize(int ysize) {
        this.ysize = ysize;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public int getFeatureId() {
        return featureId;
    }

    public void setFeatureId(int featureId) {
        this.featureId = featureId;
    }

    public int getThreadId() {
        return threadId;
    }

    public void setThreadId(int threadId) {
        this.threadId = threadId;
    }

    public FeatureIndex getFeature_index_() {
        return featureIndex;
    }

    public void setFeature_index_(FeatureIndex feature_index_) {
        this.featureIndex = feature_index_;
    }

    public List<List<String>> getX() {
        return x;
    }

    public void setX(List<List<String>> x) {
        this.x = x;
    }

    public List<List<Node>> getNode() {
        return node;
    }

    public void setNode(List<List<Node>> node) {
        this.node = node;
    }

    public List<Integer> getAnswer() {
        return answer;
    }

    public void setAnswer(List<Integer> answer) {
        this.answer = answer;
    }

    public List<Integer> getResult() {
        return result;
    }

    public void setResult(List<Integer> result) {
        this.result = result;
    }

    public List<List<Integer>> getFeatureCache() {
        return featureCache;
    }

    public int size() {
        return x.size();
    }

    public int xsize() {
        return featureIndex.getXsize();
    }

    public int y(int i) {
        return result.get(i);
    }

    public String yname(int i) {
        return featureIndex.getY().get(i);
    }

    public String y2(int i) {
        return yname(result.get(i));
    }

    public String x(int i, int j) {
        return x.get(i).get(j);
    }

    public List<String> x(int i) {
        return x.get(i);
    }

}
