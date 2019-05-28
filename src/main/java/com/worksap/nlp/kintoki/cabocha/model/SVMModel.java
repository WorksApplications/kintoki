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

package com.worksap.nlp.kintoki.cabocha.model;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public abstract class SVMModel implements SVMModelInterface {

    public int size() { return alpha.size(); }

    public double y(int i) { return alpha.get(i) > 0 ? +1 : -1; }

    public List<Integer> x(int i) { return x.get(i); }

    public int id(String key) { return 0; }

    public double classify(List<Integer> x) { return 0; }

    protected List<Double> alpha;
    protected List<List<Integer>> x;
    protected Map<String, String> param;

}
