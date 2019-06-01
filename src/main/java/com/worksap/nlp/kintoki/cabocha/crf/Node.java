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

class Node {
    int x;
    int y;
    double cost;
    double bestCost;
    Node prev;
    List<Integer> fVector;
    List<Path> lpath;

    Node() {
        lpath = new ArrayList<>();
        clear();
        bestCost = 0.0;
        prev = null;
    }

    void clear() {
        x = 0;
        y = 0;
        cost = 0;
        prev = null;
        fVector = null;
        lpath.clear();
    }
}
