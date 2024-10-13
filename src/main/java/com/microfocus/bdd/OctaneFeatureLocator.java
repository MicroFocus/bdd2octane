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

import com.microfocus.bdd.api.OctaneFeature;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class OctaneFeatureLocator {

    private FeatureFileLocator featureFileLocator;
    private Map<String, OctaneFeature> featureFileToFeatureMap = new HashMap<>();

    public OctaneFeatureLocator(List<String> featureFiles) {
        featureFileLocator = new FeatureFileLocator(featureFiles);
    }

    public Optional<OctaneFeature> getOctaneFeatureByName(String featureName) throws IOException {
        FeatureFileMeta featureFileMeta = featureFileLocator.getFeatureFileMeta(featureName);
        if (null == featureFileMeta) {
            return Optional.empty();
        }
        return Optional.of(createOctaneFeatureIfNotCached(featureFileMeta));
    }

    public OctaneFeature getOctaneFeatureByPath(String featureFile) {
        FeatureFileMeta featureFileMeta = featureFileLocator.createFeatureFileMeta(featureFile);
        return createOctaneFeatureIfNotCached(featureFileMeta);
    }

    public Optional<OctaneFeature> getFeatureByScenarioName(String scenarioName) {
        Optional<FeatureFileMeta> meta = featureFileLocator.getFeatureFileMetaByScenarioNameFromCache(scenarioName);
        if (meta.isPresent()) {
            return Optional.of(createOctaneFeatureIfNotCached(meta.get()));
        }
        Optional<OctaneFeature> featureOpt = featureFileLocator.getOctaneFeatureByScenarioNameFromFile(scenarioName);
        if (featureOpt.isPresent()) {
            OctaneFeature octaneFeature = featureOpt.get();
            String featureFile = octaneFeature.getFeatureFile();
            return Optional.of(featureFileToFeatureMap.computeIfAbsent(featureFile, key -> octaneFeature));
        }
        return Optional.empty();
    }

    public void remove(OctaneFeature octaneFeature) {
        featureFileToFeatureMap.remove(octaneFeature.getFeatureFile());
    }

    private OctaneFeature createOctaneFeatureIfNotCached(FeatureFileMeta featureFileMeta) {
        return featureFileToFeatureMap.computeIfAbsent(featureFileMeta.getFeatureFile(),
                key -> featureFileLocator.getOctaneFeature(featureFileMeta));
    }
}
