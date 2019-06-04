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

public class Hypothesis {

  private List<Integer> head;
  private List<Double> score;
  private List<List<Integer>> children;
  double hscore;

  public void init(int size) {
    head = new ArrayList<>();
    score = new ArrayList<>();
    children = new ArrayList<>();
    hscore = 0.0;
    for (int i = 0; i < size; ++i) {
      head.add(-1);
      score.add((double) 0);
      children.add(new ArrayList<>());
    }
  }

  public List<Integer> getHead() {
    return head;
  }

  public void setHead(List<Integer> head) {
    this.head = head;
  }

  public List<Double> getScore() {
    return score;
  }

  public void setScore(List<Double> score) {
    this.score = score;
  }

  public List<List<Integer>> getChildren() {
    return children;
  }

  public void setChildren(List<List<Integer>> children) {
    this.children = children;
  }

  public double getHscore() {
    return hscore;
  }

  public void setHscore(double hscore) {
    this.hscore = hscore;
  }
}
