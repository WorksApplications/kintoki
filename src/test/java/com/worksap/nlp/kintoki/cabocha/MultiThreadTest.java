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

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class MultiThreadTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();
    String configPath;

    @Before
    public void setUp() throws IOException {
        TestUtils.copyResources(temporaryFolder.getRoot().toPath());
        configPath = TestUtils.buildConfig(temporaryFolder.getRoot().toPath());
    }

    private static Map<Integer, List<String>> mapResult = new HashMap<>();

    public synchronized static void AddResult(int id, String result) {
        if (!mapResult.containsKey(id)) {
            mapResult.put(id, new ArrayList<>());
        }
        mapResult.get(id).add(result);
    }

    public static class CabochaTestThread implements Runnable {

        private int id;
        private int testCount;
        private String testText;
        private String config;

        public CabochaTestThread(int id, int count, String config, String text) {
            this.id = id;
            this.testCount = count;
            this.config = config;
            this.testText = text;
        }

        @Override
        public void run() {
            for (int i = 0; i < testCount; i++) {
                try {
                    Cabocha cabocha = new Cabocha(config);
                    Tree tree = cabocha.parse(testText);
                    String result = TestHelper.getResultString(tree);
                    AddResult(id, result);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Test
    public void testCabocha() throws InterruptedException, IOException {
        final String sent = "太郎は花子が読んでいる本を次郎に渡した。";

        mapResult.clear();

        Cabocha cabocha = new Cabocha(configPath);
        Tree tree = cabocha.parse(sent);
        final String trueResult = TestHelper.getResultString(tree);

        Integer[] threadIds = { 1, 2, 3, 4 };
        Thread[] threads = new Thread[threadIds.length];
        final int testCount = 10;

        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(new CabochaTestThread(threadIds[i], testCount, configPath, sent));
            threads[i].start();
        }

        for (Thread t : threads) {
            t.join();
        }

        assertArrayEquals(threadIds, mapResult.keySet().stream().sorted().toArray(Integer[]::new));

        for (Integer id : threadIds) {
            List<String> rl = mapResult.get(id);
            assertEquals(testCount, rl.size());
            for (String res : rl) {
                assertEquals(trueResult, res);
            }
        }

        mapResult.clear();
    }

}
