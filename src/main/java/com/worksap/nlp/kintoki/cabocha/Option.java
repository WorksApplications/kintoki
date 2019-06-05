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

public class Option {

    private String name;
    private String shortName;
    private Object defaultValue;
    private String argDescription;
    private String description;

    public Option(String name, char shortName, Object defaultValue, String argDescription, String description) {
        this.name = name;
        this.shortName = new String(new char[] { shortName });
        this.defaultValue = defaultValue;
        this.argDescription = argDescription;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getShortName() {
        return shortName;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public String getArgDescription() {
        return argDescription;
    }

    public String getDescription() {
        return description;
    }

    static String buildHelpMessage(Option[] opts, String systemName) {
        StringBuilder sb = new StringBuilder();
        sb.append(Constant.COPYRIGHT);
        sb.append("\nUsage: ");
        sb.append(systemName);
        sb.append(" [options] files\n");

        int max = 0;
        for (Option opt : opts) {
            int l = 1 + opt.getName().length();
            if (Utils.check(opt.getArgDescription())) {
                l += (1 + opt.getArgDescription().length());
            }
            max = Math.max(l, max);
        }

        for (Option opt : opts) {
            int l = opt.getName().length();
            if (Utils.check(opt.getArgDescription()))
                l += (1 + opt.getArgDescription().length());
            sb.append(" -");
            sb.append(opt.getShortName());
            sb.append(", --");
            sb.append(opt.getName());
            if (Utils.check(opt.getArgDescription())) {
                sb.append("=");
                sb.append(opt.getArgDescription());
            }
            for (; l <= max; l++) {
                sb.append(" ");
            }
            sb.append(opt.getDescription());
            sb.append("\n");
        }

        return sb.toString();
    }
}
