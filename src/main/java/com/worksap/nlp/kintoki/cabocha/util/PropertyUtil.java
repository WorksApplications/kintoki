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

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

public class PropertyUtil {

    private PropertyUtil() {
    }

    public static boolean getPatterns(Map<String, String> patterns) throws IOException {
        Properties prop = new Properties();
        try (InputStream input = PropertyUtil.class.getClassLoader().getResourceAsStream("patterns.properties")) {
            prop.load(input);
            patterns.put("KUTOUTEN_PAT", prop.getProperty("KUTOUTEN_PAT"));
            patterns.put("OPEN_BRACKET_PAT", prop.getProperty("OPEN_BRACKET_PAT"));
            patterns.put("CLOSE_BRACKET_PAT", prop.getProperty("CLOSE_BRACKET_PAT"));
            patterns.put("DYN_A_PAT", prop.getProperty("DYN_A_PAT"));
            patterns.put("CASE_PAT", prop.getProperty("CASE_PAT"));
            patterns.put("UNIDIC_FUNC_PAT", prop.getProperty("UNIDIC_FUNC_PAT"));
            patterns.put("UNIDIC_HEAD_PAT", prop.getProperty("UNIDIC_HEAD_PAT"));
            patterns.put("UNIDIC_FUNC_PAT2", prop.getProperty("UNIDIC_FUNC_PAT2"));
            patterns.put("UNIDIC_HEAD_PAT2", prop.getProperty("UNIDIC_HEAD_PAT2"));
            patterns.put("UNIDIC_HEAD_PRE_PAT", prop.getProperty("UNIDIC_HEAD_PRE_PAT"));
        }
        return true;
    }

}
