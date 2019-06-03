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

import com.worksap.nlp.kintoki.cabocha.util.EastAsianWidth;
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
        if (isEmpty()) {
            return sentence;
        } else {
            return getTokens().stream().map(Token::getSurface).collect(Collectors.joining());
        }
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

    public void read(String input, InputLayerType inputLayer) {
        switch (inputLayer) {
        case INPUT_RAW_SENTENCE:
            this.sentence = input;
            break;
        case INPUT_POS:
        case INPUT_CHUNK:
        case INPUT_SELECTION:
        case INPUT_DEP:
            readCaboChaFormat(input, inputLayer);
            break;
        default:
            throw new IllegalArgumentException("Invalid input layer");
        }

        // verify chunk link
        if (getChunks().stream().map(Chunk::getLink).anyMatch(l -> l != -1 && (l >= getChunkSize() || l < -1))) {
            throw new IllegalArgumentException("Invalid dependencies");
        }
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

    private void readCaboChaFormat(String input, InputLayerType inputLayer) {
        int chunkId = 0;
        for (String line : input.split("\n")) {
            if (line.trim().isEmpty()) {
                throw new IllegalArgumentException("Invalid format");
            }
            if (line.length() >= 3 && line.startsWith("* ")) {
                if (inputLayer == InputLayerType.INPUT_POS) {
                    continue;
                }
                chunks.add(readHeader(line, chunkId));
                chunkId++;
            } else {
                Token token = readToken(line);
                getTokens().add(token);
                if (!chunks.isEmpty() && inputLayer.getValue() > Constant.CABOCHA_INPUT_POS) {
                    addTokenToLastChunk(token);
                }
            }
        }
        if (chunks.stream().anyMatch(c -> c.getTokenSize() == 0)) {
            throw new IllegalArgumentException("Empty chunk");
        }
    }

    private Chunk readHeader(String line, int chunkId) {
        String[] columns = line.split(" ");
        if (columns.length < 3 || chunkId != Integer.parseInt(columns[1])) {
            throw new IllegalArgumentException("Invalid header format");
        }

        Chunk chunk = new Chunk();
        chunk.setLink(Integer.parseInt(columns[2].substring(0, columns[2].length() - 1)));

        if (columns.length >= 4) {
            int[] value = Arrays.stream(columns[3].split("/")).mapToInt(Integer::valueOf).toArray();
            chunk.setHeadPos(value[0]);
            chunk.setFuncPos(value[1]);
        }

        if (columns.length >= 5) {
            chunk.setScore(Double.parseDouble(columns[4]));
        }

        if (columns.length >= 6) {
            chunk.setFeatureList(Arrays.asList(columns[5].split(",")));
        }

        return chunk;
    }

    private Token readToken(String line) {
        String[] columns = line.split("\t");
        if (columns.length < 2 || columns[0].isEmpty() || columns[1].isEmpty()) {
            throw new IllegalArgumentException("Invalid format");
        }

        Token token = new Token();
        token.setSurface(columns[0]);
        token.setNormalizedSurface(columns[0]);
        token.setFeature(columns[1]);
        token.setFeatureList(Arrays.asList(columns[1].split(",")));

        return token;
    }

    public String toString(FormatType outputFormat) {
        StringBuilder sb = new StringBuilder();
        writeTree(sb, outputLayer, outputFormat);
        return sb.toString();
    }

    public void writeTree(StringBuilder sb, OutputLayerType outputLayer, FormatType outputFormat) {
        switch (outputFormat) {
        case FORMAT_LATTICE:
            writeLattice(sb, outputLayer);
            break;
        case FORMAT_TREE_LATTICE:
            writeTree(sb);
            writeLattice(sb, outputLayer);
            break;
        case FORMAT_TREE:
            writeTree(sb);
            break;
        case FORMAT_XML:
        case FORMAT_CONLL:
            throw new UnsupportedOperationException("Not implemented");
        case FORMAT_NONE:
            break;
        default:
            throw new IllegalArgumentException("unknown format: " + outputFormat + "\n");
        }
    }

    private void writeLattice(StringBuilder sb, OutputLayerType outputLayer) {
        if (outputLayer == OutputLayerType.OUTPUT_RAW_SENTENCE) {
            sb.append(getSentence());
            sb.append("\n");
        } else if (outputLayer == OutputLayerType.OUTPUT_POS) {
            sb.append(getTokens().stream().map(t -> t.getSurface() + "\t" + t.getFeature() + "\n")
                    .collect(Collectors.joining()));
            sb.append(EOS_NL);
        } else {
            int ci = 0;
            for (Chunk chunk : getChunks()) {
                writeChunk(sb, chunk, ci++, outputLayer);
            }
            sb.append(EOS_NL);
        }
    }

    private void writeChunk(StringBuilder sb, Chunk chunk, int id, OutputLayerType outputLayer) {
        writeHeader1(sb, id, (outputLayer == OutputLayerType.OUTPUT_DEP) ? chunk.getLink() : -1);
        if (outputLayer != OutputLayerType.OUTPUT_CHUNK) {
            writeHeader2(sb, chunk);
            if (outputLayer == OutputLayerType.OUTPUT_SELECTION && chunk.getFeatureList() != null
                    && !chunk.getFeatureList().isEmpty()) {
                sb.append(' ').append(String.join(",", chunk.getFeatureList()));
            }
        }
        sb.append('\n');

        for (Token token : chunk.getTokens()) {
            sb.append(token.getSurface()).append('\t').append(token.getFeature()).append('\n');
        }
    }

    private void writeHeader1(StringBuilder sb, int id, int link) {
        sb.append("* ").append(id).append(' ').append(link).append("D");
    }

    private void writeHeader2(StringBuilder sb, Chunk chunk) {
        sb.append(' ').append(chunk.getHeadPos()).append('/').append(chunk.getFuncPos()).append(' ')
                .append(chunk.getScore());
    }

    private void writeTree(StringBuilder sb) {
        int size = getChunkSize();
        Optional<Integer> maxLength = getChunks().stream()
                .map(chunk -> EastAsianWidth.getEastAsianWidth(chunk.getSurface()))
                .collect(Collectors.reducing(Integer::max));
        if (!maxLength.isPresent()) {
            sb.append(EOS_NL);
            return;
        }
        int maxLen = maxLength.get();
        boolean[] e = new boolean[size];

        for (int i = 0; i < size; ++i) {
            boolean isDep = false;
            int link = chunk(i).getLink();
            String surface = chunk(i).getSurface();
            int rem = maxLen - EastAsianWidth.getEastAsianWidth(surface) + i * 2;
            for (int j = 0; j < rem; ++j) {
                sb.append(' ');
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

    public boolean isEmpty() {
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

    private void addTokenToLastChunk(Token token) {
        chunks.get(chunks.size() - 1).getTokens().add(token);
    }
}
