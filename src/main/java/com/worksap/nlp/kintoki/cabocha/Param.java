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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Param {

    public static final String INPUT_LAYER = "input-layer";
    public static final String OUTPUT_LAYER = "output-layer";
    public static final String PARSER_MODEL = "parser-model";
    public static final String CHUNKER_MODEL = "chunker-model";
    public static final String SUDACHI_DICT = "sudachi-dict";
    public static final String OUTPUT_FORMAT = "output-format";
    public static final String RC_FILE = "rcfile";
    public static final String OUTPUT = "output";

    static final Pattern LONG_OPTION_PATTERN = Pattern.compile("--(\\S+?)(?:=(\\S+))?");
    static final Pattern SHORT_OPTION_PATTERN = Pattern.compile("-([\\S&&[^-]])(\\S+)?");

    private static List<String> keyList = Arrays.asList(INPUT_LAYER, OUTPUT_LAYER, PARSER_MODEL, CHUNKER_MODEL,
            SUDACHI_DICT, OUTPUT_FORMAT);

    private Map<String, Object> conf = new HashMap<>();
    private List<String> rest = new ArrayList<>();

    public Object get(String key) {
        return conf.get(key);
    }

    public String getString(String key) {
        Object value = conf.get(key);
        if (value instanceof String) {
            return (String) value;
        } else {
            return null;
        }
    }

    public int getInt(String key) {
        Object value = conf.get(key);
        if (value instanceof Integer) {
            return (Integer) value;
        } else if (value instanceof String) {
            return Integer.parseInt((String) value);
        } else {
            return 0;
        }
    }

    public void set(String key, Object value) {
        conf.put(key, value);
    }

    public List<String> getRest() {
        return rest;
    }

    public void loadConfig() throws IOException {
        Properties prop = new Properties();
        try (InputStream inputStream = Param.class.getClassLoader().getResourceAsStream("cabocharc.properties")) {
            prop.load(inputStream);
        }
        initParam(prop);
    }

    public void loadConfig(String configPath) throws IOException {
        Properties prop = new Properties();
        try (InputStream inputStream = new FileInputStream(configPath)) {
            prop.load(inputStream);
        }
        initParam(prop);
    }

    private void initParam(Properties prop) {
        for (String key : keyList) {
            String value = prop.getProperty(key);
            conf.put(key, value);
        }
    }

    public void update(Param param) {
        this.conf.putAll(param.conf);
        this.rest.addAll(param.rest);
    }

    static Param open(String[] args, Option[] options) {
        Param param = new Param();

        Stream.of(options).filter(o -> o.getDefaultValue() != null)
                .forEach(o -> param.set(o.getName(), o.getDefaultValue()));

        Iterator<String> iterator = Stream.of(args).iterator();
        while (iterator.hasNext()) {
            String arg = iterator.next();
            if (!parseOption(arg, param, iterator, options, true)
                    && !parseOption(arg, param, iterator, options, false)) {
                param.rest.add(arg);
            }
        }
        return param;
    }

    private static boolean parseOption(String arg, Param param, Iterator<String> args, Option[] options,
            boolean isShort) {
        Pattern optionPattern = isShort ? SHORT_OPTION_PATTERN : LONG_OPTION_PATTERN;
        Matcher matcher = optionPattern.matcher(arg);
        if (!matcher.matches()) {
            return false;
        }
        String name = matcher.group(1);
        String value = matcher.group(2);
        Option option = getOption(options, name, isShort, arg);
        if (option.getArgDescription() != null) {
            if (value == null) {
                if (!args.hasNext()) {
                    throw new IllegalArgumentException("`" + arg + "` requires an argument");
                }
                value = args.next();
            }
            param.set(option.getName(), value);
        } else {
            if (value != null) {
                throw new IllegalArgumentException("`" + arg + "` doesn't allow an argument");
            }
            param.set(option.getName(), "1");
        }
        return true;
    }

    private static Option getOption(Option[] options, String name, boolean isShort, String arg) {
        Optional<Option> result = Stream.of(options)
                .filter(o -> isShort ? o.getShortName().equals(name) : o.getName().equals(name)).findFirst();
        if (!result.isPresent()) {
            throw new IllegalArgumentException("unrecognized option `" + arg + "`");
        }
        return result.get();
    }
}
