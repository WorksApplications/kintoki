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

import static org.junit.Assert.assertArrayEquals;

import java.io.IOException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ChunkerTest {

  private static Param param;
  private static Analyzer morpher;
  private static Analyzer chunker;
  private static Tree tree;

  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Before
  public void setUp() throws IOException {
    TestUtils.copyResources(temporaryFolder.getRoot().toPath());
    String configPath = TestUtils.buildConfig(temporaryFolder.getRoot().toPath());

    param = new Param();
    param.loadConfig(configPath);

    morpher = new MorphAnalyzer();
    morpher.open(param);

    chunker = new Chunker();
    chunker.open(param);

    tree = new Tree();
  }

  @Test
  public void parse() {
    final String sent = "太郎は花子が読んでいる本を次郎に渡した。";

    tree.setSentence(sent);
    morpher.parse(tree);
    chunker.parse(tree);

    assertArrayEquals(
        new String[] {"太郎は", "花子が", "読んでいる", "本を", "次郎に", "渡した。"},
        TestHelper.getChunkStrings(tree).stream().toArray(String[]::new));
  }
}
