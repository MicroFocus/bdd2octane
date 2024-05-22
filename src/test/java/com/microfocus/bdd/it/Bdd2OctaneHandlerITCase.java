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
package com.microfocus.bdd.it;

import com.microfocus.bdd.Bdd2Octane;
import com.microfocus.bdd.FilesLocator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runners.Parameterized;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Bdd2OctaneHandlerITCase extends Bdd2OctaneITCase {
    private List<String> reportFiles, featureFiles;

    @Parameterized.Parameters(name = "test{index} - framework - {0}")
    public static List<Object[]> data() {
        return Arrays.asList(new Object[][]{
                //single feature file
                {Framework.CUCUMBER_JS, "src/test/resources/cucumber-js/cucumber-junit-test-with-examples-has-same-name.xml", "src/test/resources/features/robustgherkin03.feature", "singleResults/cucumber-js-final.xml", "**/singleResults/standard-single-cucumber-js-final.xml"},
                {Framework.CUCUMBER_JVM, "src/test/resources/cucumber-jvm/junit-passed.xml", "src/test/resources/features/robustgherkin.feature", "singleResults/cucumber-jvm-final.xml", "**/singleResults/standard-single-cucumber-jvm-final.xml"},
                {Framework.CUCUMBER_RUBY, "src/test/resources/cucumber-ruby/passed_skipped_failed.xml", "src/test/resources/features/robustgherkin.feature", "singleResults/cucumber-ruby-final.xml", "**/singleResults/standard-single-cucumber-ruby-final.xml"},
                {Framework.PHP_BEHAT, "src/test/resources/php-behat/fail-assert-1.xml", "src/test/resources/features/robustgherkin.feature", "singleResults/php-behat-final.xml", "**/singleResults/standard-single-php-behat-final.xml"},
                {Framework.PYTHON_BEHAVE, "src/test/resources/python-behave/TESTS-python-behave-pass-skip-fail.xml", "src/test/resources/features/dealer.feature", "singleResults/python-behave-final.xml", "**/singleResults/standard-single-python-behave-final.xml"},
                {Framework.PYTHON_RADISH, "src/test/resources/python-radish/python-radish-skipped-passed-failed.xml", "src/test/resources/features/robustgherkin03.feature", "singleResults/python-radish-final.xml", "**/singleResults/standard-single-python-radish-final.xml"},
                //multiple feature files - different file path
                {Framework.CUCUMBER_JS, "src/test/resources/cucumber-js/cucumber*.xml", "src/test/resources/features/*.feature", "multipleDifferentResults/cucumber-js-final.xml", "**/multipleDifferentResults/standard-multipleDifferent-cucumber-js-final.xml"},
                {Framework.CUCUMBER_JVM, "src/test/resources/cucumber-jvm/**/*passed.xml", "src/test/resources/features/*.feature", "multipleDifferentResults/cucumber-jvm-final.xml", "**/multipleDifferentResults/standard-multipleDifferent-cucumber-jvm-final.xml"},
                {Framework.CUCUMBER_RUBY, "src/test/resources/cucumber-ruby/*passed.xml", "src/test/resources/features/*.feature", "multipleDifferentResults/cucumber-ruby-final.xml", "**/multipleDifferentResults/standard-multipleDifferent-cucumber-ruby-final.xml"},
                {Framework.PHP_BEHAT, "src/test/resources/php-behat/*pass*.xml", "src/test/resources/features/*.feature", "multipleDifferentResults/php-behat-final.xml", "**/multipleDifferentResults/standard-multipleDifferent-php-behat-final.xml"},
                {Framework.PYTHON_BEHAVE, "src/test/resources/python-behave/*skipped*.xml", "src/test/resources/features/*.feature", "multipleDifferentResults/python-behave-final.xml", "**/multipleDifferentResults/standard-multipleDifferent-python-behave-final.xml"},
                {Framework.PYTHON_RADISH, "src/test/resources/python-radish/*all-pass*.xml", "src/test/resources/features/*.feature", "multipleDifferentResults/python-radish-final.xml", "**/multipleDifferentResults/standard-multipleDifferent-python-radish-final.xml"},
                //multiple feature files - same file path
                {Framework.CUCUMBER_JS, "src/test/resources/cucumber-js/*test-report.xml", "**/*.feature", "multipleSameResults/cucumber-js-final.xml", "**/multipleSameResults/standard-multipleSame-cucumber-js-final.xml"},
                {Framework.CUCUMBER_JVM, "**/junit-passed.xml", "**/*.feature", "multipleSameResults/cucumber-jvm-final.xml", "**/multipleSameResults/standard-multipleSame-cucumber-jvm-final.xml"},
                {Framework.CUCUMBER_RUBY, "src/test/resources/cucumber-ruby/TEST*.xml", "src/test/resources/features/*.feature", "multipleSameResults/cucumber-ruby-final.xml", "**/multipleSameResults/standard-multipleSame-cucumber-ruby-final.xml"},
                {Framework.PHP_BEHAT, "src/test/resources/php-behat/*1.xml", "src/test/resources/features/*.feature", "multipleSameResults/php-behat-final.xml", "**/multipleSameResults/standard-multipleSame-php-behat-final.xml"},
                {Framework.PYTHON_BEHAVE, "src/test/resources/python-behave/*skip-*.xml", "src/test/resources/features/dealer.feature", "multipleSameResults/python-behave-final.xml", "**/multipleSameResults/standard-multipleSame-python-behave-final.xml"},
                {Framework.PYTHON_RADISH, "src/test/resources/python-radish/python*.xml", "src/test/resources/features/*.feature", "multipleSameResults/python-radish-final.xml", "**/multipleSameResults/standard-multipleSame-python-radish-final.xml"},
                //separate feature file
                //2 consecutive junit reports
                {Framework.CUCUMBER_JVM, "src/test/resources/cucumber-jvm/separate-test1/*.xml", "src/test/resources/features/robustgherkin.feature", "separateResults/cucumber-jvm-final1.xml", "**/separateResults/standard-cucumber-jvm-final1.xml"},
                //2 consecutive junit reports + 1 repeated junit report
                {Framework.CUCUMBER_JVM, "src/test/resources/cucumber-jvm/separate-test2/*.xml", "src/test/resources/features/robustgherkin.feature", "separateResults/cucumber-jvm-final2.xml", "**/separateResults/standard-cucumber-jvm-final2.xml"},
                //2 discrete junit reports + 1 unrelated junit report
                {Framework.CUCUMBER_JVM, "src/test/resources/cucumber-jvm/separate-test3/*.xml", "src/test/resources/features/*.feature", "separateResults/cucumber-jvm-final3.xml", "**/separateResults/standard-cucumber-jvm-final3.xml"},
                //1 junit report + 2 feature files
                {Framework.CUCUMBER_JVM, "src/test/resources/cucumber-jvm/separate-test4/junit.xml", "src/test/resources/features/separate_test4*.feature", "separateResults/cucumber-jvm-final4.xml", "**/separateResults/standard-cucumber-jvm-final4.xml"},
                //1 junit report + 3 feature files
                {Framework.CUCUMBER_JVM, "src/test/resources/cucumber-jvm/separate-test5/junit.xml", "src/test/resources/features/MyAccount*.feature", "separateResults/cucumber-jvm-final5.xml", "**/separateResults/standard-cucumber-jvm-final5.xml"},

                {Framework.CUCUMBER_JVM, "src/test/resources/cucumber-jvm/separate-test6/junit.xml", "src/test/resources/features/Calculator.feature", "separateResults/cucumber-jvm-final6.xml", "**/separateResults/standard-cucumber-jvm-final6.xml"},

                {Framework.CUCUMBER_JVM, "src/test/resources/cucumber-jvm/space_feature_file/junit.xml", "src/test/resources/features/Check*.feature", "separateResults/cucumber-jvm-final7.xml", "**/separateResults/standard-cucumber-jvm-final7.xml"},
                //testng cases
                {Framework.CUCUMBER_JVM, "src/test/resources/cucumber-jvm/testng/testng-before-test-failed-all-steps-skipped.xml", "src/test/resources/features/applicationLogin.feature", "separateResults/cucumber-jvm-final8.xml", "**/separateResults/standard-cucumber-jvm-final8.xml"},
                {Framework.CUCUMBER_JVM, "src/test/resources/cucumber-jvm/testng/testng-first-step-passed-second-step-undefined.xml", "src/test/resources/features/applicationLogin.feature", "separateResults/cucumber-jvm-final9.xml", "**/separateResults/standard-cucumber-jvm-final9.xml"},
                {Framework.CUCUMBER_JVM, "src/test/resources/cucumber-jvm/testng/testng-first-step-undefined.xml", "src/test/resources/features/applicationLogin.feature", "separateResults/cucumber-jvm-final10.xml", "**/separateResults/standard-cucumber-jvm-final10.xml"},
                //junit5 scenario outline cases
                {Framework.CUCUMBER_JVM, "src/test/resources/cucumber-jvm/3outlines/junit5_scenario_outline1.xml", "src/test/resources/features/robustgherkin_has_3_examples.feature", "separateResults/cucumber-jvm-final11.xml", "**/separateResults/standard-cucumber-jvm-final11.xml"},
                {Framework.CUCUMBER_JVM, "src/test/resources/cucumber-jvm/3outlines/junit5_scenario_outline2.xml", "src/test/resources/features/robustgherkin_has_3_examples.feature", "separateResults/cucumber-jvm-final12.xml", "**/separateResults/standard-cucumber-jvm-final12.xml"},
                {Framework.CUCUMBER_JVM, "src/test/resources/cucumber-jvm/3outlines/junit5_scenario_contain_hyphen_outline3.xml", "src/test/resources/features/robustgherkin_has_3_examples_feature_scenario_name_contain_hyphen.feature", "separateResults/cucumber-jvm-final13.xml", "**/separateResults/standard-cucumber-jvm-final13.xml"},
                {Framework.CUCUMBER_JVM, "src/test/resources/cucumber-jvm/3outlines/junit5_scenario_contain_hyphen_outline4.xml", "src/test/resources/features/robustgherkin_has_3_examples_feature_scenario_name_contain_hyphen.feature", "separateResults/cucumber-jvm-final14.xml", "**/separateResults/standard-cucumber-jvm-final14.xml"},
                {Framework.CUCUMBER_JVM, "src/test/resources/cucumber-jvm/3outlines/junit5_scenario_outline_name_similar.xml", "src/test/resources/features/OutlineConfusion.feature", "separateResults/cucumber-jvm-final15.xml", "**/separateResults/standard-cucumber-jvm-final15.xml"},
                //scenario outlines failed beforeClass case
                {Framework.CUCUMBER_JVM, "src/test/resources/cucumber-jvm/3outlines/failed_scenario_outlines_without_any_failed_step_info.xml", "src/test/resources/features/check_payment_method_status.feature", "separateResults/cucumber-jvm-final16.xml", "**/separateResults/standard-cucumber-jvm-final16.xml"},
                //1 junit report + 3 feature files (Feature names contain hyphens and are partially same.)
                {Framework.CUCUMBER_JVM, "src/test/resources/cucumber-jvm/separate-test17/junit.xml", "src/test/resources/features/ContainHyphens*.feature", "separateResults/cucumber-jvm-final17.xml", "**/separateResults/standard-cucumber-jvm-final17.xml"},
        });

    }

    @Before
    public void setup() {
        reportFiles = FilesLocator.getReportFiles(reportFilesPath);
        featureFiles = FilesLocator.getFeatureFiles(featureFilesPath);
        reportFiles.forEach(System.out::println);
        featureFiles.forEach(System.out::println);
    }

    @Test
    public void bdd2OctaneITCase() throws CompareXmlException, ParserConfigurationException, SAXException, IOException, IllegalAccessException, XMLStreamException, InstantiationException {
        String resultFilePath = "target/generated-test-sources-" + resultFilesPath;
        System.out.println("Comparing result file " + resultFilePath + " with expected file " + standardResultPath);
        new Bdd2Octane(reportFiles, featureFiles, resultFilePath, framework.getValue()).run();
        validate(resultFilePath, standardResultPath);
    }
}

