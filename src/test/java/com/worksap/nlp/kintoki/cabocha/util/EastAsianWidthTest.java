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

package com.worksap.nlp.kintoki.cabocha.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class EastAsianWidthTest {

  @Test
  public void getEastAsianWidth() {
    assertEquals(3, EastAsianWidth.getEastAsianWidth("abc"));
    assertEquals(4, EastAsianWidth.getEastAsianWidth("計ab"));
    assertEquals(3, EastAsianWidth.getEastAsianWidth("ｶﾀ4"));
    assertEquals(0, EastAsianWidth.getEastAsianWidth(""));
  }
}
