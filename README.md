# Kintoki

Kintoki is a dependency parser library.

This library includes followings:

- A port of the CaboCha to Java
- A command line tool
- Models for Chunking and Parsing

## Requirement
- Java version >= 1.8.0


# Setting
This library uses Sudachi as morphological analyzer, and provide models for Chunking and Parsing separately. 
The directories for dictionary of Sudachi, Chunking and Parsing models need to be configured before using the library.
There are two ways to set these configurations: 

- Configure in `cabocharc.properties`. 

The following directories should be changed to real development environment. 

```
# Parser model file name
parser-model  = dep.bccwj.model

# Chunker model file name
chunker-model = chunk.bccwj.model

# Sudachi
sudachi-dict = ./
```

About the configuration file, the `cabocharc.properties` file under the resources folder is used by default. 
You can also specify the directory of configuration file when creating a new instance of `Cabocha` or `Parser` class.

```
String path = "cabocharc.properties";
Cabocha cabocha = new Cabocha(path);
Tree tree = cabocha.parse(sent);
...
```

# Usage of APIs
The `Cabocha` class provide APIs for CLI use and `Parser` class provide APIs for library use. 
You can reference the interfaces as you need from external. 

## APIs provided by `Cabocha` class

```
/**
 * Create a new instance of Cabocha class.
 */
public Cabocha() throws IOException {...}

/**
 * Create a new instance of Cabocha class.
 *
 * @param config the path of configuration file
 */
public Cabocha(String config) throws IOException {...}

/**
 * Create a new instance of Cabocha class.
 *
 * @param param configuration parameters
 */
public Cabocha(Param param) throws IOException {...}

/**
 * Parse a given sentence.
 *
 * @param sent the sentence to be parsed
 * @return a tree object will be returned if the parsing is success, otherwise return null
 * @throws IOException IOexception will be thrown when error occurs in reading files (such as model file, resource file)
 */
public Tree parse(String sent) throws IOException {...}

/**
 * Parse a given sentence tree.
 *
 * @param sent the sentence tree to be parsed
 * @return a tree object will be returned if the parsing is success, otherwise return null
 * @throws IOException IOexception will be thrown when error occurs in reading files (such as model file, resource file)
 */
public Tree parse(Tree sent) throws IOException {...}

/**
 * Parse a given sentence.
 *
 * @param sent the sentence to be parsed
 * @return a string will be returned if the parsing is success, otherwise return null
 * @throws IOException IOexception will be thrown when error occurs in reading files (such as model file, resource file)
 */
public String parseToString(String sent) throws IOException {...}

/**
 * Parse from command line.
 *
 * @param args command line arguments
 * @throws IOException IOexception will be thrown when error occurs in reading files (such as model file, resource file)
 */
public static void parse(String[] args) throws IOException {...}

```

## APIs provided by `Parser` class

```
/**
 * Create a new instance of Parser class.
 *
 * @throws IOException IOexception will be thrown when error occurs in reading files (such as model file, resource file)
 */
public Parser() throws IOException {...}

/**
 * Create a new instance of Parser class.
 *
 * @param config the path of configuration file
 * @throws IOException IOexception will be thrown when error occurs in reading files (such as model file, resource file)
 */
public Parser(String config) throws IOException {...}

/**
 * Create a new instance of Parser class.
 *
 * @param param parameters required in parsing
 * @throws IOException IOexception will be thrown when error occurs in reading files (such as model file, resource file)
 */
public Parser(Param param) throws IOException {...}

/**
 * Initialization for parser, which should be called before parsing.
 *
 * @throws IOException IOexception will be thrown when error occurs in reading files (such as model file, resource file)
 */
public void open() throws IOException {...}

/**
 * Parse a given sentence tree.
 *
 * @param sent the sentence tree to be parsed
 * @return a tree object will be returned if the parsing is success, otherwise return null
 * @throws IOException IOexception will be thrown when error occurs in reading files (such as model file, resource file)
 */
public Tree parse(Tree tree) throws IOException {...}

/**
 * Parse a given sentence.
 *
 * @param text the sentence to be parsed
 * @return a tree object will be returned if the parsing is success, otherwise return null
 */
public Tree parse(String text) {...}

/**
 * Parse a given sentence.
 *
 * @param sent the sentence to be parsed
 * @return a string will be returned if the parsing is success, otherwise return null
 * @throws IOException IOexception will be thrown when error occurs in reading files (such as model file, resource file)
 */
public String parseToString(String sent) throws IOException {...}

```
## Example

