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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Parser {

    private List<Analyzer> analyzerList = new ArrayList<>();
    private FormatType outputFormat;
    private InputLayerType inputLayer;
    private OutputLayerType outputLayer;
    private Param param;

    /**
     * Create a new instance of Parser class.
     *
     * @throws IOException IOexception will be thrown when error occurs in reading
     *                     files (such as model file, resource file)
     */
    public Parser() throws IOException {
        this.param = new Param();
        this.param.loadConfig();
        loadParam(param);
    }

    /**
     * Create a new instance of Parser class.
     *
     * @param config the path of configuration file
     * @throws IOException IOexception will be thrown when error occurs in reading
     *                     files (such as model file, resource file)
     */
    public Parser(String config) throws IOException {
        this.param = new Param();
        this.param.loadConfig(config);
        loadParam(param);
    }

    /**
     * Create a new instance of Parser class.
     *
     * @param param parameters required in parsing
     */
    public Parser(Param param) {
        this.param = param;
        loadParam(param);
    }

    private void loadParam(Param param) {
        switch (param.getInt(Param.INPUT_LAYER)) {
        case Constant.CABOCHA_INPUT_RAW_SENTENCE:
            this.inputLayer = InputLayerType.INPUT_RAW_SENTENCE;
            break;
        case Constant.CABOCHA_INPUT_POS:
            this.inputLayer = InputLayerType.INPUT_POS;
            break;
        case Constant.CABOCHA_INPUT_CHUNK:
            this.inputLayer = InputLayerType.INPUT_CHUNK;
            break;
        case Constant.CABOCHA_INPUT_SELECTION:
            this.inputLayer = InputLayerType.INPUT_SELECTION;
            break;
        case Constant.CABOCHA_INPUT_DEP:
            this.inputLayer = InputLayerType.INPUT_DEP;
            break;
        default:
            throw new IllegalArgumentException("unknown input layer: " + param.getInt(Param.INPUT_LAYER) + "\n");
        }
        switch (param.getInt(Param.OUTPUT_LAYER)) {
        case Constant.CABOCHA_OUTPUT_RAW_SENTENCE:
            this.outputLayer = OutputLayerType.OUTPUT_RAW_SENTENCE;
            break;
        case Constant.CABOCHA_OUTPUT_POS:
            this.outputLayer = OutputLayerType.OUTPUT_POS;
            break;
        case Constant.CABOCHA_OUTPUT_CHUNK:
            this.outputLayer = OutputLayerType.OUTPUT_CHUNK;
            break;
        case Constant.CABOCHA_OUTPUT_SELECTION:
            this.outputLayer = OutputLayerType.OUTPUT_SELECTION;
            break;
        case Constant.CABOCHA_OUTPUT_DEP:
            this.outputLayer = OutputLayerType.OUTPUT_DEP;
            break;
        default:
            throw new IllegalArgumentException("unknown output layer: " + param.getInt(Param.OUTPUT_LAYER) + "\n");
        }
        switch (param.getInt(Param.OUTPUT_FORMAT)) {
        case Constant.CABOCHA_FORMAT_TREE:
            this.outputFormat = FormatType.FORMAT_TREE;
            break;
        case Constant.CABOCHA_FORMAT_LATTICE:
            this.outputFormat = FormatType.FORMAT_LATTICE;
            break;
        case Constant.CABOCHA_FORMAT_TREE_LATTICE:
            this.outputFormat = FormatType.FORMAT_TREE_LATTICE;
            break;
        case Constant.CABOCHA_FORMAT_XML:
            this.outputFormat = FormatType.FORMAT_XML;
            break;
        case Constant.CABOCHA_FORMAT_CONLL:
            this.outputFormat = FormatType.FORMAT_CONLL;
            break;
        case Constant.CABOCHA_FORMAT_NONE:
            this.outputFormat = FormatType.FORMAT_NONE;
            break;
        default:
            throw new IllegalArgumentException("unknown output format: " + param.getInt(Param.OUTPUT_FORMAT) + "\n");
        }
    }

    /**
     * Initialization for parser, which should be called before parsing.
     *
     * @throws IOException IOexception will be thrown when error occurs in reading
     *                     files (such as model file, resource file)
     */
    public void open() throws IOException {
        switch (this.inputLayer) {
        case INPUT_RAW_SENTENCE: // case 1
        {
            switch (this.outputLayer) {
            case OUTPUT_POS:
                pushAnalyzer(new MorphAnalyzer());
                break;
            case OUTPUT_CHUNK:
                pushAnalyzer(new MorphAnalyzer());
                pushAnalyzer(new Chunker());
                break;
            case OUTPUT_SELECTION:
                pushAnalyzer(new MorphAnalyzer());
                pushAnalyzer(new Chunker());
                pushAnalyzer(new Selector());
                break;
            case OUTPUT_DEP:
                pushAnalyzer(new MorphAnalyzer());
                pushAnalyzer(new Chunker());
                pushAnalyzer(new Selector());
                pushAnalyzer(new DependencyParser());
                break;
            default:
                break;
            }
            break;
        }

        case INPUT_POS: // case 2
        {
            switch (this.outputLayer) {
            case OUTPUT_POS:
                break;
            case OUTPUT_CHUNK:
                pushAnalyzer(new Chunker());
                break;
            case OUTPUT_SELECTION:
                pushAnalyzer(new Chunker());
                pushAnalyzer(new Selector());
                break;
            case OUTPUT_DEP:
                pushAnalyzer(new Chunker());
                pushAnalyzer(new Selector());
                pushAnalyzer(new DependencyParser());
                break;
            default:
                break;
            }
            break;
        }

        case INPUT_CHUNK: // case 3
        {
            switch (this.outputLayer) {
            case OUTPUT_POS:
            case OUTPUT_CHUNK:
                break;
            case OUTPUT_SELECTION:
                pushAnalyzer(new Selector());
                break;
            case OUTPUT_DEP:
                pushAnalyzer(new Selector());
                pushAnalyzer(new DependencyParser());
                break;
            default:
                break;
            }
            break;
        }

        case INPUT_SELECTION: // case 4
        {
            switch (this.outputLayer) {
            case OUTPUT_POS:
            case OUTPUT_CHUNK:
            case OUTPUT_SELECTION:
                break;
            case OUTPUT_DEP:
                pushAnalyzer(new DependencyParser());
                break;
            default:
                break;
            }
            break;
        }

        default:
            break;
        }
    }

    private void pushAnalyzer(Analyzer analyzer) throws IOException {
        analyzer.open(param);
        this.analyzerList.add(analyzer);
    }

    /**
     * Parse a given sentence.
     *
     * @param tree the sentence tree to be parsed
     * @return a tree object will be returned if the parsing is success, otherwise
     *         return null
     */
    public Tree parse(Tree tree) {
        tree.setOutputLayer(this.outputLayer);
        for (Analyzer analyzer : analyzerList) {
            analyzer.parse(tree);
        }
        return tree;
    }

    /**
     * Parse a given sentence.
     *
     * @param text the sentence to be parsed
     * @return a tree object will be returned
     */
    public Tree parse(String text) {
        Tree tree = new Tree();

        try {
            tree.read(text, inputLayer);
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("Format error: [" + text + "] ", e);
        }

        return parse(tree);
    }

    /**
     * Parse a given sentence.
     *
     * @param sent the sentence to be parsed
     * @return a string will be returned if the parsing is success, otherwise return
     *         null
     */
    public String parseToString(String sent) {
        Tree tree = parse(sent);
        return tree.toString(outputFormat);
    }

}
