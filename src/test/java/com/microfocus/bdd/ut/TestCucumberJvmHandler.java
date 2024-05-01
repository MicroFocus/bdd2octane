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

import com.microfocus.bdd.*;
import com.microfocus.bdd.api.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.xml.stream.XMLStreamException;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;

public class TestCucumberJvmHandler {

    private OctaneFeature octaneFeature;

    @Before
    public void setup() {
        octaneFeature = TestUtil.parseFeatureFile("src/test/resources/features/robustgherkin.feature");
    }

    @Test
    public void testFailedScenario() {
        CucumberJvmHandler handler = new CucumberJvmHandler();
        Element element = TestUtil.getXmlElement("src/test/resources/cucumber-jvm/surefire2.12.4/TEST-hellocucumber.failed.RunCucumberTest.xml", "testcase", 1);
        handler.setElement(element);
        Assert.assertEquals("handler can get back feature name",
                "Some terse yet descriptive text of what is desired like bla bla",
                handler.getFeatureName().get());
        Assert.assertEquals("handler can get back scenario name",
                "Some determinable business situation", handler.getScenarioName(octaneFeature));
        Assert.assertEquals("handler can get back file path",
                "file:///C:/junit2octane/src/test/resources/features/robustgherkin.feature",
                handler.getFeatureFile().get());
        OctaneStep step = new OctaneStep("Given", "the following people exist:", null, null, null);
        handler.fillStep(step);
        Assert.assertTrue("Handle recognize passed status", step.getStatus()==Status.FAILED);
    }

    @Test
    public void testExceptionScenario() {
        CucumberJvmHandler handler = new CucumberJvmHandler();
        Element element = TestUtil.getXmlElement("src/test/resources/cucumber-jvm/surefire2.12.4/TEST-hellocucumber.failed.RunCucumberTest.xml", "testcase", 2);
        handler.setElement(element);
        Assert.assertEquals("handler can get back feature name",
                "Some terse yet descriptive text of what is desired like bla bla",
                handler.getFeatureName().get());
        Assert.assertEquals("handler can get back scenario name",
                "Some another scenario 2", handler.getScenarioName(octaneFeature));
        Assert.assertEquals("handler can get back file path",
                "file:///C:/junit2octane/src/test/resources/features/robustgherkin.feature",
                handler.getFeatureFile().get());
        OctaneStep step = new OctaneStep("Given", "some precondition", null, null, null);
        handler.fillStep(step);
        Assert.assertTrue("Handle recognize failed status", step.getStatus()==Status.FAILED);
    }

    @Test
    public void testRuntimeExceptionScenario() {
        CucumberJvmHandler handler = new CucumberJvmHandler();
        Element element = TestUtil.getXmlElement("src/test/resources/cucumber-jvm/surefire2.12.4/TEST-hellocucumber.failed.RunCucumberTest.xml", "testcase", 4);
        handler.setElement(element);
        Assert.assertEquals("handler can get back feature name",
                "Some terse yet descriptive text of what is desired like bla bla",
                handler.getFeatureName().get());
        Assert.assertEquals("handler can get back scenario name",
                "feeding a cow <name> yum yum yum", handler.getScenarioName(octaneFeature));
        Assert.assertEquals("handler can get back file path",
                "file:///C:/junit2octane/src/test/resources/features/robustgherkin.feature",
                handler.getFeatureFile().get());
        OctaneStep step = new OctaneStep("Given", "the cow weighs 500 kg", null, null, null);
        handler.fillStep(step);
        Assert.assertTrue("Handle recognize failed status", step.getStatus()==Status.FAILED);
    }


    @Test
    public void testPassedScenario(){
        CucumberJvmHandler handler = new CucumberJvmHandler();
        Element element = TestUtil.getXmlElement("src/test/resources/cucumber-jvm/surefire2.12.4/TEST-hellocucumber.passed.RunCucumberTest.xml", "testcase", 1);
        handler.setElement(element);
        Assert.assertEquals("handler can get back feature name",
                "Some terse yet descriptive text of what is desired like bla bla",
                handler.getFeatureName().get());
        Assert.assertEquals("handler can get back scenario name",
                "Some determinable business situation", handler.getScenarioName(octaneFeature));
        Assert.assertFalse("handler can NOT get back file path",
                handler.getFeatureFile().isPresent());
        OctaneStep step = new OctaneStep(null, null, null, null, null);
        handler.fillStep(step);
        Assert.assertTrue("Handle recognize passed status", step.getStatus()==Status.PASSED);
    }

    @Test
    public void testOutlineScenario() {
        Element element = TestUtil.getXmlElement(
                "src/test/resources/cucumber-jvm/surefire2.12.4/TEST-hellocucumber.passed.RunCucumberTest.xml",
                "testcase",
                3);

        CucumberJvmHandler handler = new CucumberJvmHandler();
        handler.setElement(element);

        Assert.assertEquals("handler can get back feature name",
                "Some terse yet descriptive text of what is desired like bla bla",
                handler.getFeatureName().get());
        String scenarioName = handler.getScenarioName(octaneFeature);
        Assert.assertEquals("handler can get back scenario name",
                "feeding a cow <name> yum yum yum", scenarioName);
    }

