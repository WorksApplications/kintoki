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

import com.worksap.nlp.kintoki.cabocha.model.FastSVMModel;
import com.worksap.nlp.kintoki.cabocha.model.SVMModelFactory;

import java.io.IOException;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

public class DependencyParser extends Analyzer {

    private FastSVMModel svmModel = null;
    private DependencyParserData data = null;

    @Override
    public void open(Param param) throws IOException {
        if (getActionMode() == Constant.PARSING_MODE) {
            String modelFile = param.getString(Param.PARSER_MODEL);
            svmModel = SVMModelFactory.loadModel(modelFile);
        }
    }

    @Override
    public void parse(Tree tree) {
        tree.setOutputLayer(OutputLayerType.OUTPUT_DEP);

        if (tree.getChunkSize() == 0) {
            return;
        }

        if (tree.getChunkSize() == 1) {
            tree.chunk(0).setLink(-1);
            tree.chunk(0).setScore(0);
            return;
        }

        // make features
        build(tree);

        parseShiftReduce(tree);
    }

    private void build(Tree tree) {
        data = new DependencyParserData();

        // collect all features from each chunk.
        for (int i = 0; i < tree.getChunkSize(); ++i) {
            Chunk chunk = tree.chunk(i);
            ChunkInfo chunkInfo = new ChunkInfo();
            for (int k = 0; k < chunk.getFeatureListSize(); ++k) {
                String feature = chunk.getFeatureList().get(k);
                switch (feature.charAt(0)) {
                    case 'F':
                        chunkInfo.getStrStaticFeature().add(feature);
                        break;
                    case 'L':
                        chunkInfo.getStrLeftContextFeature().add(feature);
                        break;
                    case 'R':
                        chunkInfo.getStrRightContextFeature().add(feature);
                        break;
                    case 'G':
                        chunkInfo.getStrGapFeature().add(feature);
                        break;
                    case 'A':
                        chunkInfo.getStrChildFeature().add(feature);
                        break;
                    default:
                        System.out.println("Unknown feature "+feature);
                }
            }
            data.getChunkInfo().add(chunkInfo);
        }
    }

    private boolean parseShiftReduce(Tree tree) {
        int size = tree.getChunkSize();

        Hypothesis hypo = data.getHypothesis();
        hypo.init(size);

        Stack<Integer> agenda = new Stack<>();
        Ref<Double> score = new Ref<>(0.0);
        agenda.push(0);

        for (int dst = 1; dst < size; ++dst) {
            Ref<Integer> src = new Ref<>(0);
            MYPOP(agenda, src);

            // |is_fake_link| is used for partial training, where
            // not all dependency relations are specified in the training phase.
            // Here we assume that a chunk modifes the next chunk,
            // if the dependency relation is unknown. We don't use the fake
            // dependency for training.
            boolean isFakeLink = (getActionMode() == Constant.TRAINING_MODE &&
                    dst != size - 1 &&
                    tree.chunk(src.get()).getLink() == -1);

            // if agenda is empty, src == -1.
            while (src.get() != -1 && (dst == size - 1 || isFakeLink || estimate(tree, src.get(), dst, score))) {
                hypo.getHead().set(src.get(), dst);
                hypo.getScore().set(src.get(), score.get());
                // store children for dynamic_features
                if (!isFakeLink) {
                    hypo.getChildren().get(dst).add(src.get());
                }

                MYPOP(agenda, src);
            }
            if (src.get() != -1) {
                agenda.push(src.get());
            }
            agenda.push(dst);
        }

        for (int src = 0; src < size; ++src) {
            Chunk chunk = tree.chunk(src);
            chunk.setLink(hypo.getHead().get(src));
            chunk.setScore(hypo.getScore().get(src));
        }

        return true;
    }

