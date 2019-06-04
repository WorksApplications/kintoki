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

public class DependencyParserData {

  private List<ChunkInfo> chunkInfo = new ArrayList<>();
  private List<Integer> fp = new ArrayList<>();
  private Hypothesis hypothesis = new Hypothesis();

  public List<ChunkInfo> getChunkInfo() {
    return chunkInfo;
  }

  public List<Integer> getFp() {
    return fp;
  }

  public ChunkInfo chunkInfo(int index) {
    return this.chunkInfo.get(index);
  }

  public Hypothesis getHypothesis() {
    return hypothesis;
  }
}
