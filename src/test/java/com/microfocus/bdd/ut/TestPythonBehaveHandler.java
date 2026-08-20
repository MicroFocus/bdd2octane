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

import com.microfocus.bdd.api.*;
import com.microfocus.bdd.PythonBehaveHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

public class TestPythonBehaveHandler {
    private OctaneFeature octaneFeature;

    @Before
    public void setup() {
        octaneFeature = TestUtil.parseFeatureFile("src/test/resources/features/robustgherkin.feature");
    }

    @Test
    public void testFailedScenario() {
        PythonBehaveHandler handler = new PythonBehaveHandler();
        Element element = TestUtil.getXmlElement("src/test/resources/python-behave/TESTS-python-behave-all-fail.xml",
                "testcase", 1);
        handler.setElement(element);

        Assert.assertEquals("handler can get back feature name",
                "Some terse yet descriptive text of what is desired like bla bla", handler.getFeatureName().get());
        Assert.assertEquals("handler can get back scenario name", "Some determinable business situation",
                handler.getScenarioName(octaneFeature));
        Assert.assertEquals("handler can get back file path", "features/dealer.feature",
                handler.getFeatureFile().get());
    }

    @Test
    public void testExceptionScenario() {
        PythonBehaveHandler handler = new PythonBehaveHandler();
        Element element = TestUtil.getXmlElement("src/test/resources/python-behave/TESTS-python-behave-exceptions.xml",
                "testcase", 1);
        handler.setElement(element);

        Assert.assertEquals("handler can get back feature name",
                "Some terse yet descriptive text of what is desired like bla bla", handler.getFeatureName().get());
        Assert.assertEquals("handler can get back scenario name", "Some determinable business situation",
                handler.getScenarioName(octaneFeature));
        Assert.assertEquals("handler can get back file path", "features\\robustGherkin.feature",
                handler.getFeatureFile().get());
        OctaneStep errorStep = new OctaneStep("And", "some other action", null, null, null);
        handler.fillStep(errorStep);
        Assert.assertTrue("Handle recognize error status", errorStep.getStatus()==Status.FAILED);
    }

    @Test
    public void testPassSkipFailedScenario() {
        PythonBehaveHandler handler = new PythonBehaveHandler();
        Element element = TestUtil.getXmlElement("src/test/resources/python-behave/TESTS-python-behave-pass-skip-fail.xml",
                "testcase", 1);
        handler.setElement(element);

        Assert.assertEquals("handler can get back feature name",
                "Some terse yet descriptive text of what is desired like bla bla", handler.getFeatureName().get());
        Assert.assertEquals("handler can get back scenario name", "Some determinable business situation",
                handler.getScenarioName(octaneFeature));
        Assert.assertEquals("handler can get back file path", "features/dealer.feature",
                handler.getFeatureFile().get());
        OctaneStep passStep = new OctaneStep("Then", "some testable outcome is achieved", null, null, null);
        handler.fillStep(passStep);
        Assert.assertNull("Non-matching step should have null status (determined by mergeScenario)", passStep.getStatus());
        
        OctaneStep failedStep = new OctaneStep( "Given", "the following people exist:", null, null, null);
        handler.fillStep(failedStep);
        Assert.assertTrue("Handle recognize failed status", failedStep.getStatus()==Status.FAILED);
    }

    @Test
    public void testPassedScenario() {
        PythonBehaveHandler handler = new PythonBehaveHandler();
        Element element = TestUtil.getXmlElement("src/test/resources/python-behave/TESTS-python-behave-all-pass.xml",
                "testcase", 1);
        handler.setElement(element);

        Assert.assertEquals("handler can get back feature name",
                "Some terse yet descriptive text of what is desired like bla bla", handler.getFeatureName().get());
        Assert.assertEquals("handler can get back scenario name", "Some determinable business situation",
                handler.getScenarioName(octaneFeature));
        Assert.assertFalse("handler can NOT get back file path", handler.getFeatureFile().isPresent());
    }

    @Test
    public void testOutlineScenario() {
        PythonBehaveHandler handler = new PythonBehaveHandler();
        Element element = TestUtil.getXmlElement("src/test/resources/python-behave/TESTS-python-behave-all-pass.xml",
                "testcase", 3);
        handler.setElement(element);

        Assert.assertEquals("handler can get back feature name",
                "Some terse yet descriptive text of what is desired like bla bla", handler.getFeatureName().get());
        String scenarioName = handler.getScenarioName(octaneFeature);
        Assert.assertEquals("handler can get back scenario name", "feeding a cow <name> yum yum yum", scenarioName);
    }

    // @Test
    // public void testTwoOutlineScenarioWithSameName() {
    // Element element = TestUtil.getXmlElement(
    // "src/test/resources/cucumber-jvm/3outlines/test_result_passed.xml",
    // "testcase",
    // 4);

    // CucumberJvmHandler handler = new CucumberJvmHandler();
    // handler.setElement(element);

    // Assert.assertEquals("handler can get back feature name",
    // "Some terse yet descriptive text of what is desired like bla bla 3 examples",
    // handler.getFeatureName().get());
    // String scenarioName = handler.getScenarioName(
    // TestUtil.parseFeatureFile("src/test/resources/features/robustgherkin_has_3_examples.feature"));
    // Assert.assertEquals("handler can get back scenario name",
    // "feeding a cow <name> yum yum yum", scenarioName);
    // }
}
