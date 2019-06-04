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

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

class DoubleArrayTrie {

  private int[] array;

  DoubleArrayTrie(ByteBuffer array, int byteSize) {
    int arraySize = byteSize / 4;
    this.array = new int[arraySize];
    for (int i = 0; i < arraySize; ++i) {
      this.array[i] = array.getInt();
    }
  }

  int exactMatchSearch(String key) {
    byte[] k = key.getBytes(StandardCharsets.UTF_8);

    int result = -1;
    int b = getBase(0);
    int p;

    for (int i = 0; i < k.length; ++i) {
      p = b + Byte.toUnsignedInt(k[i]) + 1;
      if (b == getCheck(p)) {
        b = getBase(p);
      } else {
        return result;
      }
    }

    p = b;
    int n = getBase(p);
    if (b == getCheck(p) && n < 0) {
      result = -n - 1;
    }
    return result;
  }

  private int getBase(int i) {
    return array[i * 2];
  }

  private int getCheck(int i) {
    return array[i * 2 + 1];
  }
}
