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
package com.microfocus.bdd.api;

 import com.microfocus.bdd.OctaneFeatureLocator;

 import java.util.Optional;

public interface BddFrameworkHandler {

    /**
     *
     * @return the name of the BDD framework
     */
    String getName();

    /**
     *
     * @param element, the XML element that represents a scenario, the implementation can take this element,
     *                 store it for later usage or parse it immediately.
     */
    void setElement(Element element);

    /**
     *  This method is supposed to extract feature name from XML element
     * @return the feature name optionally. If it is empty, this XML element is skipped and an error message is printed.
     */
    Optional<String> getFeatureName(OctaneFeatureLocator... octaneFeatureLocator);

    /**
     *  This method is supposed to extract feature file name from XML element. It is treated a shortcut to location the
     *   feature file of the scenario. If it is empty, all the feature files could be scanned in order to locate the
     *   corresponding feature file.
     * @return Optional of feature file (including path)
     */
    Optional<String> getFeatureFile();

    /**
     *  This method is to get the scenario name out of the OctaneFeature structure. If the scenario is an outlined one,
     *  the name must contains parameter name inside a pair of brackets.
     * @param feature, the OctaneFeature
     * @return the scenario name
     */
    String getScenarioName(OctaneFeature feature, OctaneFeatureLocator... octaneFeatureLocator);

    /**
     *  This method is invoked to get back the status of the OctaneStep, the implementation should set correct Status
     *  based on the XML element, and in case of failure, set the errorMessage as well.
     *  If the duration is available in XML element, the implementation also should set it by invoking setDuration.
     *
     * @param octaneStep
     */
    void fillStep(OctaneStep octaneStep);

    /**
     * @return the XML element name of a scenario, most of bdd frameworks use "testcase"
     * cucumber-js uses "testsuite"
     */
    String getTestCaseElementName();

    /**
     * Returns the error message if the test case failed at the test-case level,
     * regardless of whether the specific failed step could be identified.
     * Used as a fallback when fillStep() cannot match the failure to a specific step.
     *
     * @return Optional containing the error message if the test case failed, empty otherwise.
     */
    default Optional<String> getErrorMessage() {
        return Optional.empty();
    }

}
