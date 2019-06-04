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

public class PatternMatcher {

  private boolean matchedResult;
  private List<String> patterns = new ArrayList<>();

  public PatternMatcher() {
    this.matchedResult = true;
  }

  public boolean compile(String pattern) {
    if (pattern.startsWith("!")) {
      this.matchedResult = false;
      pattern = pattern.substring(1);
    }

    if (pattern.length() >= 3 && pattern.startsWith("(") && pattern.endsWith(")")) {
      pattern = pattern.substring(1, pattern.length() - 1);
      String[] items = pattern.split("\\|");
      for (String item : items) {
        this.patterns.add(item);
      }
    } else {
      this.patterns.add(pattern);
    }

    return !patterns.isEmpty();
  }

  public boolean match(String str) {
    for (int i = 0; i < this.patterns.size(); ++i) {
      if (this.patterns.get(i).equals(str)) {
        return this.matchedResult;
      }
    }
    return !this.matchedResult;
  }

  public boolean prefixMatch(String str) {
    int len = str.length();
    for (int i = 0; i < this.patterns.size(); ++i) {
      String pat = this.patterns.get(i);
      if (len < pat.length()) {
        continue;
      }
      if (str.startsWith(pat)) {
        return matchedResult;
      }
    }
    return !this.matchedResult;
  }
}
