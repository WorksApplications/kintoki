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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TreeTest {

  @Test
  public void readWithRawSentence() {
    final String input = "abc";
    Tree tree = new Tree();
    tree.read(input, InputLayerType.INPUT_RAW_SENTENCE);
    assertEquals(input, tree.getSentence());
  }

  @Test
  public void readWithPOS() {
    final String input =
        "* 0 -1D\n太郎\t名詞,固有名詞,人名,名,*,*,太郎,タロウ,タロー\nは\t助詞,係助詞,*,*,*,*,は,ハ,ワ\n* 1 -1D\n花子\t名詞,固有名詞,人名,名,*,*,花子,ハナコ,ハナコ\nが\t助詞,格助詞,一般,*,*,*,が,ガ,ガ\n";
    Tree tree = new Tree();
    tree.read(input, InputLayerType.INPUT_POS);
    assertEquals(4, tree.getTokenSize());
    assertEquals("太郎", tree.getTokens().get(0).getSurface());
  }

  @Test(expected = IllegalArgumentException.class)
  public void readWithPOSWithInvalidFormat() {
    final String input = "* 0 -1D\n太郎\n";
    Tree tree = new Tree();
    tree.read(input, InputLayerType.INPUT_POS);
  }

  @Test
  public void readWithChunk() {
    final String input =
        "* 0 -1D\n太郎\t名詞,固有名詞,人名,名,*,*,太郎,タロウ,タロー\nは\t助詞,係助詞,*,*,*,*,は,ハ,ワ\n* 1 -1D\n花子\t名詞,固有名詞,人名,名,*,*,花子,ハナコ,ハナコ\nが\t助詞,格助詞,一般,*,*,*,が,ガ,ガ\n";
    Tree tree = new Tree();
    tree.read(input, InputLayerType.INPUT_CHUNK);
    assertEquals(2, tree.getChunkSize());
    assertEquals("太郎", tree.getChunks().get(0).getTokens().get(0).getSurface());
  }

  @Test(expected = IllegalArgumentException.class)
  public void readWithChunkWithInvalidHeader() {
    final String input = "* AA -1D\n太郎\t名詞,固有名詞,人名,名,*,*,太郎,タロウ,タロー\nは\t助詞,係助詞,*,*,*,*,は,ハ,ワ\n";
    Tree tree = new Tree();
    tree.read(input, InputLayerType.INPUT_CHUNK);
  }

  @Test(expected = IllegalArgumentException.class)
  public void readWithChunkWithInvalidChunkId() {
    final String input = "* 1 -1D\n太郎\t名詞,固有名詞,人名,名,*,*,太郎,タロウ,タロー\nは\t助詞,係助詞,*,*,*,*,は,ハ,ワ\n";
    Tree tree = new Tree();
    tree.read(input, InputLayerType.INPUT_CHUNK);
  }

  @Test(expected = IllegalArgumentException.class)
  public void readWithChunkWithEmptyChunk() {
    final String input = "* 0 -1D\n";
    Tree tree = new Tree();
    tree.read(input, InputLayerType.INPUT_CHUNK);
  }

  @Test(expected = IllegalArgumentException.class)
  public void readWithChunkWithInvalidDependencies() {
    final String input = "* 0 1D\n太郎\t名詞,固有名詞,人名,名,*,*,太郎,タロウ,タロー\nは\t助詞,係助詞,*,*,*,*,は,ハ,ワ\n";
    Tree tree = new Tree();
    tree.read(input, InputLayerType.INPUT_CHUNK);
  }

  @Test
  public void writeLattice() {
    final String input =
        "* 0 1D\n太郎\t名詞,固有名詞,人名,名,*,*,太郎,タロウ,タロー\nは\t助詞,係助詞,*,*,*,*,は,ハ,ワ\n* 1 -1D\n花子\t名詞,固有名詞,人名,名,*,*,花子,ハナコ,ハナコ\nが\t助詞,格助詞,一般,*,*,*,が,ガ,ガ\n";
    Tree tree = new Tree();
    tree.read(input, InputLayerType.INPUT_CHUNK);
    StringBuilder sb = new StringBuilder();
    tree.writeTree(sb, OutputLayerType.OUTPUT_DEP, FormatType.FORMAT_LATTICE);
    assertEquals(
        "* 0 1D 0/0 0.0\n太郎\t名詞,固有名詞,人名,名,*,*,太郎,タロウ,タロー\nは\t助詞,係助詞,*,*,*,*,は,ハ,ワ\n* 1 -1D 0/0 0.0\n花子\t名詞,固有名詞,人名,名,*,*,花子,ハナコ,ハナコ\nが\t助詞,格助詞,一般,*,*,*,が,ガ,ガ\nEOS\n",
        sb.toString());
  }

  @Test
  public void writeLatticeWithRawSentence() {
    final String input =
        "* 0 1D\n太郎\t名詞,固有名詞,人名,名,*,*,太郎,タロウ,タロー\nは\t助詞,係助詞,*,*,*,*,は,ハ,ワ\n* 1 -1D\n花子\t名詞,固有名詞,人名,名,*,*,花子,ハナコ,ハナコ\nが\t助詞,格助詞,一般,*,*,*,が,ガ,ガ\n";
    Tree tree = new Tree();
    tree.read(input, InputLayerType.INPUT_CHUNK);
    StringBuilder sb = new StringBuilder();
    tree.writeTree(sb, OutputLayerType.OUTPUT_RAW_SENTENCE, FormatType.FORMAT_LATTICE);
    assertEquals("太郎は花子が\n", sb.toString());
  }

  @Test
  public void writeLatticeWithPOS() {
    final String input =
        "* 0 1D\n太郎\t名詞,固有名詞,人名,名,*,*,太郎,タロウ,タロー\nは\t助詞,係助詞,*,*,*,*,は,ハ,ワ\n* 1 -1D\n花子\t名詞,固有名詞,人名,名,*,*,花子,ハナコ,ハナコ\nが\t助詞,格助詞,一般,*,*,*,が,ガ,ガ\n";
    Tree tree = new Tree();
    tree.read(input, InputLayerType.INPUT_CHUNK);
    StringBuilder sb = new StringBuilder();
    tree.writeTree(sb, OutputLayerType.OUTPUT_POS, FormatType.FORMAT_LATTICE);
    assertEquals(
        "太郎\t名詞,固有名詞,人名,名,*,*,太郎,タロウ,タロー\nは\t助詞,係助詞,*,*,*,*,は,ハ,ワ\n花子\t名詞,固有名詞,人名,名,*,*,花子,ハナコ,ハナコ\nが\t助詞,格助詞,一般,*,*,*,が,ガ,ガ\nEOS\n",
        sb.toString());
  }

  @Test
  public void writeLatticeWithChunk() {
    final String input =
        "* 0 1D\n太郎\t名詞,固有名詞,人名,名,*,*,太郎,タロウ,タロー\nは\t助詞,係助詞,*,*,*,*,は,ハ,ワ\n* 1 -1D\n花子\t名詞,固有名詞,人名,名,*,*,花子,ハナコ,ハナコ\nが\t助詞,格助詞,一般,*,*,*,が,ガ,ガ\n";
    Tree tree = new Tree();
    tree.read(input, InputLayerType.INPUT_CHUNK);
    StringBuilder sb = new StringBuilder();
    tree.writeTree(sb, OutputLayerType.OUTPUT_CHUNK, FormatType.FORMAT_LATTICE);
    assertEquals(
        "* 0 -1D\n太郎\t名詞,固有名詞,人名,名,*,*,太郎,タロウ,タロー\nは\t助詞,係助詞,*,*,*,*,は,ハ,ワ\n* 1 -1D\n花子\t名詞,固有名詞,人名,名,*,*,花子,ハナコ,ハナコ\nが\t助詞,格助詞,一般,*,*,*,が,ガ,ガ\nEOS\n",
        sb.toString());
  }

  @Test
  public void writeLatticeWithSelection() {
    final String input =
        "* 0 1D\n太郎\t名詞,固有名詞,人名,名,*,*,太郎,タロウ,タロー\nは\t助詞,係助詞,*,*,*,*,は,ハ,ワ\n* 1 -1D\n花子\t名詞,固有名詞,人名,名,*,*,花子,ハナコ,ハナコ\nが\t助詞,格助詞,一般,*,*,*,が,ガ,ガ\n";
    Tree tree = new Tree();
    tree.read(input, InputLayerType.INPUT_CHUNK);
    StringBuilder sb = new StringBuilder();
    tree.writeTree(sb, OutputLayerType.OUTPUT_SELECTION, FormatType.FORMAT_LATTICE);
    assertEquals(
        "* 0 -1D 0/0 0.0\n太郎\t名詞,固有名詞,人名,名,*,*,太郎,タロウ,タロー\nは\t助詞,係助詞,*,*,*,*,は,ハ,ワ\n* 1 -1D 0/0 0.0\n花子\t名詞,固有名詞,人名,名,*,*,花子,ハナコ,ハナコ\nが\t助詞,格助詞,一般,*,*,*,が,ガ,ガ\nEOS\n",
        sb.toString());
  }

  @Test
  public void writeTree() {
    final String input =
        "* 0 5D 0/1\n"
            + "太郎\t名詞,固有名詞,人名,名,*,*,太郎,タロウ,タロー\n"
            + "は\t助詞,係助詞,*,*,*,*,は,ハ,ワ\n"
            + "* 1 2D 0/1\n"
            + "花子\t名詞,固有名詞,人名,名,*,*,花子,ハナコ,ハナコ\n"
            + "が\t助詞,格助詞,一般,*,*,*,が,ガ,ガ\n"
            + "* 2 3D 0/2\n"
            + "読ん\t動詞,自立,*,*,五段・マ行,連用タ接続,読む,ヨン,ヨン\n"
            + "で\t助詞,接続助詞,*,*,*,*,で,デ,デ\n"
            + "いる\t動詞,非自立,*,*,一段,基本形,いる,イル,イル\n"
            + "* 3 5D 0/1\n"
            + "本\t名詞,一般,*,*,*,*,本,ホン,ホン\n"
            + "を\t助詞,格助詞,一般,*,*,*,を,ヲ,ヲ\n"
            + "* 4 5D 1/2\n"
            + "次郎\t名詞,固有名詞,人名,名,*,*,次郎,ジロウ,ジロー\n"
            + "に\t助詞,格助詞,一般,*,*,*,に,ニ,ニ\n"
            + "* 5 -1D 0/1\n"
            + "渡し\t動詞,自立,*,*,五段・サ行,連用形,渡す,ワタシ,ワタシ\n"
            + "た\t助動詞,*,*,*,特殊・タ,基本形,た,タ,タ\n";
    Tree tree = new Tree();
    tree.read(input, InputLayerType.INPUT_CHUNK);
    StringBuilder sb = new StringBuilder();
    tree.writeTree(sb, OutputLayerType.OUTPUT_DEP, FormatType.FORMAT_TREE);
    assertEquals(
        "    太郎は---------D\n"
            + "      花子が-D     |\n"
            + "    読んでいる-D   |\n"
            + "            本を---D\n"
            + "            次郎に-D\n"
            + "              渡した\n"
            + "EOS\n",
        sb.toString());
  }

  @Test
  public void writeEmptyRawSentence() {
    Tree tree = new Tree();
    tree.setOutputLayer(OutputLayerType.OUTPUT_RAW_SENTENCE);
    assertEquals("\n", tree.toString(FormatType.FORMAT_LATTICE));
    assertEquals("EOS\n", tree.toString(FormatType.FORMAT_TREE));
  }

  @Test
  public void writeEmptyPOS() {
    Tree tree = new Tree();
    tree.setOutputLayer(OutputLayerType.OUTPUT_POS);
    assertEquals("EOS\n", tree.toString(FormatType.FORMAT_LATTICE));
    assertEquals("EOS\n", tree.toString(FormatType.FORMAT_TREE));
  }

  @Test
  public void writeEmptyChunk() {
    Tree tree = new Tree();
    tree.setOutputLayer(OutputLayerType.OUTPUT_CHUNK);
    assertEquals("EOS\n", tree.toString(FormatType.FORMAT_LATTICE));
    assertEquals("EOS\n", tree.toString(FormatType.FORMAT_TREE));
  }

  @Test
  public void writeEmptySelection() {
    Tree tree = new Tree();
    tree.setOutputLayer(OutputLayerType.OUTPUT_SELECTION);
    assertEquals("EOS\n", tree.toString(FormatType.FORMAT_LATTICE));
    assertEquals("EOS\n", tree.toString(FormatType.FORMAT_TREE));
  }

  @Test
  public void writeEmptyDep() {
    Tree tree = new Tree();
    tree.setOutputLayer(OutputLayerType.OUTPUT_DEP);
    assertEquals("EOS\n", tree.toString(FormatType.FORMAT_LATTICE));
    assertEquals("EOS\n", tree.toString(FormatType.FORMAT_TREE));
  }
}
