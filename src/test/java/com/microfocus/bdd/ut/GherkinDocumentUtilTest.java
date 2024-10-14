 /**
 *
 * Copyright 2021-2023 Open Text
 *
 * The only warranties for products and services of Open Text and
 * its affiliates and licensors (“Open Text”) are as may be set forth
 * in the express warranty statements accompanying such products and services.
 * Nothing herein should be construed as constituting an additional warranty.
 * Open Text shall not be liable for technical or editorial errors or
 * omissions contained herein. The information contained herein is subject
 * to change without notice.
 *
 * Except as specifically indicated otherwise, this document contains
 * confidential information and a valid license is required for possession,
 * use or copying. If this work is provided to the U.S. Government,
 * consistent with FAR 12.211 and 12.212, Commercial Computer Software,
 * Computer Software Documentation, and Technical Data for Commercial Items are
 * licensed to the U.S. Government under vendor's standard commercial license.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.microfocus.bdd.ut;

import com.microfocus.bdd.FeatureFileMeta;
import com.microfocus.bdd.api.OctaneFeature;
import com.microfocus.bdd.gherkin.GherkinMultiLingualService;
import com.microfocus.bdd.util.GherkinDocumentUtil;
import org.junit.Assert;
import org.junit.Test;

public class GherkinDocumentUtilTest {

    @Test
    public void parseFeatureFile() {
        String featureFile = "src/test/resources/features/robustgherkin_has_3_examples.feature";
        FeatureFileMeta featureFileMeta = new FeatureFileMeta(featureFile, GherkinMultiLingualService.DEFAULT_LANGUAGE);
        OctaneFeature octaneFeature = GherkinDocumentUtil.generateSkeletonFeature(featureFileMeta);

        // test octane scenario
        Assert.assertEquals("outline scenario #1 name is feeding a cow <name> yum yum yum", "feeding a cow <name> yum yum yum",
                octaneFeature.getScenarios("feeding a cow <name> yum yum yum").get(0).getName());
        Assert.assertEquals("outline scenario #1 outlineName is feeding a cow cow1 yum yum yum", "feeding a cow cow1 yum yum yum",
                octaneFeature.getScenarios("feeding a cow <name> yum yum yum").get(0).getOutlineName());
        Assert.assertEquals("outline scenario #1 outlineIndex is 1", 1,
                octaneFeature.getScenarios("feeding a cow <name> yum yum yum").get(0).getOutlineIndex());
        Assert.assertEquals("outline scenario #1 scenario type is Scenario Outline", GherkinMultiLingualService.SCENARIO_OUTLINE,
                octaneFeature.getScenarios("feeding a cow <name> yum yum yum").get(0).getScenarioType());

        // test octane step
        Assert.assertEquals("outline scenario #1 step #4 is filled with correct parameter value 450", "first rule administrator named \"RG\"",
                octaneFeature.getScenarios("feeding a cow <name> yum yum yum").get(0).getSteps().get(3).getName());
        Assert.assertEquals("outline scenario #1 step #4 keyword is Given", "Given",
                octaneFeature.getScenarios("feeding a cow <name> yum yum yum").get(0).getSteps().get(3).getKeyword());
        Assert.assertEquals("outline scenario #1 step #4 is filled with correct parameter value 450", "the cow weighs 450 kg",
                octaneFeature.getScenarios("feeding a cow <name> yum yum yum").get(0).getSteps().get(6).getName());
        Assert.assertEquals("outline scenario #1 step #4 keyword is Given", "Given",
                octaneFeature.getScenarios("feeding a cow <name> yum yum yum").get(0).getSteps().get(6).getKeyword());
        Assert.assertEquals("outline scenario #1 step #3 is * a customer named \"Wilson\"", "* a customer named \"Wilson\"",
                octaneFeature.getScenarios("feeding a cow <name> yum yum yum").get(0).getSteps().get(2).getKeyword() + " " +
                        octaneFeature.getScenarios("feeding a cow <name> yum yum yum").get(0).getSteps().get(2).getName());
        Assert.assertEquals("outline scenario #1 step #3 keyword is *", "*",
                octaneFeature.getScenarios("feeding a cow <name> yum yum yum").get(0).getSteps().get(2).getKeyword());
        Assert.assertEquals("outline scenario #1 step #3 step type is Given", "Given",
                octaneFeature.getScenarios("feeding a cow <name> yum yum yum").get(0).getSteps().get(2).getStepType());

        // test misc
        Assert.assertEquals("feature contains 5 scenarios", 5, octaneFeature.getScenarios().size());
        Assert.assertTrue("feature contains document", octaneFeature.getGherkinDocument() != null);
        Assert.assertEquals("feature contains filePath", featureFile, octaneFeature.getFeatureFile());

        // test * at first step
        Assert.assertEquals("scenario #1 step number is 10", 10,
                octaneFeature.getScenarios("Some determinable business situation").get(0).getSteps().size());
        Assert.assertEquals("scenario #1 step #1 name is star at first step", "star at first step",
                octaneFeature.getScenarios("Some determinable business situation").get(0).getSteps().get(3).getName());
        Assert.assertEquals("scenario #1 step #1 keyword is Given", "Given",
                octaneFeature.getScenarios("feeding a cow <name> yum yum yum").get(0).getSteps().get(3).getKeyword());
    }

    @Test
    public void parseCNFeatureFile() {
        String featureFile = "src/test/resources/features/robustgherkin_cn.feature";
        FeatureFileMeta featureFileMeta = new FeatureFileMeta(featureFile, GherkinMultiLingualService.CN_LANGUAGE);
        OctaneFeature octaneFeature = GherkinDocumentUtil.generateSkeletonFeature(featureFileMeta);
        Assert.assertEquals("feature Name is 一个功能", "一个功能", octaneFeature.getName());

        // test octane scenario in cn
        Assert.assertEquals("scenario #1 name is 一个场景", "一个场景",
                octaneFeature.getScenarios("一个场景").get(0).getName());
        Assert.assertEquals("scenario #1 scenario type is Scenario", GherkinMultiLingualService.SCENARIO,
                octaneFeature.getScenarios("一个场景").get(0).getScenarioType());

        // test octane step in cn
        Assert.assertEquals("step #1 is 一个假如", "一个假如",
                octaneFeature.getScenarios("一个场景").get(0).getSteps().get(0).getName());
        Assert.assertEquals("step #1 keyword is 假如", "假如",
                octaneFeature.getScenarios("一个场景").get(0).getSteps().get(0).getKeyword());
        Assert.assertEquals("step #1 stepType is Given", "Given",
                octaneFeature.getScenarios("一个场景").get(0).getSteps().get(0).getStepType());

        // test misc
        Assert.assertEquals("feature contains 1 scenarios", 1, octaneFeature.getScenarios().size());
        Assert.assertTrue("feature contains document", octaneFeature.getGherkinDocument() != null);
        Assert.assertEquals("feature contains filePath", featureFile, octaneFeature.getFeatureFile());
    }
}