- An exmaple of calling `public Cabocha()` API of `Cabocha` class: 

```
public class example {

    public static void main(String[] args) {
        String sent = "太郎は花子が読んでいる本を次郎に渡した。";
        String result = "";

        Cabocha cabocha = new Cabocha();
        Tree tree = cabocha.parse(sent);

        if (tree.getChunks() != null) {
            for (int i=0; i<tree.getChunkSize(); i++) {
                Chunk chunk = tree.chunk(i);
                result += "* "+i+" "+chunk.getLink()+"D ";
                result += chunk.getHeadPos()+"/"+chunk.getFuncPos()+"\n";
                for (int j=0; j<chunk.getTokenSize();j++) {
                    Token token = chunk.token(j);
                    result += token.getSurface()+"\t"+token.getFeature()+"\n";
                }
            }
            result += "EOS\n";
        }
        System.out.println(result);
    }

}
```

- An exmaple of calling `public Parser(Param param)` API of `Parser` class: 

```
public class example {

    public static void main(String[] args) throws IOException {
        String sent = "太郎は花子が読んでいる本を次郎に渡した。";
        String result = "";
        String configPath = "cabocharc.properties";
        String sudachi = "./";
        String chunkerModel = "chunk.bccwj.model";
        String parserModel = "dep.bccwj.model";

        Param param = new Param();
        param.loadConfig(configPath);
        param.set(Param.SUDACHI_DICT, sudachi);
        param.set(Param.CHUNKER_MODEL, chunkerModel);
        param.set(Param.PARSER_MODEL, parserModel);
        Parser parser = new Parser(param);
        parser.open();
        Tree tree = parser.parse(sent);

        if (tree.getChunks() != null) {
            for (int i=0; i<tree.getChunkSize(); i++) {
                Chunk chunk = tree.chunk(i);
                result += "* "+i+" "+chunk.getLink()+"D ";
                result += chunk.getHeadPos()+"/"+chunk.getFuncPos()+" ";
                result += chunk.getScore()+"\n";
                for (int j=0; j<chunk.getTokenSize();j++) {
                    Token token = chunk.token(j);
                    result += token.getSurface()+"\t"+token.getFeature()+"\n";
                }
            }
            result += "EOS\n";
        }
        System.out.println(result);
    }

}
```


# Command Line Tool
Beside the APIs, we also provide a command line tool for terminal use. 

- Packaging

Download the kintoki library to your local, and package it by maven. A jar file named `kintoki-0.1.1-SNAPSHOT-jar-with-dependencies.jar` will be created if the packaging is success.

```
$ mvn package
```

- Command Line Options

```
 -f, --output-format=TYPE  set output format style
                            0 - tree(default)
                            1 - lattice
                            2 - tree + lattice
                            3 - XML
                            4 - CoNLL
 -I, --input-layer=LAYER   set input layer
                            0 - raw sentence layer(default)
                            1 - POS tagged layer
                            2 - POS tagger and Chunked layer
                            3 - POS tagged, Chunked and Feature selected layer
 -O, --output-layer=LAYER  set output layer
                            1 - POS tagged layer
                            2 - POS tagged and Chunked layer
                            3 - POS tagged, Chunked and Feature selected layer
                            4 - Parsed layer(default)
 -m, --parser-model=FILE   use FILE as parser model file
 -M, --chunker-model=FILE  use FILE as chunker model file
 -r, --rcfile=FILE         use FILE as resource file
 -d, --sudachi-dict=DIR    use DIR as sudachi dictionary directory
 -o, --output=FILE         use FILE as output file
 -v, --version             show the version and exit
 -h, --help                show this help and exit
```

## Usage
As mentioned in `Setting` section, the configuration file should be set before using this tool. 
For command line tool, you can also use following options to set the parsing configuration:

