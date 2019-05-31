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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class Param {

    public static final String INPUT_LAYER = "input-layer";
    public static final String OUTPUT_LAYER = "output-layer";
    public static final String PARSER_MODEL = "parser-model";
    public static final String CHUNKER_MODEL = "chunker-model";
    public static final String SUDACHI_DICT = "sudachi-dict";
    public static final String ACTION_MODE = "action-mode";
    public static final String OUTPUT_FORMAT = "output-format";
    public static final String NBEST = "nbest";
    public static final String VERBOSE = "verbose";
    public static final String COST_FACTOR = "cost-factor";
    public static final String RC_FILE = "rcfile";
    public static final String OUTPUT = "output";
    public static final String HELP = "help";
    public static final String VERSION = "version";

    private static Map<String, Class> keyTypes;
    static {
        keyTypes = new HashMap<>();
        keyTypes.put(Param.INPUT_LAYER, Integer.class);
        keyTypes.put(Param.OUTPUT_LAYER, Integer.class);
        keyTypes.put(Param.PARSER_MODEL, String.class);
        keyTypes.put(Param.CHUNKER_MODEL, String.class);
        keyTypes.put(Param.SUDACHI_DICT, String.class);
        keyTypes.put(Param.ACTION_MODE, Integer.class);
        keyTypes.put(Param.OUTPUT_FORMAT, Integer.class);
    }

    private Map<String, Object> conf = new HashMap<>();
    private List<String> rest = new ArrayList<>();
    private String systemName;
    private String help;
    private String version;

    public Object get(String key) {
        return conf.get(key);
    }

    public String getString(String key) {
        return (String) conf.get(key);
    }

    public int getInt(String key) {
        return (Integer) conf.get(key);
    }

    public double getDouble(String key) {
        return (Double) conf.get(key);
    }

    public void set(String key, Object value) {
        if (value instanceof String) {
            Class klass = keyTypes.get(key);
            if (klass == Integer.class) {
                conf.put(key, Integer.parseInt((String) value));
            } else if (klass == Double.class) {
                conf.put(key, Double.valueOf((String) value));
            } else {
                conf.put(key, value);
            }
        } else {
            conf.put(key, value);
        }
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public String getHelp() {
        return help;
    }

    public void setHelp(String help) {
        this.help = help;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
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
        for (Map.Entry<String, Class> entry : keyTypes.entrySet()) {
            String key = entry.getKey();
            Class klass = entry.getValue();
            String value = prop.getProperty(key);
            if (klass == Integer.class) {
                conf.put(key, Integer.parseInt(value));
            } else if (klass == Double.class) {
                conf.put(key, Double.valueOf(value));
            } else {
                conf.put(key, value);
            }
        }
    }

    public void initParam(Option[] opts) {
        StringBuilder helpMsg = new StringBuilder();
        helpMsg.append(Constant.COPYRIGHT);
        helpMsg.append("\nUsage: ");
        helpMsg.append(systemName);
        helpMsg.append(" [options] files\n");

        this.version = Constant.PACKAGE + " of " + Constant.VERSION + "\n";

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
            helpMsg.append(" -");
            helpMsg.append(opt.getShortName());
            helpMsg.append(", --");
            helpMsg.append(opt.getName());
            if (Utils.check(opt.getArgDescription())) {
                helpMsg.append("=");
                helpMsg.append(opt.getArgDescription());
            }
            for (; l <= max; l++) {
                helpMsg.append(" ");
            }
            helpMsg.append(opt.getDescription());
            helpMsg.append("\n");
        }

        helpMsg.append("\n");
        help = helpMsg.toString();
    }

    public void update(Param param) {
        this.conf.putAll(param.conf);
    }

    public void open(String[] args, Option[] opts) {
        int ind = 0;
        int argc = args.length;

        if (argc <= 0) {
            systemName = "unknown";
            return; // this is not error
        }

        systemName = System.getProperty("java.home") + "/bin/java -jar ";
        systemName += new File(Param.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getName();

        initParam(opts);

        for (int i = 0; i < opts.length; ++i) {
            if (opts[i].getDefaultValue() != null) {
                set(opts[i].getName(), opts[i].getDefaultValue());
            }
        }

        for (ind = 0; ind < argc; ind++) {
            if (args[ind].charAt(0) == '-') {
                // long options
                if (args[ind].charAt(1) == '-') {
                    String s = args[ind].substring(2);
                    String name = s.trim();
                    if (s.indexOf(' ') != -1) {
                        name = s.substring(0, s.indexOf(' ')).trim();
                    }
                    if (s.indexOf('=') != -1) {
                        name = s.substring(0, s.indexOf('=')).trim();
                    }
                    if (name.length() == 0) {
                        return;
                    }

                    boolean hit = false;
                    int i = 0;
                    for (i = 0; i < opts.length; ++i) {
                        if (Utils.check(opts[i].getName()) && opts[i].getName().equals(name)) {
                            hit = true;
                            break;
                        }
                    }

                    if (!hit) {
                        gotoFatalError(0, args[ind]);
                    }

                    if (Utils.check(opts[i].getArgDescription())) {
                        if (s.indexOf('=') != -1) {
                            String[] fields = s.split("=");
                            String value = fields.length > 1 ? fields[1].trim() : null;
                            if (!Utils.check(value)) {
                                gotoFatalError(1, args[ind]);
                            }
                            set(opts[i].getName(), value);
                        } else {
                            if (argc == (ind + 1)) {
                                gotoFatalError(1, args[ind]);
                            }
                            set(opts[i].getName(), args[++ind]);
                        }
                    } else {
                        if (s.indexOf('=') != -1) {
                            gotoFatalError(2, args[ind]);
                        }
                        set(opts[i].getName(), "1");
                    }

                    // short options
                } else if (args[ind].charAt(1) != '\0') {
                    int i = 0;
                    boolean hit = false;
                    for (i = 0; i < opts.length && Utils.check(opts[i].getName()); ++i) {
                        if (opts[i].getShortName() == args[ind].charAt(1)) {
                            hit = true;
                            break;
                        }
                    }

                    if (!hit) {
                        gotoFatalError(0, args[ind]);
                    }

                    if (Utils.check(opts[i].getArgDescription())) {
                        if (args[ind].length() >= 3 && args[ind].charAt(2) != '\0') {
                            set(opts[i].getName(), args[ind].substring(2));
                        } else {
                            if (argc == (ind + 1)) {
                                gotoFatalError(1, args[ind]);
                            }
                            set(opts[i].getName(), args[++ind]);
                        }
                    } else {
                        if (args[ind].length() >= 3 && args[ind].charAt(2) != '\0') {
                            gotoFatalError(2, args[ind]);
                        }
                        set(opts[i].getName(), "1");
                    }
                }
            } else {
                rest.add(args[ind]); // others
            }
        }
    }

    private void gotoFatalError(int errno, String arg) {
        switch (errno) {
        case 0:
            throw new IllegalArgumentException("unrecognized option `" + arg + "`");
        case 1:
            throw new IllegalArgumentException("`" + arg + "` requires an argument");
        case 2:
            throw new IllegalArgumentException("`" + arg + "` doesn't allow an argument");
        default:
            throw new IllegalArgumentException("unknown error");
        }
    }

    public boolean helpVersion() {
        if (Utils.check(getString(HELP))) {
            System.out.println(getHelp());
            return true;
        }

        if (Utils.check(getString(VERSION))) {
            System.out.println(getVersion());
            return true;
        }

        return false;
    }

}