    private boolean estimate(Tree tree, int src, int dst, Ref<Double> score) {
        Hypothesis hypo = data.getHypothesis();

        List<Integer> fp = data.getFp();

        // distance features
        int dist = dst - src;
        if (dist == 1) {
            addFeature("DIST:1");
        } else if (dist >= 2 && dist <= 5) {
            addFeature("DIST:2-5");
        } else {
            addFeature("DIST:6-");
        }

        {
            ChunkInfo chunkInfo = data.chunkInfo(src);
            if (chunkInfo.getStaticFeature().isEmpty()) {
                for (int i = 0; i < chunkInfo.getStrStaticFeature().size(); ++i) {
                    String feature = chunkInfo.getStrStaticFeature().get(i).substring(1);
                    chunkInfo.getStrStaticFeature().set(i, "S"+feature);
                    addFeature2(chunkInfo.getStrStaticFeature().get(i), chunkInfo.getStaticFeature());
                }
            }
            copyFeature(chunkInfo.getStaticFeature());
        }

        {
            ChunkInfo chunkInfo = data.chunkInfo(dst);
            if (chunkInfo.getDst1StaticFeature().isEmpty()) {
                for (int i = 0; i < chunkInfo.getStrStaticFeature().size(); ++i) {
                    String feature = chunkInfo.getStrStaticFeature().get(i).substring(1);
                    chunkInfo.getStrStaticFeature().set(i, "D"+feature);
                    addFeature2(chunkInfo.getStrStaticFeature().get(i), chunkInfo.getDst1StaticFeature());
                }
            }
            copyFeature(chunkInfo.getDst1StaticFeature());
        }

        if (src > 0) {
            ChunkInfo chunkInfo = data.chunkInfo(src - 1);
            if (chunkInfo.getLeftContextFeature().isEmpty()) {
                for (int i = 0; i < chunkInfo.getStrLeftContextFeature().size(); ++i) {
                    addFeature2(chunkInfo.getStrLeftContextFeature().get(i),
                            chunkInfo.getLeftContextFeature());
                }
            }
            copyFeature(chunkInfo.getLeftContextFeature());
        }

        if (dst < tree.getChunkSize() - 1) {
            ChunkInfo chunkInfo = data.chunkInfo(dst + 1);
            if (chunkInfo.getRight1ContextFeature().isEmpty()) {
                for (int i = 0; i < chunkInfo.getStrRightContextFeature().size(); ++i) {
                    addFeature2(chunkInfo.getStrRightContextFeature().get(i),
                            chunkInfo.getRight1ContextFeature());
                }
            }
            copyFeature(chunkInfo.getRight1ContextFeature());
        }

        for (int i = 0; i < hypo.getChildren().get(src).size(); ++i) {
            int child = hypo.getChildren().get(src).get(i);
            ChunkInfo chunkInfo = data.chunkInfo(child);
            if (chunkInfo.getSrcChildFeature().isEmpty()) {
                for (int j = 0; j < chunkInfo.getStrChildFeature().size(); ++j) {
                    String feature = chunkInfo.getStrChildFeature().get(j).substring(1);
                    chunkInfo.getStrChildFeature().set(j, "a"+feature);
                    addFeature2(chunkInfo.getStrChildFeature().get(j), chunkInfo.getSrcChildFeature());
                }
            }
            copyFeature(chunkInfo.getSrcChildFeature());
        }

        for (int i = 0; i < hypo.getChildren().get(dst).size(); ++i) {
            int child = hypo.getChildren().get(dst).get(i);
            ChunkInfo chunkInfo = data.chunkInfo(child);
            if (chunkInfo.getDst1ChildFeature().isEmpty()) {
                for (int j = 0; j < chunkInfo.getStrChildFeature().size(); ++j) {
                    String feature = chunkInfo.getStrChildFeature().get(j).substring(1);
                    chunkInfo.getStrChildFeature().set(j, "A"+feature);
                    addFeature2(chunkInfo.getStrChildFeature().get(j), chunkInfo.getDst1ChildFeature());
                }
            }
            copyFeature(chunkInfo.getDst1ChildFeature());
        }

        // gap features
        int bracketStatus = 0;
        for (int k = src + 1; k <= dst - 1; ++k) {
            ChunkInfo chunkInfo = data.chunkInfo(k);
            for (int i = 0; i < chunkInfo.getStrGapFeature().size(); ++i) {
                String gapFeature = chunkInfo.getStrGapFeature().get(i);
                if (gapFeature.equals("GOB:1")) {
                    bracketStatus |= 1;
                } else if (gapFeature.equals("GCB:1")) {
                    bracketStatus |= 2;
                } else {
                    addFeature(gapFeature);
                }
            }
        }

        // bracket status
        switch (bracketStatus) {
            case 0: addFeature("GNB:1"); break;  // nothing
            case 1: addFeature("GOB:1"); break;  // open only
            case 2: addFeature("GCB:1"); break;  // close only
            default: addFeature("GBB:1"); break;  // both
        }

        fp = fp.stream().sorted().distinct().collect(Collectors.toList());

        if (getActionMode() == Constant.PARSING_MODE) {
            score.set(svmModel.classify(fp));
            data.getFp().clear();
            return score.get() > 0;
        }

        return false;
    }

    private void MYPOP(Stack<Integer> agenda, Ref<Integer> n) {
        if (agenda.empty()) {
            n.set(-1);
        } else {
            n.set(agenda.peek());
            agenda.pop();
        }
    }

    private void addFeature(String key) {
        int id = this.svmModel.id(key);
        if (id != -1) { this.data.getFp().add(id); }
    }

    private void addFeature2(String key, List<Integer> array) {
        int id = this.svmModel.id(key);
        if (id != -1) { array.add(id); }
    }

    private void copyFeature(List<Integer> feature) {
        this.data.getFp().addAll(feature);
    }
}
