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

import org.junit.Test;

import java.io.File;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class ParamTest {

    final static String systemName = System.getProperty("java.home") + "/bin/java -jar "
            + new File(Param.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getName();
    final static String version = Constant.PACKAGE + " of " + Constant.VERSION + "\n";
    final static String help = "Kintoki-CaboCha\n"
            + "Copyright(C) Works Applications, All rights reserved.\n"
            + "\n"
            + "Usage: " + systemName + " [options] files\n"
            + " -f, --output-format=TYPE  set output format style\n"
            + "\t\t\t    0 - tree(default)\n"
            + "\t\t\t    1 - lattice\n"
            + "\t\t\t    2 - tree + lattice\n"
            + "\t\t\t    3 - XML\n"
            + "\t\t\t    4 - CoNLL\n"
            + " -I, --input-layer=LAYER   set input layer\n"
            + "\t\t\t    0 - raw sentence layer(default)\n"
            + "\t\t\t    1 - POS tagged layer\n"
            + "\t\t\t    2 - POS tagger and Chunked layer\n"
            + "\t\t\t    3 - POS tagged, Chunked and Feature selected layer\n"
            + " -O, --output-layer=LAYER  set output layer\n"
            + "\t\t\t    1 - POS tagged layer\n"
            + "\t\t\t    2 - POS tagged and Chunked layer\n"
            + "\t\t\t    3 - POS tagged, Chunked and Feature selected layer\n"
            + "\t\t\t    4 - Parsed layer(default)\n"
            + " -m, --parser-model=FILE   use FILE as parser model file\n"
            + " -M, --chunker-model=FILE  use FILE as chunker model file\n"
            + " -r, --rcfile=FILE         use FILE as resource file\n"
            + " -d, --sudachi-dict=DIR    use DIR as sudachi dictionary directory\n"
            + " -o, --output=FILE         use FILE as output file\n"
            + " -v, --version             show the version and exit\n"
            + " -h, --help                show this help and exit\n\n";

    @Test
    public void initParam() {
        Param param = new Param();
        param.setSystemName(systemName);
        assertNull(param.getHelp());
        assertNull(param.getVersion());

        param.initParam(Cabocha.longOptions);

        assertEquals(help, param.getHelp());
        assertEquals(version, param.getVersion());
    }

    public void open() {
        Param param = new Param();
        String dep = "dep.bccwj.model";
        String chunk = "chunk.bccwj.model";
        String rc = "cabocharc.properties";
        String dic = "./";
        String input[] = { "input_file_1", "input_file_2" };
        String output = "output_file";
        String[] args1 = { "-I1", "-O3", "-f2", "-m", dep, "-M", chunk, "-d", dic, "-r", rc, "-o", output, "-v", "-h" };
        String[] args2 = { "--input-layer=1", "--output-layer=3", "--output-format=2", "--parser-model=" + dep,
                "--chunker-model=" + chunk, "--sudachi-dict=" + dic, "--rcfile=" + rc, "--output=" + output,
                "--version", "--help" };

        args1 = Stream.of(args1, input).flatMap(Stream::of).toArray(String[]::new);
        param.open(args1, Cabocha.longOptions);

        assertEquals(1, param.getInt(Param.INPUT_LAYER));
        assertEquals(3, param.getInt(Param.OUTPUT_LAYER));
        assertEquals(2, param.getInt(Param.OUTPUT_FORMAT));
        assertEquals(dep, param.getString(Param.PARSER_MODEL));
        assertEquals(chunk, param.getString(Param.CHUNKER_MODEL));
        assertEquals(dic, param.getString(Param.SUDACHI_DICT));
        assertEquals(rc, param.getString(Param.RC_FILE));
        assertEquals(output, param.getString(Param.OUTPUT));
        assertArrayEquals(input, param.getRest().toArray());
        assertEquals("1", param.getString(Param.HELP));
        assertEquals("1", param.getString(Param.VERSION));

        param = new Param();
        args2 = Stream.of(args2, input).flatMap(Stream::of).toArray(String[]::new);
        param.open(args2, Cabocha.longOptions);

        assertEquals(1, param.getInt(Param.INPUT_LAYER));
        assertEquals(3, param.getInt(Param.OUTPUT_LAYER));
        assertEquals(2, param.getInt(Param.OUTPUT_FORMAT));
        assertEquals(dep, param.getString(Param.PARSER_MODEL));
        assertEquals(chunk, param.getString(Param.CHUNKER_MODEL));
        assertEquals(dic, param.getString(Param.SUDACHI_DICT));
        assertEquals(rc, param.getString(Param.RC_FILE));
        assertEquals(output, param.getString(Param.OUTPUT));
        assertArrayEquals(input, param.getRest().toArray());
        assertEquals("1", param.getString(Param.HELP));
        assertEquals("1", param.getString(Param.VERSION));
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalArgumentTest() {
        Param param = new Param();
        String dep = "dep.bccwj.model";
        String chunk = "chunk.bccwj.model";
        String rc = "cabocharc.properties";
        String dic = "./";
        String output = "output_file";
        String[] args = { "--I1", "-O3", "-f2", "-m", dep, "-M", chunk, "-d", dic, "-r", rc, "-o", output, "-v", "-h" };

        param.open(args, Cabocha.longOptions);
    }

    @Test
    public void geString() {
        Param param = new Param();
        param.set(Param.OUTPUT, "foo");
        assertEquals("foo", param.getString(Param.OUTPUT));
        assertEquals(null, param.getString("Foo"));
    }

    @Test
    public void getInt() {
        Param param = new Param();
        param.set(Param.INPUT_LAYER, 1);
        assertEquals(1, param.getInt(Param.INPUT_LAYER));
        assertEquals(0, param.getInt("Foo"));
    }
}