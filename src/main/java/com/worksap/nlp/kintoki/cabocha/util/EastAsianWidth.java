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

package com.worksap.nlp.kintoki.cabocha.util;

import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class EastAsianWidth {
    static {
        wideRanges = new ArrayList<>();
        try {
            loadDefinition(EastAsianWidth.class.getResourceAsStream("/EastAsianWidth.txt"));
        } catch (Throwable ignore) {
            // do nothing
            // getEastAsianWidth() always return String#length()
        }
    }

    static class Range {
        private int begin;
        private int end;

        Range(int begin, int end) {
            this.begin = begin;
            this.end = end;
        }

        boolean contains(int i) {
            return begin <= i && i < end;
        }
    }

    static List<Range> wideRanges;

    private EastAsianWidth() {
    }

    public static int getEastAsianWidth(String text) {
        return text.codePoints().map(c -> wideRanges.stream().anyMatch(r -> r.contains(c)) ? 2 : 1).sum();
    }

    private static void loadDefinition(InputStream input) throws IOException {
        try (Scanner scanner = new Scanner(input)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().replaceFirst("\\s*#.*", "");
                if (line.isEmpty()) {
                    continue;
                }
                String[] columns = line.split(";");
                if (columns[1].equals("F") || columns[1].equals("W") || columns[1].equals("A")) {
                    String[] range = columns[0].split("\\.\\.");
                    if (range.length == 1) {
                        int codePoints = Integer.parseInt(range[0], 16);
                        wideRanges.add(new Range(codePoints, codePoints + 1));
                    } else {
                        int beginCodePoint = Integer.parseInt(range[0], 16);
                        int endCodePoint = Integer.parseInt(range[1], 16);
                        wideRanges.add(new Range(beginCodePoint, endCodePoint + 1));
                    }
                }
            }
        }
    }
}