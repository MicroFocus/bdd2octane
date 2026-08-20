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

public class CucumberJsHandler implements BddFrameworkHandler {
    private Element element;
    private String failureStep;
    private String errorMessage;
    private Optional<Long> failureDuration;
    private Map<String, Optional<Long>> passedStepWithDuration;
    private boolean isSkipped = false;

    @Override
    public String getName() {
        return "cucumber-js";
    }

    @Override
    public void setElement(Element element) {
        this.element = element;
        passedStepWithDuration = new HashMap<>();
        if(!element.getChildren().isEmpty()) {
            for(Element stepElement: element.getChildren()) {
                if(stepElement.getName().equals("testcase")) {
                    if(stepElement.getChildren().isEmpty()) {
                        passedStepWithDuration.put(stepElement.getAttribute("name"), convertToLong(stepElement.getAttribute("time")));
                    } else if (stepElement.getChildren().get(0).getName().equals("failure")) {
                        Element failedStep = stepElement.getChildren().get(0);
                        failureStep = stepElement.getAttribute("name");
                        errorMessage = failedStep.getText();
                        failureDuration = convertToLong(failedStep.getAttribute("time"));
                    } else if (stepElement.getChildren().get(0).getName().equals("skipped")) {
                        isSkipped = true;
                    }
                }
            }
        }
    }

    private Optional<Long> convertToLong(String time) {
        if(time != null) {
            Double value = Double.parseDouble(time);
            return Optional.of(Math.round(value * 1000));
        }
        return null;
    }

    @Override
    public Optional<String> getFeatureName(OctaneFeatureLocator... octaneFeatureLocator) {
        return Optional.empty();
    }

    @Override
    public Optional<String> getFeatureFile() {
        return findFeatureFile();
    }

    @Override
    public String getScenarioName(OctaneFeature feature, OctaneFeatureLocator... octaneFeatureLocator) {
        String featureName = feature.getName();
        String featureNameWithDash = featureName.replaceAll(" ", "-");
        featureNameWithDash = featureNameWithDash.substring(0, 1).toLowerCase() + featureNameWithDash.substring(1);
        String elementName = element.getAttribute("name");
        String scenarioNameFromElement = elementName.replace(featureNameWithDash.toLowerCase(), "").substring(1);
        for (OctaneScenario scenario : feature.getScenarios()) {
            String currentScenarioName = scenario.getOutlineName();
            String changedScenarioName = currentScenarioName.replace(" ", "-").replace(" ", "");
            if (changedScenarioName.equalsIgnoreCase(scenarioNameFromElement)) {
                return scenario.getName();
            }

        }
        return null;
    }

    @Override
    public void fillStep(OctaneStep octaneStep) {
        String stepWithKeyWord = octaneStep.getCompleteStepName();
        if(failureStep != null && failureStep.equals(stepWithKeyWord)) {
            octaneStep.setStatus(Status.FAILED);
            octaneStep.setErrorMessage(errorMessage);
            if(failureDuration != null && failureDuration.isPresent()) {
                octaneStep.setDuration(failureDuration.get());
            }
        } else if (passedStepWithDuration.containsKey(stepWithKeyWord)) {
            octaneStep.setStatus(Status.PASSED);
            if (passedStepWithDuration.get(stepWithKeyWord) != null) {
                octaneStep.setDuration(passedStepWithDuration.get(stepWithKeyWord).get());
            }
        } else if (isSkipped) {
            octaneStep.setStatus(Status.SKIPPED);
        }
    }

    public Optional<String> findFeatureFile() {
        Element propertiesElements = element.getChild("properties").get();
        for (Element property: propertiesElements.getChildren()){
                if (property.getAttribute("name").equals("URI")) {
                    return Optional.of(property.getAttribute("value"));
                }

        }
        return Optional.empty();
    }

    @Override
    public Optional<String> getErrorMessage() {
        return Optional.ofNullable(errorMessage);
    }

    public String getTestCaseElementName() {
        return "testsuite";
    }
}
