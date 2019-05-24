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

import java.io.*;
import java.util.List;
import java.util.Scanner;

public class Cabocha {

    public final static Option longOptions[] = {
            new Option("output-format",   'f', 0, "TYPE",
                    "set output format style\n\t\t\t    "+
                    "0 - tree(default)\n\t\t\t    "+
                    "1 - lattice\n\t\t\t    "+
                    "2 - tree + lattice\n\t\t\t    "+
                    "3 - XML\n\t\t\t    "+
                    "4 - CoNLL" ),
            new Option("input-layer",     'I', 0,
                    "LAYER", "set input layer\n\t\t\t    "+
                    "0 - raw sentence layer(default)\n\t\t\t    "+
                    "1 - POS tagged layer\n\t\t\t    "+
                    "2 - POS tagger and Chunked layer\n\t\t\t    "+
                    "3 - POS tagged, Chunked and Feature selected layer"),
            new Option("output-layer",    'O', 4,
                    "LAYER", "set output layer\n\t\t\t    "+
                    "1 - POS tagged layer\n\t\t\t    "+
                    "2 - POS tagged and Chunked layer\n\t\t\t    "+
                    "3 - POS tagged, Chunked and Feature selected layer\n\t\t\t    "+
                    "4 - Parsed layer(default)"),
            new Option("parser-model",    'm', null, "FILE", "use FILE as parser model file"),
            new Option("chunker-model",   'M', null, "FILE", "use FILE as chunker model file"),
            new Option( "rcfile",          'r', null, "FILE", "use FILE as resource file" ),
            new Option( "sudachi-dict",    'd', null, "DIR",  "use DIR as sudachi dictionary directory"),
            new Option( "output",          'o', null, "FILE", "use FILE as output file"),
            new Option( "version",         'v', null, null, "show the version and exit"),
            new Option( "help",            'h', null, null, "show this help and exit"),
    };

    private Parser parser = null;

    /**
     * Create a new instance of Cabocha class.
     */
    public Cabocha() throws IOException {
        parser = new Parser();
        parser.open();
    }

    /**
     * Create a new instance of Cabocha class.
     *
     * @param config the path of configuration file
     */
    public Cabocha(String config) throws IOException {
        parser = new Parser(config);
        parser.open();
    }

    /**
     * Create a new instance of Cabocha class.
     *
     * @param param configuration parameters
     */
    public Cabocha(Param param) throws IOException {
        parser = new Parser(param);
        parser.open();
    }

    /**
     * Parse a given sentence.
     *
     * @param sent the sentence to be parsed
     * @return a tree object will be returned if the parsing is success, otherwise return null
     * @throws IOException IOexception will be thrown when error occurs in reading files (such as model file, resource file)
     */
    public Tree parse(String sent) throws IOException {
        Tree tree = parser.parse(sent);

        return tree;
    }

    /**
     * Parse a given sentence tree.
     *
     * @param sent the sentence tree to be parsed
     * @return a tree object will be returned if the parsing is success, otherwise return null
     * @throws IOException IOexception will be thrown when error occurs in reading files (such as model file, resource file)
     */
    public Tree parse(Tree sent) throws IOException {
        Tree tree = parser.parse(sent);

        return tree;
    }

    /**
     * Parse a given sentence.
     *
     * @param sent the sentence to be parsed
     * @return a string will be returned if the parsing is success, otherwise return null
     * @throws IOException IOexception will be thrown when error occurs in reading files (such as model file, resource file)
     */
    public String parseToString(String sent) throws IOException {
        StringBuilder result = new StringBuilder();
        Tree tree = parser.parse(sent);

        if (tree.getChunks() != null) {
            for (int i=0; i<tree.getChunkSize(); i++) {
                Chunk chunk = tree.chunk(i);
                result.append("* "+i+" "+chunk.getLink()+"D ");
                result.append(chunk.getHeadPos()+"/"+chunk.getFuncPos()+" ");
                result.append(chunk.getScore()+"\n");
                for (int j=0; j<chunk.getTokenSize();j++) {
                    Token token = chunk.token(j);
                    result.append(token.getSurface()+"\t"+token.getFeature()+"\n");
                }
            }
            result.append("EOS\n");
        }else {
            return null;
        }

        return result.toString();
    }

    /**
     * Parse from command line.
     *
     * @param args command line arguments
     * @throws IOException IOexception will be thrown when error occurs in reading files (such as model file, resource file)
     */
    public static void parse(String[] args) throws IOException {
        Param param = new Param();
        param.open(args, longOptions);

        if (param.helpVersion()) {
            return;
        }

        String outputFile = param.getString(Param.OUTPUT);
        if (Utils.check(outputFile)) {
            File f = new File(outputFile);
            if(f.exists() && f.isDirectory()) {
                System.out.println("no such file or directory: " + outputFile);
                return;
            }
        }

        Param newParam = new Param();
        String rcfile = param.getString(Param.RC_FILE);
        if (Utils.check(rcfile))
            newParam.loadConfig(rcfile);
        else
            newParam.loadConfig();

        newParam.update(param);

        if (newParam.getInt(Param.OUTPUT_LAYER) != Constant.CABOCHA_OUTPUT_DEP) {
            newParam.set(Param.OUTPUT_FORMAT, Constant.CABOCHA_FORMAT_LATTICE);
        }

        Parser parser = new Parser(newParam);
        parser.open();

        int inputLayer = newParam.getInt(Param.INPUT_LAYER);
        List<String> rest = param.getRest();

        if (Utils.check(outputFile)) {
            try (FileWriter writer = new FileWriter(outputFile);
                 BufferedWriter bw = new BufferedWriter(writer)) {
                if (rest.isEmpty()){
                    oneLineParse(parser, bw);
                } else {
                    batchParse(parser, rest, inputLayer, bw);
                }
            }
        } else {
            if (rest.isEmpty()){
                oneLineParse(parser, null);
            } else {
                batchParse(parser, rest, inputLayer, null);
            }
        }

    }

    private static void oneLineParse(Parser parser, BufferedWriter bw) throws IOException {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String line = scanner.nextLine();
            String result = parser.parseToString(line);
            if (result == null)continue;
            if (bw != null) {
                bw.write(result);
            } else {
                System.out.print(result);
            }
        }
    }

    private static void batchParse(Parser parser, List<String> inputFiles, int inputLayer, BufferedWriter bw) throws IOException {
        for (String inputFile: inputFiles) {
            String line = null;
            StringBuilder sb = new StringBuilder();

            try (FileReader reader = new FileReader(inputFile);
                 BufferedReader br = new BufferedReader(reader)) {
                while ((line = br.readLine()) != null) {
                    if (inputLayer == Constant.CABOCHA_INPUT_RAW_SENTENCE) {
                        sb.append(line);
                    } else {
                        int lineNum = 0;
                        sb.append(line);
                        sb.append("\n");
                        if (++lineNum > Constant.CABOCHA_MAX_LINE_SIZE) {
                            throw new IllegalStateException("too long line #line must be <= " + Constant.CABOCHA_MAX_LINE_SIZE);
                        }
                        if (!line.trim().equals("EOS")) {
                            continue;
                        }
                    }

                    String result = parser.parseToString(sb.toString());
                    if (result == null)continue;
                    if (bw != null) {
                        bw.write(result);
                    } else {
                        System.out.print(result);
                    }

                    sb = new StringBuilder();
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        parse(args);
    }

}
