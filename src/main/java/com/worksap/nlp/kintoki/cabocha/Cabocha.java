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

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.List;

public class Cabocha {

    static class FileStdoutStream implements Closeable {
        PrintStream output;
        boolean isFile;

        FileStdoutStream(String fileName) throws IOException {
            if (Utils.check(fileName)) {
                output = new PrintStream(fileName);
                isFile = true;
            } else {
                output = System.out;
            }
        }

        void print(String x) {
            output.print(x);
        }

        @Override
        public void close() {
            if (isFile) {
                output.close();
            }
        }
    }

    static final Option[] longOptions = {
            new Option("output-format", 'f', 0, "TYPE",
                    "set output format style\n\t\t\t    " + "0 - tree(default)\n\t\t\t    " + "1 - lattice\n\t\t\t    "
                            + "2 - tree + lattice\n\t\t\t    " + "3 - XML\n\t\t\t    " + "4 - CoNLL"),
            new Option("input-layer", 'I', 0, "LAYER",
                    "set input layer\n\t\t\t    " + "0 - raw sentence layer(default)\n\t\t\t    "
                            + "1 - POS tagged layer\n\t\t\t    " + "2 - POS tagger and Chunked layer\n\t\t\t    "
                            + "3 - POS tagged, Chunked and Feature selected layer"),
            new Option("output-layer", 'O', 4, "LAYER", "set output layer\n\t\t\t    "
                    + "1 - POS tagged layer\n\t\t\t    " + "2 - POS tagged and Chunked layer\n\t\t\t    "
                    + "3 - POS tagged, Chunked and Feature selected layer\n\t\t\t    " + "4 - Parsed layer(default)"),
            new Option("parser-model", 'm', null, "FILE", "use FILE as parser model file"),
            new Option("chunker-model", 'M', null, "FILE", "use FILE as chunker model file"),
            new Option("rcfile", 'r', null, "FILE", "use FILE as resource file"),
            new Option("sudachi-dict", 'd', null, "DIR", "use DIR as sudachi dictionary directory"),
            new Option("output", 'o', null, "FILE", "use FILE as output file"),
            new Option("version", 'v', null, null, "show the version and exit"),
            new Option("help", 'h', null, null, "show this help and exit"), };

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
     * @param config
     *            the path of configuration file
     */
    public Cabocha(String config) throws IOException {
        parser = new Parser(config);
        parser.open();
    }

    /**
     * Create a new instance of Cabocha class.
     *
     * @param param
     *            configuration parameters
     */
    public Cabocha(Param param) throws IOException {
        parser = new Parser(param);
        parser.open();
    }

    /**
     * Parse a given sentence.
     *
     * @param sent
     *            the sentence to be parsed
     * @return a tree object will be returned if the parsing is success, otherwise
     *         return null
     * @throws IOException
     *             IOexception will be thrown when error occurs in reading files
     *             (such as model file, resource file)
     */
    public Tree parse(String sent) throws IOException {
        return parser.parse(sent);
    }

    /**
     * Parse a given sentence tree.
     *
     * @param sent
     *            the sentence tree to be parsed
     * @return a tree object will be returned if the parsing is success, otherwise
     *         return null
     * @throws IOException
     *             IOexception will be thrown when error occurs in reading files
     *             (such as model file, resource file)
     */
    public Tree parse(Tree sent) throws IOException {
        return parser.parse(sent);
    }

    /**
     * Parse a given sentence.
     *
     * @param sent
     *            the sentence to be parsed
     * @return the result of parsing in LATTICE format will be returned
     * @throws IOException
     *             IOexception will be thrown when error occurs in reading files
     *             (such as model file, resource file)
     */
    public String parseToString(String sent) throws IOException {
        Tree tree = parser.parse(sent);
        return tree.toString(FormatType.FORMAT_LATTICE);
    }

    /**
     * Parse from command line.
     *
     * @param args
     *            command line arguments
     * @throws IOException
     *             IOexception will be thrown when error occurs in reading files
     *             (such as model file, resource file)
     */
    public static void parse(String[] args) throws IOException {
        Param param = buildParam(args);
        Parser parser = new Parser(param);
        parser.open();

        int inputLayer = param.getInt(Param.INPUT_LAYER);
        List<String> rest = param.getRest();

        try (FileStdoutStream output = new FileStdoutStream(param.getString(Param.OUTPUT))) {
            if (rest.isEmpty()) {
                run(parser, inputLayer, System.in, output);
            } else {
                for (String inputFileName : rest) {
                    try (FileInputStream input = new FileInputStream(inputFileName)) {
                        run(parser, inputLayer, input, output);
                    }
                }
            }
        }

    }

    private static void run(Parser parser, int inputLayer, InputStream input, FileStdoutStream output)
            throws IOException {
        try (InputStreamReader inputReader = new InputStreamReader(input);
                BufferedReader reader = new BufferedReader(inputReader)) {
            String sentence = null;
            while ((sentence = readSentence(reader, inputLayer)) != null) {
                output.print(parser.parseToString(sentence));
            }
        }
    }

    private static String readSentence(BufferedReader input, int inputLayer) throws IOException {
        if (inputLayer == Constant.CABOCHA_INPUT_RAW_SENTENCE) {
            return input.readLine();
        } else {
            StringBuilder sb = new StringBuilder();
            String line = null;
            int lineNum = 0;
            while ((line = input.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.equals("EOS")) {
                    break;
                }
                sb.append(line).append('\n');
                if (++lineNum > Constant.CABOCHA_MAX_LINE_SIZE) {
                    throw new IllegalArgumentException(
                            "too long line #line must be <= " + Constant.CABOCHA_MAX_LINE_SIZE);
                }
            }
            if (sb.length() != 0) {
                return sb.toString();
            } else {
                return null;
            }
        }
    }

    private static Param buildParam(String[] args) throws IOException {
        Param param = Param.open(args, longOptions);

        String help = helpVersion(param);
        if (help != null) {
            System.out.println(help);
            System.exit(0);
        }

        Param newParam = new Param();
        String rcfile = param.getString(Param.RC_FILE);
        if (Utils.check(rcfile)) {
            newParam.loadConfig(rcfile);
        } else {
            newParam.loadConfig();
        }

        newParam.update(param);

        if (newParam.getInt(Param.OUTPUT_LAYER) != Constant.CABOCHA_OUTPUT_DEP) {
            newParam.set(Param.OUTPUT_FORMAT, Constant.CABOCHA_FORMAT_LATTICE);
        }

        return newParam;
    }

    static String helpVersion(Param param) {
        if (Utils.check(param.getString("help"))) {
            String systemName = System.getProperty("java.home") + "/bin/java -jar "
                    + new File(Param.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getName();
            return Option.buildHelpMessage(longOptions, systemName);
        } else if (Utils.check(param.getString("version"))) {
            return Constant.PACKAGE + " of " + Constant.VERSION;
        }
        return null;
    }

    public static void main(String[] args) throws IOException {
        parse(args);
    }
}
