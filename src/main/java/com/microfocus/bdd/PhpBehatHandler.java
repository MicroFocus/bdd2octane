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

import java.util.Optional;

public class PhpBehatHandler implements BddFrameworkHandler {

    private Element element;
    private String status;
    private String errorMessage;

    @Override
    public String getName() {
        return "php-behat";
    }

    @Override
    public void setElement(Element element) {
        this.element = element;
        this.status = element.getAttribute("status");
        if (status.equalsIgnoreCase(Status.FAILED.toString())) {
            this.errorMessage = element.getChild("failure").get().getAttribute("message");
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
        String elementName = element.getAttribute("name");
        if (!feature.getScenarios(elementName).isEmpty()) {
            return elementName;
        }
        for (OctaneScenario scenario : feature.getScenarios()) {
            if (elementName.startsWith(scenario.getName())) {
                return scenario.getName();
            }
        }
        return null;
    }


    @Override
    public void fillStep(OctaneStep octaneStep) {
        if (status.equalsIgnoreCase(Status.PASSED.toString())) {
            octaneStep.setStatus(Status.PASSED);
        } else if (status.equalsIgnoreCase(Status.FAILED.toString())) {
            if (errorMessage.contains(octaneStep.getName())) {
                octaneStep.setStatus(Status.FAILED);
                octaneStep.setErrorMessage(errorMessage);
            }
            // else: leave null — step didn't match the error message
        } else {
            octaneStep.setStatus(Status.SKIPPED);
        }
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