    @Test
    public void testTwoOutlineScenarioWithSameName() {
        Element element = TestUtil.getXmlElement(
                "src/test/resources/cucumber-jvm/3outlines/test_result_passed.xml",
                "testcase",
                4);

        CucumberJvmHandler handler = new CucumberJvmHandler();
        handler.setElement(element);

        Assert.assertEquals("handler can get back feature name",
                "Some terse yet descriptive text of what is desired like bla bla 3 examples",
                handler.getFeatureName().get());
        String scenarioName = handler.getScenarioName(
                TestUtil.parseFeatureFile("src/test/resources/features/robustgherkin_has_3_examples.feature"));
        Assert.assertEquals("handler can get back scenario name",
                "feeding a cow <name> yum yum yum", scenarioName);
    }

    @Test
    public void testSkippedScenario() throws XMLStreamException {
        CucumberJvmHandler handler = new CucumberJvmHandler();
        Iterator<Element> element = new JunitReportReader(new ByteArrayInputStream(
                ("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                "<testsuite errors=\"0\" failures=\"0\" name=\"io.cucumber.core.plugin.JUnitFormatter\" skipped=\"0\" tests=\"4\" time=\"0.206\">\n" +
                "    <testcase classname=\"Some terse yet descriptive text of what is desired like bla bla\" name=\"Some determinable business situation\" time=\"0.027\">\n" +
                "        <skipped>\n" +
                "        </skipped>\n" +
                "    </testcase>\n" +
                " </testsuite>").getBytes(StandardCharsets.UTF_8)), "testcase").iterator();
        handler.setElement(element.next());
        OctaneStep step = new OctaneStep(null, null, null, null, null);
        handler.fillStep(step);
        Assert.assertTrue("handler can recognize skipped status", step.getStatus()==Status.SKIPPED);
    }

    @Test
    public void testGetFeatureFilePath() {
        Element element = TestUtil.getXmlElement(
                "src/test/resources/cucumber-jvm/calculatortwo.xml",
                "testcase",
                4);

        CucumberJvmHandler handler = new CucumberJvmHandler();
        handler.setElement(element);

        Assert.assertEquals("handler can get back file path",
                "file:src/test/resources/features/addtwo/calculator.feature",
                handler.getFeatureFile().get());
    }

    @Test
    public void testStrictModeWithUndefinedMethod(){
        CucumberJvmHandler handler = new CucumberJvmHandler();
        Element element = TestUtil.getXmlElement("src/test/resources/cucumber-jvm/strict-mode/strict-mode.xml",
                "testcase", 3);
        handler.setElement(element);
        Assert.assertEquals("handler can get back feature name",
                "Validate manual Bike lookup API in Guidewire Customer Engage 10",
                handler.getFeatureName().get());
        Assert.assertFalse("handler can NOT get back file path",
                handler.getFeatureFile().isPresent());
        OctaneStep step = new OctaneStep("And", "nothing happened", null, null, null);
        handler.fillStep(step);
        Assert.assertTrue("Handle recognize failed status", step.getStatus()==Status.FAILED);
    }

    @Test
    public void testSurefire221PrettyMode(){
        CucumberJvmHandler handler = new CucumberJvmHandler();
        Element element = TestUtil.getXmlElement("src/test/resources/cucumber-jvm/surefire2.22.1/TEST-hellocucumber.failed.RunCucumberTest.xml",
                "testcase", 3);
        handler.setElement(element);
        Assert.assertTrue("handler can get back file path",
                handler.getFeatureFile().isPresent());
        OctaneStep step = new OctaneStep("When", "we calculate the feeding requirements", null, null, null);
        handler.fillStep(step);
        Assert.assertTrue("Handle recognize failed status", step.getStatus()==Status.FAILED);
    }

    @Test
    public void testSurefireEmptyFeatureName(){
        CucumberJvmHandler handler = new CucumberJvmHandler();
        Element element = TestUtil.getXmlElement("src/test/resources/cucumber-jvm/empty-feature-name/TEST-empty-feature-name.xml",
                "testcase", 3);
        handler.setElement(element);
        Assert.assertTrue("handler doesn't return feature name",
                !handler.getFeatureName().isPresent());
    }

  @Test
  public void testHyphenatedFeatureNameExactMatch() {
    CucumberJvmHandler handler = new CucumberJvmHandler();
    Element element = TestUtil.getXmlElement("src/test/resources/cucumber-jvm/hyphenated-feature-name/TEST-hyphenated-feature-name.xml",
        "testcase", 1);
    handler.setElement(element);
    OctaneFeatureLocator cache = new OctaneFeatureLocator(Arrays.asList(
        "src/test/resources/features/robustgherkin.feature", // this file is to add some salt to the test case
        "src/test/resources/features/robustgherkin_cn.feature",// this file is to add some salt to the test case
        "src/test/resources/features/feature_name_has_hyphens.feature"));
    Assert.assertEquals("handler can get back feature name",
        "feature - with hyphens - group one",
        handler.getFeatureName(cache).get());

  }
  @Test
  public void testHyphenatedFeatureNamePartMatch()  {
    CucumberJvmHandler handler = new CucumberJvmHandler();
    Element element = TestUtil.getXmlElement("src/test/resources/cucumber-jvm/hyphenated-feature-name/TEST-hyphenated-feature-name.xml",
        "testcase", 2);
    handler.setElement(element);
    OctaneFeatureLocator cache = new OctaneFeatureLocator(Arrays.asList(
        "src/test/resources/features/robustgherkin.feature", // this file is to add some salt to the test case
        "src/test/resources/features/robustgherkin_cn.feature",// this file is to add some salt to the test case
        "src/test/resources/features/feature_name_has_hyphens.feature"));
    Assert.assertEquals("handler can get back feature name",
        "feature - with hyphens - group one",
        handler.getFeatureName(cache).get());

  }

}
