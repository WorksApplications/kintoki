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

import java.util.ArrayList;
import java.util.List;

public class ChunkInfo {

  private List<String> strStaticFeature = new ArrayList<>();
  private List<String> strGapFeature = new ArrayList<>();
  private List<String> strLeftContextFeature = new ArrayList<>();
  private List<String> strRightContextFeature = new ArrayList<>();
  private List<String> strChildFeature = new ArrayList<>();
  private List<Integer> staticFeature = new ArrayList<>();
  private List<Integer> dst1StaticFeature = new ArrayList<>();
  private List<Integer> dst2StaticFeature = new ArrayList<>();
  private List<Integer> leftContextFeature = new ArrayList<>();
  private List<Integer> right1ContextFeature = new ArrayList<>();
  private List<Integer> right2ContextFeature = new ArrayList<>();
  private List<Integer> srcChildFeature = new ArrayList<>();
  private List<Integer> dst1ChildFeature = new ArrayList<>();
  private List<Integer> dst2ChildFeature = new ArrayList<>();

  public List<String> getStrStaticFeature() {
    return strStaticFeature;
  }

  public void setStrStaticFeature(List<String> strStaticFeature) {
    this.strStaticFeature = strStaticFeature;
  }

  public List<String> getStrGapFeature() {
    return strGapFeature;
  }

  public void setStrGapFeature(List<String> strGapFeature) {
    this.strGapFeature = strGapFeature;
  }

  public List<String> getStrLeftContextFeature() {
    return strLeftContextFeature;
  }

  public void setStrLeftContextFeature(List<String> strLeftContextFeature) {
    this.strLeftContextFeature = strLeftContextFeature;
  }

  public List<String> getStrRightContextFeature() {
    return strRightContextFeature;
  }

  public void setStrRightContextFeature(List<String> strRightContextFeature) {
    this.strRightContextFeature = strRightContextFeature;
  }

  public List<String> getStrChildFeature() {
    return strChildFeature;
  }

  public void setStrChildFeature(List<String> strChildFeature) {
    this.strChildFeature = strChildFeature;
  }

  public List<Integer> getStaticFeature() {
    return staticFeature;
  }

  public void setStaticFeature(List<Integer> staticFeature) {
    this.staticFeature = staticFeature;
  }

  public List<Integer> getDst1StaticFeature() {
    return dst1StaticFeature;
  }

  public void setDst1StaticFeature(List<Integer> dst1StaticFeature) {
    this.dst1StaticFeature = dst1StaticFeature;
  }

  public List<Integer> getDst2StaticFeature() {
    return dst2StaticFeature;
  }

  public void setDst2StaticFeature(List<Integer> dst2StaticFeature) {
    this.dst2StaticFeature = dst2StaticFeature;
  }

  public List<Integer> getLeftContextFeature() {
    return leftContextFeature;
  }

  public void setLeftContextFeature(List<Integer> leftContextFeature) {
    this.leftContextFeature = leftContextFeature;
  }

  public List<Integer> getRight1ContextFeature() {
    return right1ContextFeature;
  }

  public void setRight1ContextFeature(List<Integer> right1ContextFeature) {
    this.right1ContextFeature = right1ContextFeature;
  }

  public List<Integer> getRight2ContextFeature() {
    return right2ContextFeature;
  }

  public void setRight2ContextFeature(List<Integer> right2ContextFeature) {
    this.right2ContextFeature = right2ContextFeature;
  }

  public List<Integer> getSrcChildFeature() {
    return srcChildFeature;
  }

  public void setSrcChildFeature(List<Integer> srcChildFeature) {
    this.srcChildFeature = srcChildFeature;
  }

  public List<Integer> getDst1ChildFeature() {
    return dst1ChildFeature;
  }

  public void setDst1ChildFeature(List<Integer> dst1ChildFeature) {
    this.dst1ChildFeature = dst1ChildFeature;
  }

  public List<Integer> getDst2ChildFeature() {
    return dst2ChildFeature;
  }

  public void setDst2ChildFeature(List<Integer> dst2ChildFeature) {
    this.dst2ChildFeature = dst2ChildFeature;
  }
}
