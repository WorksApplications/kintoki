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

public class Node {
    public int x;
    public int y;
    public double alpha;
    public double beta;
    public double cost;
    public double bestCost;
    public Node prev;
    public List<Integer> fVector;
    public List<Path> lpath;
    public List<Path> rpath;
    public static int MINUS_LOG_EPSILON = 50;

    public Node() {
        lpath = new ArrayList<Path>();
        rpath = new ArrayList<Path>();
        clear();
        bestCost = 0.0;
        prev = null;
    }

    public static double logsumexp(double x, double y, boolean flg) {
        if (flg) {
            return y;
        }
        double vmin = Math.min(x, y);
        double vmax = Math.max(x, y);
        if (vmax > vmin + MINUS_LOG_EPSILON) {
            return vmax;
        } else {
            return vmax + Math.log(Math.exp(vmin - vmax) + 1.0);
        }
    }

    public void calcAlpha() {
        alpha = 0.0;
        for (Path p: lpath) {
            alpha = logsumexp(alpha, p.cost + p.lnode.alpha, p == lpath.get(0));
        }
        alpha += cost;
    }

    public void calcBeta() {
        beta = 0.0;
        for (Path p: rpath) {
            beta = logsumexp(beta, p.cost + p.rnode.beta, p == rpath.get(0));
        }
        beta += cost;
    }

    public void clear() {
        x = 0;
        y = 0;
        alpha = 0;
        beta = 0;
        cost = 0;
        prev = null;
        fVector = null;
        lpath.clear();
        rpath.clear();
    }
}
