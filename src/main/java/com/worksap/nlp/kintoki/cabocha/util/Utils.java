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

import com.worksap.nlp.kintoki.cabocha.Token;

public class Utils {

    private Utils() {}

    public static boolean check(String str){
        return str != null && !str.trim().isEmpty();
    }

    public static String concatFeature(Token token, int size) {
        StringBuilder output = new StringBuilder();
        int minSize = Math.min(token.getFeatureListSize(), size);
        for (int i = 0; i < minSize; ++i) {
            if (("*").equals(token.getFeatureList().get(i))) {
                break;
            }
            if (i != 0) {
                output.append("-");
            }
            output.append(token.getFeatureList().get(i));
        }
        return output.toString();
    }

}
