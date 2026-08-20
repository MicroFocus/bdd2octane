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

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PythonRadishHandler implements BddFrameworkHandler {
    private static final Pattern scenarioOutLineSuffixPattern = Pattern.compile("\\s-\\srow\\s\\d+"); // pattern "- row 0" or "- row 1"

    private Element element;
    private List<String> errorSteps; //report may contains only 1 error step
    private Boolean isSkippedElement;
    private String errorMessage;

    @Override
    public String getName() {
        return "python-radish";
    }

    @Override
    public void setElement(Element element) {
        this.element = element;
        // add logic for failed message check
        errorSteps = new ArrayList<>();
        isSkippedElement = false;
        if(!element.getChildren().isEmpty()) {
            for(Element stepElement: element.getChildren()) {
                if(stepElement.getName().equals("failure")) {
                    errorSteps.add(stepElement.getAttribute("message"));
                    errorMessage = stepElement.getText();
                }else if(stepElement.getName().equals("skipped")){
                    isSkippedElement = true;
                }
            }
        }
    }

    @Override
    public Optional<String> getFeatureName(OctaneFeatureLocator... octaneFeatureLocator) {
        return Optional.of(element.getAttribute("classname"));
    }

    @Override
    public Optional<String> getFeatureFile() {
        return Optional.empty();
    }

    @Override
    public String getScenarioName(OctaneFeature feature, OctaneFeatureLocator... octaneFeatureLocator) {
        //two cases: with outline/without outline
        String elementScenarioName = element.getAttribute("name");
        Matcher scenarioOutLineSuffixMatcher = scenarioOutLineSuffixPattern.matcher(elementScenarioName);
        if (scenarioOutLineSuffixMatcher.find()) {
            //if it contains outline
            //feature.gherkinDocument.feature.children
            String elementScenarioRow = scenarioOutLineSuffixMatcher.group();
            //remove - row 0
            String elementScenarioNameWithoutRow = elementScenarioName.replace(elementScenarioRow, "");
            return elementScenarioNameWithoutRow;
        }
        return elementScenarioName;
    }

    @Override
    public void fillStep(OctaneStep octaneStep) {
        //set each step passed/failed/skipped
        if(isSkippedElement) {
            octaneStep.setStatus(Status.SKIPPED);
            return;
        }

        String stepName = octaneStep.getCompleteStepName();
        if (errorSteps.contains(stepName)) {
            octaneStep.setStatus(Status.FAILED);
            octaneStep.setErrorMessage(errorMessage);
            return;
        }

        if (errorMessage == null) {
            octaneStep.setStatus(Status.PASSED);
        }
        // else: leave null — test failed but can't identify which step
    }

    @Override
    public Optional<String> getErrorMessage() {
        return Optional.ofNullable(errorMessage);
    }

    @Override
    public String getTestCaseElementName() {
        return "testcase";
    }
}