* use `-r` option to specify the path of configuration file, ex. `-r cabocharc.properties`.
* use `-d` option to specify the path of dictionary for Sudachi, ex. `-d ./`.
* use `-m` option to specify the path of parser model, ex. `-m dep.bccwj.model`. 
* use `-M` option to specify the path of chunker model, ex. `-M chunk.bccwj.model`. 

### Parse from directly input

```
$ java -jar kintoki-0.1.1-SNAPSHOT-jar-with-dependencies.jar -f1 -r cabocharc.properties
太郎は花子が読んでいる本を次郎に渡した。
* 0 5D 0/1 -1.3855161666870117
太郎    名詞,固有名詞,人名,名,*,*
は      助詞,係助詞,*,*,*,*
* 1 2D 0/1 0.788101315498352
花子    名詞,固有名詞,人名,名,*,*
が      助詞,格助詞,*,*,*,*
* 2 3D 2/2 1.4824764728546143
読ん    動詞,一般,*,*,五段-マ行,連用形-撥音便
で      助詞,接続助詞,*,*,*,*
いる    動詞,非自立可能,*,*,上一段-ア行,連体形-一般
* 3 5D 0/1 -1.3855161666870117
本      名詞,普通名詞,一般,*,*,*
を      助詞,格助詞,*,*,*,*
* 4 5D 0/1 -1.3855161666870117
次郎    名詞,固有名詞,人名,名,*,*
に      助詞,格助詞,*,*,*,*
* 5 -1D 0/1 0.0
渡し    動詞,一般,*,*,五段-サ行,連用形-一般
た      助動詞,*,*,*,助動詞-タ,終止形-一般
。      補助記号,句点,*,*,*,*
EOS
```

### Parse from input file (raw sentence layer to Parsed layer)

- Command:


```
java -jar kintoki-0.1.1-SNAPSHOT-jar-with-dependencies.jar -I0 -O4 -f1 -r cabocharc.properties input_file 
```
- Input:

```
太郎は花子が読んでいる本を次郎に渡した。
詰め将棋の本を買ってきました。
```

- Output:

```
* 0 5D 0/1 -1.3855161666870117
太郎    名詞,固有名詞,人名,名,*,*
は      助詞,係助詞,*,*,*,*
* 1 2D 0/1 0.788101315498352
花子    名詞,固有名詞,人名,名,*,*
が      助詞,格助詞,*,*,*,*
* 2 3D 2/2 1.4824764728546143
読ん    動詞,一般,*,*,五段-マ行,連用形-撥音便
で      助詞,接続助詞,*,*,*,*
いる    動詞,非自立可能,*,*,上一段-ア行,連体形-一般
* 3 5D 0/1 -1.3855161666870117
本      名詞,普通名詞,一般,*,*,*
を      助詞,格助詞,*,*,*,*
* 4 5D 0/1 -1.3855161666870117
次郎    名詞,固有名詞,人名,名,*,*
に      助詞,格助詞,*,*,*,*
* 5 -1D 0/1 0.0
渡し    動詞,一般,*,*,五段-サ行,連用形-一般
た      助動詞,*,*,*,助動詞-タ,終止形-一般
。      補助記号,句点,*,*,*,*
EOS
* 0 1D 1/2 1.6246509552001953
詰め    名詞,普通名詞,一般,*,*,*
将棋    名詞,普通名詞,一般,*,*,*
の      助詞,格助詞,*,*,*,*
* 1 2D 0/1 1.6246509552001953
本      名詞,普通名詞,一般,*,*,*
を      助詞,格助詞,*,*,*,*
* 2 -1D 2/4 0.0
買っ    動詞,一般,*,*,五段-ワア行,連用形-促音便
て      助詞,接続助詞,*,*,*,*
き      動詞,非自立可能,*,*,カ行変格,連用形-一般
まし    助動詞,*,*,*,助動詞-マス,連用形-一般
た      助動詞,*,*,*,助動詞-タ,終止形-一般
。      補助記号,句点,*,*,*,*
EOS
```

### Parse from input file (POS tagged layer to Parsed layer)

- Command:

