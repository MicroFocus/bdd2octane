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
package com.microfocus.bdd;

import com.microfocus.bdd.api.*;
import io.cucumber.messages.types.FeatureChild;

import java.util.Optional;

public class CucumberRubyHandler implements BddFrameworkHandler {

    private Element element;
    private String errorMessage;
    private String failedStep;
    private Status statusTag;

    @Override
    public String getScenarioName(OctaneFeature feature, OctaneFeatureLocator... octaneFeatureLocator){
        String testcaseNameInReport = element.getAttribute("name");
        String[] nameParts = testcaseNameInReport.split("\\(outline example", 2);
        if (nameParts.length == 2) {
            String testcaseName = nameParts[0].trim();
            Optional<FeatureChild> child = feature.getGherkinDocument().getFeature().get().getChildren().stream()
                    .filter(featureChild -> featureChild.getScenario().isPresent() && featureChild.getScenario().get().getName().equals(testcaseName)).findFirst();
            if (child.isPresent()) {
                if (child.get().getScenario().get().getExamples().isEmpty()) {
                    return testcaseNameInReport;
                } else {
                    return testcaseName;
                }
            }
        } else {
            return testcaseNameInReport;
        }
        return "";
    }

    @Override
    public String getName() {
        return "cucumber-ruby";
    }

    @Override
    public void setElement(Element element) {
        this.element = element;
        if (!element.getChildren().isEmpty()) {
            Element child = element.getChildren().get(0);
            if (child.getName().equals("failure")) {
                /* sample call stack
  <testcase classname="Some terse yet descriptive text of what is desired like bla bla 3 examples" name="feeding a cow &lt;name&gt; yum yum yum (outline example : | cow1 | 450 | 26500 |)" time="0.007322">
    <failure message="failed feeding a cow &lt;name&gt; yum yum yum (outline example : | cow1 | 450 | 26500 |)" type="failed">
      <![CDATA[Scenario Outline: feeding a cow <name> yum yum yum

Example row: | cow1 | 450 | 26500 |

Message:
]]>
      <![CDATA[error (RuntimeError)
./features/step_definitions/gherkin_sample.rb:48:in `nil'
features/gherkin_sample.feature:48:42:in `the cow weighs 450 kg']]>
    </failure>
    <system-out>
      <![CDATA[]]>
    </system-out>
    <system-err>
      <![CDATA[]]>
    </system-err>
  </testcase>

                 */

                errorMessage = child.getText();
                if (errorMessage != null && !errorMessage.isEmpty()) {
                    String lastLine = findLastNonEmptyLine(errorMessage);
                    String[] stepParts = lastLine.split(":in `");
                    if (stepParts.length == 2) {
                        failedStep = stepParts[1].trim().substring(0, stepParts[1].length() - 1);
                        statusTag = Status.FAILED;
                    }
                }
            } else if (child.getName().equals("skipped")) {
                statusTag = Status.SKIPPED;
            }
        }
    }

    private String findLastNonEmptyLine(String message) {
        LinesBottomUpIterator iter = new LinesBottomUpIterator(message);
        while(iter.hasNext()) {
            String line = iter.next();
            if (!line.isEmpty()) {
                return line;
            }
        }
        return null;
    }

    @Override
    public void fillStep(OctaneStep octaneStep) {
        if (statusTag == null) {
            octaneStep.setStatus(Status.PASSED);
        } else if (statusTag.equals(Status.FAILED)) {
            if (octaneStep.getName().equals(failedStep)) {
                octaneStep.setStatus(Status.FAILED);
                octaneStep.setErrorMessage(errorMessage);
            } else {
                octaneStep.setStatus(Status.PASSED);
            }
        } else if (statusTag.equals(Status.SKIPPED)) {
            octaneStep.setStatus(Status.SKIPPED);
        } else {
        }
    }

    @Override
    public String getTestCaseElementName() {
        return "testcase";
    }

    @Override
    public Optional<String> getFeatureName(OctaneFeatureLocator... octaneFeatureLocator) {
        return Optional.ofNullable(element.getAttribute("classname"));
    }

    @Override
    public Optional<String> getFeatureFile() {
        return Optional.empty();
    }
}
