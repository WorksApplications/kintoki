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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import com.worksap.nlp.sudachi.dictionary.build.DicBuilder;

public class TestUtils {
    static final String[] RESOURCES = { "/chunk.bccwj.model", "/dep.bccwj.model", "/sudachi.json", "/input",
            "/input2" };

    static final String PROPERTY_FILE = "/cabocharc.properties";
    static final String REPLACE_DIR = "@@TEST_DIR@@";
    static final String INPUT_FILE = "/input";
    static final String INPUT_FILE2 = "/input2";
    static final String OUTPUT_FILE = "/output";

    public static void copyResources(Path folder) throws IOException {
        for (String file : RESOURCES) {
            try {
                URL src = TestUtils.class.getResource(file);
                Path dest = Paths.get(src.toURI()).getFileName();
                Files.copy(src.openStream(), folder.resolve(dest));
            } catch (URISyntaxException e) {
                throw new IOException(e);
            }
        }
        buildSudachiDict(folder);
    }

    static void buildSudachiDict(Path path) throws IOException {
        InputStream matrixDef = TestUtils.class.getResourceAsStream("/sudachi_matrix.def");
        InputStream lexicon = TestUtils.class.getResourceAsStream("/sudachi_dict.csv");
        DicBuilder.System builder = DicBuilder.system().matrix(matrixDef).lexicon(lexicon);
        builder.build(Files.newByteChannel(path.resolve("system.dic"), StandardOpenOption.WRITE,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING));
    }

    static String buildConfig(Path folder) throws IOException {
        Path dest = Paths.get(PROPERTY_FILE).getFileName();
        Path configPath = folder.resolve(dest);
        String dir = folder.toString();
        try (InputStream in = TestUtils.class.getResourceAsStream(PROPERTY_FILE + ".in");
                InputStreamReader r = new InputStreamReader(in);
                BufferedReader reader = new BufferedReader(r);
                PrintStream output = new PrintStream(configPath.toFile())) {
            reader.lines().forEach(l -> output.println(l.replace(REPLACE_DIR, dir)));
        }
        return configPath.toString();
    }

    static String getInput(Path folder) throws IOException {
        Path dest = Paths.get(INPUT_FILE).getFileName();
        Path configPath = folder.resolve(dest);
        return configPath.toString();
    }

    static String getInput2(Path folder) throws IOException {
        Path dest = Paths.get(INPUT_FILE2).getFileName();
        Path configPath = folder.resolve(dest);
        return configPath.toString();
    }

    static String getOutput(Path folder) throws IOException {
        Path dest = Paths.get(OUTPUT_FILE).getFileName();
        Path configPath = folder.resolve(dest);
        return configPath.toString();
    }
}