```
java -jar kintoki-0.1.1-SNAPSHOT-jar-with-dependencies.jar -I1 -O4 -f1 -r cabocharc.properties input_file
```

- Input:

```
太郎    名詞,固有名詞,人名,名,*,*
は      助詞,係助詞,*,*,*,*
花子    名詞,固有名詞,人名,名,*,*
が      助詞,格助詞,*,*,*,*
読ん    動詞,一般,*,*,五段-マ行,連用形-撥音便
で      助詞,接続助詞,*,*,*,*
いる    動詞,非自立可能,*,*,上一段-ア行,連体形-一般
本      名詞,普通名詞,一般,*,*,*
を      助詞,格助詞,*,*,*,*
次郎    名詞,固有名詞,人名,名,*,*
に      助詞,格助詞,*,*,*,*
渡し    動詞,一般,*,*,五段-サ行,連用形-一般
た      助動詞,*,*,*,助動詞-タ,終止形-一般
。      補助記号,句点,*,*,*,*
EOS
詰め    名詞,普通名詞,一般,*,*,*
将棋    名詞,普通名詞,一般,*,*,*
の      助詞,格助詞,*,*,*,*
本      名詞,普通名詞,一般,*,*,*
を      助詞,格助詞,*,*,*,*
買っ    動詞,一般,*,*,五段-ワア行,連用形-促音便
て      助詞,接続助詞,*,*,*,*
き      動詞,非自立可能,*,*,カ行変格,連用形-一般
まし    助動詞,*,*,*,助動詞-マス,連用形-一般
た      助動詞,*,*,*,助動詞-タ,終止形-一般
。      補助記号,句点,*,*,*,*
EOS

```

- Output:

```
* 0 5D 0/1 -1.3855161666870117
太郎    名詞,固有名詞,人名,名,*,*
は      助詞,係助詞,*,*,*,*
* 1 2D 0/1 0.788101315498352
花子    名詞,固有名詞,人名,名,*,*
が      助詞,格助詞,*,*,*,*
* 2 3D 2/2 1.4824764728546143
読ん    動詞,一般,*,*,五段-マ行,連用形-撥音便
で      助詞,接続助詞,*,*,*,*
いる    動詞,非自立可能,*,*,上一段-ア行,連体形-一般
* 3 5D 0/1 -1.3855161666870117
本      名詞,普通名詞,一般,*,*,*
を      助詞,格助詞,*,*,*,*
* 4 5D 0/1 -1.3855161666870117
次郎    名詞,固有名詞,人名,名,*,*
に      助詞,格助詞,*,*,*,*
* 5 -1D 0/1 0.0
渡し    動詞,一般,*,*,五段-サ行,連用形-一般
た      助動詞,*,*,*,助動詞-タ,終止形-一般
。      補助記号,句点,*,*,*,*
EOS
* 0 1D 1/2 1.6246509552001953
詰め    名詞,普通名詞,一般,*,*,*
将棋    名詞,普通名詞,一般,*,*,*
の      助詞,格助詞,*,*,*,*
* 1 2D 0/1 1.6246509552001953
本      名詞,普通名詞,一般,*,*,*
を      助詞,格助詞,*,*,*,*
* 2 -1D 2/4 0.0
買っ    動詞,一般,*,*,五段-ワア行,連用形-促音便
て      助詞,接続助詞,*,*,*,*
き      動詞,非自立可能,*,*,カ行変格,連用形-一般
まし    助動詞,*,*,*,助動詞-マス,連用形-一般
た      助動詞,*,*,*,助動詞-タ,終止形-一般
。      補助記号,句点,*,*,*,*
EOS
```

# Models
We provide two models, `chunk.bccwj.model` and `dep.bccwj.model` for Chunking and Parsing, which are trained using CaboCha(0.69). 
The dataset used for training is BCCWJ corpus, you can also use other dataset to train models for Kintoki. 

- Training model

For the detail of training model, please refer to https://taku910.github.io/cabocha/ for details. 
Kintoki use `UNIDIC` as defalult pos set and `UTF8` as default character set, 
so it is necessary to keep the same settings when training a new model for kintoki. 


# Licenses
```
Copyright 2019 Works Applications Co., Ltd.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```