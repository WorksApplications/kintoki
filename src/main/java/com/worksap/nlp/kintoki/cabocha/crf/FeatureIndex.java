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

import java.util.ArrayList;
import java.util.List;

abstract class FeatureIndex {

    private static final String[] BOS = { "_B-1", "_B-2", "_B-3", "_B-4", "_B-5", "_B-6", "_B-7", "_B-8" };
    private static final String[] EOS = { "_B+1", "_B+2", "_B+3", "_B+4", "_B+5", "_B+6", "_B+7", "_B+8" };
    protected int maxId;
    protected double[] alpha;
    protected double costFactor = 1.0;
    protected int xsize;
    protected boolean checkMaxXsize;
    protected int maxXsize;
    protected List<String> unigramTempls;
    protected List<String> bigramTempls;
    protected List<String> y;

    protected abstract int getID(String s);

    double calcCost(Node node) {
        double c = 0.0;
        for (int f : node.fVector) {
            c += alpha[f + node.y];
        }
        return costFactor * c;
    }

    double calcPathCost(Node lNode, Node rNode) {
        double c = 0.0;
        for (int f : rNode.lPathFVector) {
            c += alpha[f + lNode.y * y.size() + rNode.y];
        }
        return costFactor * c;
    }

    private String getIndex(String[] idxStr, int pos, Tagger tagger) {
        int row = Integer.parseInt(idxStr[0]);
        int col = Integer.parseInt(idxStr[1]);
        int idx = row + pos;
        if (row < -EOS.length || row > EOS.length || col < 0 || col >= tagger.xsize()) {
            return null;
        }

        // TODO(taku): very dirty workaround
        if (checkMaxXsize) {
            maxXsize = Math.max(maxXsize, col + 1);
        }
        if (idx < 0) {
            return BOS[-idx - 1];
        } else if (idx >= tagger.size()) {
            return EOS[idx - tagger.size()];
        } else {
            return tagger.x(idx, col);
        }
    }

    private String applyRule(String rule, int pos, Tagger tagger) {
        StringBuilder sb = new StringBuilder();
        for (String tmp : rule.split("%x", -1)) {
            if (tmp.startsWith("U") || tmp.startsWith("B")) {
                sb.append(tmp);
            } else if (tmp.length() > 0) {
                String[] tuple = tmp.split("]");
                String[] idx = tuple[0].replace("[", "").split(",");
                String r = getIndex(idx, pos, tagger);
                if (r != null) {
                    sb.append(r);
                }
                if (tuple.length > 1) {
                    sb.append(tuple[1]);
                }
            }
        }

        return sb.toString();
    }

    private void buildFeatureFromTempl(List<Integer> feature, List<String> templs, int pos, Tagger tagger) {
        for (String tmpl : templs) {
            String featureID = applyRule(tmpl, pos, tagger);
            if (featureID == null || featureID.length() == 0) {
                throw new IllegalStateException("format error");
            }
            int id = getID(featureID);
            if (id != -1) {
                feature.add(id);
            }
        }
    }

    void buildFeatures(Tagger tagger) {
        List<List<Integer>> featureCache = tagger.getFeatureCache();
        tagger.setFeatureId(featureCache.size());

        for (int cur = 0; cur < tagger.size(); cur++) {
            List<Integer> feature = new ArrayList<>();
            buildFeatureFromTempl(feature, unigramTempls, cur, tagger);
            featureCache.add(feature);
        }
        for (int cur = 1; cur < tagger.size(); cur++) {
            List<Integer> feature = new ArrayList<>();
            buildFeatureFromTempl(feature, bigramTempls, cur, tagger);
            featureCache.add(feature);
        }
    }

    void rebuildFeatures(Tagger tagger) {
        int fid = tagger.getFeatureId();
        List<List<Integer>> featureCache = tagger.getFeatureCache();
        for (int pos = 0; pos < tagger.size(); pos++) {
            List<Integer> f = featureCache.get(fid++);
            for (int i = 0; i < y.size(); i++) {
                Node n = new Node();
                n.x = pos;
                n.y = i;
                n.fVector = f;
                tagger.setNode(n, pos, i);
            }
        }
        for (int pos = 1; pos < tagger.size(); pos++) {
            List<Integer> f = featureCache.get(fid++);
            for (int i = 0; i < y.size(); i++) {
                tagger.node(pos, i).lPathFVector = f;
            }
        }
    }

    int ysize() {
        return y.size();
    }

    void setCostFactor(double costFactor) {
        this.costFactor = costFactor;
    }

    int getXsize() {
        return xsize;
    }

    List<String> getY() {
        return y;
    }
}
