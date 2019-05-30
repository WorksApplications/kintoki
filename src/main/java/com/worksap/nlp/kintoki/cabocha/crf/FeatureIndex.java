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
    protected float[] alphaFloat;
    protected double costFactor = 1.0;
    protected int xsize;
    protected boolean checkMaxXsize;
    protected int maxXsize;
    protected List<String> unigramTempls;
    protected List<String> bigramTempls;
    protected List<String> y;
    protected List<List<Path>> pathList;
    protected List<List<Node>> nodeList;

    protected abstract int getID(String s);

    void calcCost(Node node) {
        node.cost = 0.0;
        if (alphaFloat != null) {
            float c = 0.0f;
            for (int i = 0; node.fVector.get(i) != -1; i++) {
                c += alphaFloat[node.fVector.get(i) + node.y];
            }
            node.cost = costFactor * c;
        } else {
            double c = 0.0;
            for (int i = 0; node.fVector.get(i) != -1; i++) {
                c += alpha[node.fVector.get(i) + node.y];
            }
            node.cost = costFactor * c;
        }
    }

    void calcCost(Path path) {
        path.cost = 0.0;
        if (alphaFloat != null) {
            float c = 0.0f;
            for (int i = 0; path.fvector.get(i) != -1; i++) {
                c += alphaFloat[path.fvector.get(i) + path.lnode.y * y.size() + path.rnode.y];
            }
            path.cost = costFactor * c;
        } else {
            double c = 0.0;
            for (int i = 0; path.fvector.get(i) != -1; i++) {
                c += alpha[path.fvector.get(i) + path.lnode.y * y.size() + path.rnode.y];
            }
            path.cost = costFactor * c;
        }
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
        List<Integer> feature = new ArrayList<>();
        List<List<Integer>> featureCache = tagger.getFeatureCache();
        tagger.setFeatureId(featureCache.size());

        for (int cur = 0; cur < tagger.size(); cur++) {
            buildFeatureFromTempl(feature, unigramTempls, cur, tagger);
            feature.add(-1);
            featureCache.add(feature);
            feature = new ArrayList<>();
        }
        for (int cur = 1; cur < tagger.size(); cur++) {
            buildFeatureFromTempl(feature, bigramTempls, cur, tagger);
            feature.add(-1);
            featureCache.add(feature);
            feature = new ArrayList<>();
        }
    }

    void rebuildFeatures(Tagger tagger) {
        int fid = tagger.getFeatureId();
        List<List<Integer>> featureCache = tagger.getFeatureCache();
        for (int pos = 0; pos < tagger.size(); pos++) {
            List<Integer> f = featureCache.get(fid++);
            for (int i = 0; i < y.size(); i++) {
                Node n = new Node();
                n.clear();
                n.x = pos;
                n.y = i;
                n.fVector = f;
                tagger.setNode(n, pos, i);
            }
        }
        for (int pos = 1; pos < tagger.size(); pos++) {
            List<Integer> f = featureCache.get(fid++);
            for (int j = 0; j < y.size(); j++) {
                for (int i = 0; i < y.size(); i++) {
                    Path p = new Path();
                    p.clear();
                    p.add(tagger.node(pos - 1, j), tagger.node(pos, i));
                    p.fvector = f;
                }
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
