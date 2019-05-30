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

import com.worksap.nlp.kintoki.cabocha.util.Utils;
import com.worksap.nlp.sudachi.Morpheme;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Tree {

    private static final String EOS_NL = "EOS\n";

    private OutputLayerType outputLayer;
    private String sentence;
    private List<Token> tokens = new ArrayList<>();
    private List<Chunk> chunks = new ArrayList<>();

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public List<Token> getTokens() {
        return tokens;
    }

    public void setTokens(List<Token> tokens) {
        this.tokens = tokens;
    }

    public List<Chunk> getChunks() {
        return chunks;
    }

    public void setChunks(List<Chunk> chunks) {
        this.chunks = chunks;
    }

    public String getSentence() {
        return sentence;
    }

    public int sentenceSize() {
        return this.sentence.length();
    }

    public Chunk chunk(int index) {
        return this.chunks.get(index);
    }

    public Token token(int index) {
        return this.tokens.get(index);
    }

    public boolean read(String input, InputLayerType inputLayer) {
        if (!Utils.check(input)) {
            return false;
        }

        int chunkId = 0;

        switch (inputLayer) {
        case INPUT_RAW_SENTENCE:
            this.sentence = input;
            break;

        case INPUT_POS:
        case INPUT_CHUNK:
        case INPUT_SELECTION:
        case INPUT_DEP:
            Chunk chunk = null;
            String[] lines = input.split("\\n");

            for (String line : lines) {
                if (line.length() <= 0) {
                    break;
                }
                if (line.length() >= 3 && line.startsWith("* ")) {
                    String[] column = line.split(" ");
                    if (column.length >= 3 && (column[1].charAt(0) == '-' || Character.isDigit(column[1].charAt(0)))) {
                        if (inputLayer == InputLayerType.INPUT_POS) {
                            continue;
                        }

                        if (chunk != null) {
                            if (chunk.getTokens().isEmpty()) {
                                return false;
                            }
                            getChunks().add(chunk);
                        }

                        if (chunkId != Integer.parseInt(column[1])) {
                            return false;
                        }
                        ++chunkId;

                        chunk = new Chunk();
                        chunk.setLink(Integer.parseInt(column[2].substring(0, column[2].length() - 1)));

                        if (column.length >= 4) {
                            int[] value = Arrays.stream(column[3].split("/")).mapToInt(Integer::valueOf).toArray();
                            chunk.setHeadPos(value[0]);
                            chunk.setFuncPos(value[1]);
                        }

                        if (column.length >= 5) {
                            chunk.setScore(Double.parseDouble(column[4]));
                        }

                        if (column.length >= 6) {
                            chunk.setFeatureList(Arrays.asList(column[5].split(",")));
                        }
                    } else {
                        return false;
                    }
                } else {
                    String[] column = line.split("\t");
                    if (column.length >= 2 && column[0].length() > 0 && column[1].length() > 0) {
                        Token token = new Token();
                        token.setSurface(column[0]);
                        token.setNormalizedSurface(column[0]);
                        token.setFeature(column[1]);
                        token.setFeatureList(Arrays.asList(column[1].split(",")));
                        getTokens().add(token);
                        if (chunk != null && inputLayer.getValue() > Constant.CABOCHA_INPUT_POS) {
                            chunk.getTokens().add(token);
                        }
                    } else {
                        break;
                    }
                }
            }

            if (chunk != null) {
                if (chunk.getTokenSize() == 0) {
                    return false;
                }
                getChunks().add(chunk);
            }
        }

        // verify chunk link
        for (int i = 0; i < getChunkSize(); ++i) {
            if (chunk(i).getLink() != -1 && (chunk(i).getLink() >= getChunkSize() || chunk(i).getLink() < -1)) {
                return false;
            }
        }

        return true;
    }

    public String toString(FormatType outputFormat) {
        StringBuilder sb = new StringBuilder();
        writeTree(this, sb, outputLayer, outputFormat);
        return sb.toString();
    }

    public void writeTree(Tree tree, StringBuilder sb, OutputLayerType outputLayer, FormatType outputFormat) {
        switch (outputFormat) {
        case FORMAT_LATTICE:
            writeLattice(tree, sb, outputLayer);
            break;
        case FORMAT_TREE_LATTICE:
            writeTree(tree, sb);
            writeLattice(tree, sb, outputLayer);
            break;
        case FORMAT_TREE:
            writeTree(tree, sb);
            break;
        case FORMAT_XML:
            writeXml(tree, sb, outputLayer);
            break;
        case FORMAT_CONLL:
            writeConll(tree, sb, outputLayer);
            break;
        case FORMAT_NONE:
            break;
        default:
            sb.append("unknown format: " + outputFormat + "\n");
        }
    }

    private void writeLattice(Tree tree, StringBuilder sb, OutputLayerType outputLayer) {
        int size = tree.getTokenSize();
        if (outputLayer == OutputLayerType.OUTPUT_RAW_SENTENCE) {
            if (tree.empty()) {
                sb.append(tree.getSentence() + "\n");
            } else {
                for (int i = 0; i < size; ++i) {
                    sb.append(tree.token(i).getSurface());
                }
                sb.append("\n");
            }
        } else {
            int ci = 0;

            if (outputLayer != OutputLayerType.OUTPUT_POS) {
                for (Chunk chunk : tree.getChunks()) {
                    switch (outputLayer) {
                    case OUTPUT_CHUNK:
                        sb.append("* " + (ci++) + " " + "-1D ");
                        break;
                    case OUTPUT_SELECTION:
                        sb.append("* " + (ci++) + " " + "-1D " + chunk.getHeadPos() + "/" + chunk.getFuncPos() + " "
                                + chunk.getScore());
                        if (chunk.getFeatureList() != null) {
                            sb.append(" " + String.join(",", chunk.getFeatureList()));
                        }
                        break;
                    case OUTPUT_DEP:
                        sb.append("* " + (ci++) + " " + chunk.getLink() + "D " + chunk.getHeadPos() + "/"
                                + chunk.getFuncPos() + " " + chunk.getScore());
                        break;
                    default:
                        // nothing
                        break;
                    }
                    sb.append("\n");

                    for (Token token : chunk.getTokens()) {
                        sb.append(token.getSurface() + "\t" + token.getFeature() + "\n");
                    }
                }
            } else {
                for (Token token : tree.getTokens()) {
                    sb.append(token.getSurface() + "\t" + token.getFeature() + "\n");
                }
            }

            sb.append(EOS_NL);
        }
    }

    private void writeTree(Tree tree, StringBuilder sb) {
        int size = tree.getChunkSize();
        Optional<Integer> maxLength = tree.getChunks().stream()
                .map(chunk -> chunk.getTokens().stream().map(Token::getSurface).collect(Collectors.joining()).length())
                .collect(Collectors.reducing(Integer::max));
        if (!maxLength.isPresent()) {
            sb.append(EOS_NL);
            return;
        }
        int maxLen = maxLength.get();
        boolean[] e = new boolean[size];

        for (int i = 0; i < size; ++i) {
            boolean isDep = false;
            int link = tree.chunk(i).getLink();
            String surface = tree.chunk(i).getTokens().stream().map(Token::getSurface).collect(Collectors.joining());
            int rem = maxLen - surface.length() + i * 2;
            for (int j = 0; j < rem; ++j) {
                sb.append(" ");
            }
            sb.append(surface);

            for (int j = i + 1; j < size; j++) {
                if (link == j) {
                    sb.append("-" + "D");
                    isDep = true;
                    e[j] = true;
                } else if (e[j]) {
                    sb.append(" |");
                } else if (isDep) {
                    sb.append("  ");
                } else {
                    sb.append("--");
                }
            }
            sb.append("\n");
        }

        sb.append(EOS_NL);
    }

    private void writeXml(Tree tree, StringBuilder sb, OutputLayerType outputLayer) {
        int ci = 0;
        int size = tree.getChunkSize();

        sb.append("<sentence>\n");

        for (int i = 0; i < size; ++i) {
            Chunk chunk = tree.chunk(i);
            if (chunk == null) {
                continue;
            }

            if (outputLayer != OutputLayerType.OUTPUT_POS) {
                if (ci > 0) {
                    sb.append(" </chunk>\n");
                }
                sb.append(" <chunk id=\"" + (ci++) + "\" link=\"" + chunk.getLink());
                sb.append("\" rel=\"D");
                sb.append("\" score=\"" + chunk.getScore());
                sb.append("\" head=\"" + chunk.getHeadPos());
                sb.append("\" func=\"" + chunk.getFuncPos() + "\"");
                if (outputLayer == OutputLayerType.OUTPUT_SELECTION && chunk.getFeatureList() != null) {
                    String features = String.join("", chunk.getFeatureList());
                    sb.append(" feature=\"");
                    sb.append(writeXmlString(features));
                    sb.append("\"");
                }
                sb.append(">\n");
            }

            for (int j = 0; j < chunk.getTokenSize(); j++) {
                Token token = chunk.token(j);
                sb.append(writeXmlToken(token, j));
            }

            if (ci > 0) {
                sb.append(" </chunk>\n");
            }
        }

        sb.append("</sentence>\n");
    }

    private String writeXmlString(String str) {
        return str.replace("\"", "&quot;").replace("\'", "&apos;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("&", "&amp;");
    }

    private String writeXmlToken(Token token, int i) {
        StringBuilder sb = new StringBuilder();
        sb.append("  <tok id=\"" + i + "\"" + " feature=\"");
        sb.append(writeXmlString(token.getFeature()));
        sb.append("\"");
        sb.append(">");
        sb.append(writeXmlString(token.getSurface()));
        sb.append("</tok>\n");

        return sb.toString();
    }

    private void writeConll(Tree tree, StringBuilder sb, OutputLayerType outputLayer) {
        int size = tree.getChunkSize();
        int tokenId = 1;
        String pos;

        for (int i = 0; i < size; ++i) {
            Chunk chunk = tree.chunk(i);
            String dlabel = "_";
            for (int j = 0; j < chunk.getTokenSize(); ++j) {
                int link = 0;
                if (j == chunk.getTokenSize() - 1 && chunk.getLink() >= 0 && chunk.getLink() < tree.getChunkSize()) {
                    Chunk head = tree.chunk(chunk.getLink());
                    link = head.getHeadPos() + head.getTokenPos() + 1;
                    dlabel = "D";
                } else {
                    link = chunk.getTokenPos() + j + 2;
                }
                if (link == tree.getTokenSize() + 1) {
                    link = 0;
                }
                Token token = tree.token(chunk.getTokenPos() + j);

                String lemma = token.getNormalizedSurface();

                pos = Utils.concatFeature(token, 4);
                if (token.getFeatureList().size() > 7) {
                    lemma = token.getFeatureList().get(6);
                }

                String category = "_";
                if (!token.getFeatureList().isEmpty()) {
                    category = token.getFeatureList().get(0);
                }

                sb.append((tokenId++) + "\t" + token.getSurface() + "\t" + lemma);
                sb.append("\t" + category + "\t" + pos);
                sb.append("\tfeature=" + token.getFeature());

                if (j == 0 && outputLayer.getValue() >= Constant.CABOCHA_OUTPUT_CHUNK) {
                    sb.append("|begin_chunk=1");
                }
                if (outputLayer.getValue() >= Constant.CABOCHA_OUTPUT_SELECTION) {
                    if (j == chunk.getHeadPos()) {
                        sb.append("|head=1");
                    }
                    if (j == chunk.getFuncPos()) {
                        sb.append("|func=1");
                    }
                }
                sb.append("\t" + link + "\t" + dlabel + "\t_\t_\n");
            }
        }

        sb.append("\n");
    }

    public void read(List<Morpheme> morphemes) {
        for (Morpheme m : morphemes) {
            Token token = new Token();
            token.setSurface(m.surface());
            token.setNormalizedSurface(m.normalizedForm());
            token.setPos(m.partOfSpeech().get(0));
            token.setFeature(String.join(",", m.partOfSpeech()));
            token.setFeatureList(m.partOfSpeech());
            tokens.add(token);
        }
    }

    public boolean empty() {
        return this.tokens.isEmpty();
    }

    public int getChunkSize() {
        return this.chunks.size();
    }

    public int getTokenSize() {
        return this.tokens.size();
    }

    public OutputLayerType getOutputLayer() {
        return outputLayer;
    }

    public void setOutputLayer(OutputLayerType outputLayer) {
        this.outputLayer = outputLayer;
    }

}
