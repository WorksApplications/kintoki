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

public class Option {

    private String name;
    private char shortName;
    private Object defaultValue;
    private String argDescription;
    private String description;

    public Option(String name, char shortName, Object defaultValue, String argDescription, String description) {
        this.name = name;
        this.shortName = shortName;
        this.defaultValue = defaultValue;
        this.argDescription = argDescription;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public char getShortName() {
        return shortName;
    }

    public void setShortName(char shortName) {
        this.shortName = shortName;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getArgDescription() {
        return argDescription;
    }

    public void setArgDescription(String argDescription) {
        this.argDescription = argDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
