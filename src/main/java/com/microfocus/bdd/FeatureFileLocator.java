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
import com.microfocus.bdd.api.OctaneScenario;
import com.microfocus.bdd.gherkin.GherkinMultiLingualService;
import com.microfocus.bdd.util.FileUtil;
import com.microfocus.bdd.util.GherkinDocumentUtil;
import io.cucumber.gherkin.GherkinDialectProvider;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FeatureFileLocator {

    private List<String> featureFiles;
    private Map<String, FeatureFileMeta> featureNameToFeatureFileMetaMap = new HashMap<>();
    private Map<String, FeatureFileMeta> scenarioNameToFeatureFileMetaMap = new HashMap<>();


    public FeatureFileLocator(List<String> featureFiles) {
        this.featureFiles = featureFiles;
    }

    public FeatureFileMeta getFeatureFileMeta(String featureName) throws IOException {
        if (featureNameToFeatureFileMetaMap.containsKey(featureName)) {
            return featureNameToFeatureFileMetaMap.get(featureName);
        }
        for (String featureFile : featureFiles) {
            Optional<FeatureFileMeta> featureFileMeta = tryToGetFeatureFileMeta(featureName, featureFile);
            if (featureFileMeta.isPresent()) {
                return featureFileMeta.get();
            }
        }
        return null;
    }

    public Optional<FeatureFileMeta> getFeatureFileMetaByScenarioNameFromCache(String scenarioName) {
        if (scenarioNameToFeatureFileMetaMap.containsKey(scenarioName)) {
            return Optional.of(scenarioNameToFeatureFileMetaMap.get(scenarioName));
        }
        return Optional.empty();
    }

    public Optional<OctaneFeature> getOctaneFeatureByScenarioNameFromFile(String scenarioName) {
        Set<String> parsedFiles = Stream.concat(
                featureNameToFeatureFileMetaMap.values().stream().map(meta -> meta.getFeatureFile()),
                scenarioNameToFeatureFileMetaMap.values().stream().map(meta -> meta.getFeatureFile()))
                .collect(Collectors.toSet());
        for (String featureFile : this.featureFiles) {
            if (parsedFiles.contains(featureFile)) {
                continue;
            }

            FeatureFileMeta featureFileMeta = createFeatureFileMeta(featureFile);
            OctaneFeature octaneFeature = parseFeatureFile(featureFileMeta);

            if (octaneFeature.getName().isEmpty()) {
                cacheScenarios(octaneFeature, featureFileMeta);
            }
            if (scenarioNameToFeatureFileMetaMap.containsKey(scenarioName)) {
                return Optional.of(octaneFeature);
            }
        }
        return Optional.empty();
    }

    public FeatureFileMeta createFeatureFileMeta(String featureFile) {
        String lang = getLanguage(featureFile);
        FeatureFileMeta featureFileMeta = new FeatureFileMeta(featureFile, lang);
        return featureFileMeta;
    }

    public OctaneFeature getOctaneFeature(FeatureFileMeta featureFileMeta) {
        OctaneFeature octaneFeature = parseFeatureFile(featureFileMeta);
        String featureName = octaneFeature.getName();
        if (!featureName.isEmpty()) {
            featureNameToFeatureFileMetaMap.putIfAbsent(octaneFeature.getName(), featureFileMeta);
        }
        return octaneFeature;
    }

    public String getLanguage(String featureFile) {
        try (BufferedReader reader = new BufferedReader(new FileReader(featureFile))) {
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                if (line.contains("# language")) {
                    return parseLanguage(line).orElse(GherkinMultiLingualService.DEFAULT_LANGUAGE);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return GherkinMultiLingualService.DEFAULT_LANGUAGE;
    }

    private Optional<String> parseLanguage(String line) {
        String[] lineParts = line.split(":", 2);
        if (lineParts.length >= 2) {
            return Optional.of(lineParts[1].trim());
        }
        return Optional.empty();
    }

    private Optional<FeatureFileMeta> tryToGetFeatureFileMeta(String featureName, String featureFile) throws IOException {
        FeatureFileMeta featureFileMeta = new FeatureFileMeta(featureFile, GherkinMultiLingualService.DEFAULT_LANGUAGE);
        Pattern pattern = Pattern.compile("^#\\s*language");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(featureFile), "UTF8"))) {
            List<String> translatedFeatureNames;
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                if (pattern.matcher(line).find()) {
                    parseLanguage(line).ifPresent(lang -> featureFileMeta.setLanguage(lang));
                    continue;
                }
                if (line.startsWith("#")) {
                    continue;
                }
                translatedFeatureNames = new GherkinDialectProvider(featureFileMeta.getLanguage())
                        .getDialect(featureFileMeta.getLanguage()).get().getFeatureKeywords();
                if (translatedFeatureNames.stream().anyMatch(line::contains)) {
                    String[] featureNamePattern = line.split(":", 2);
                    if (featureNamePattern.length >= 2) {
                        String fName = featureNamePattern[1].trim();
                        featureNameToFeatureFileMetaMap.putIfAbsent(fName, featureFileMeta);
                        featureFiles.remove(fName);
                        if (fName.equals(featureName)) {
                            return Optional.of(featureFileMeta);
                        }
                    }
                    break;
                }
            }
        }
        return Optional.empty();
    }

    private OctaneFeature parseFeatureFile(FeatureFileMeta featureFileMeta) {
        return GherkinDocumentUtil.generateSkeletonFeature(featureFileMeta);
    }

    private void cacheScenarios(OctaneFeature octaneFeature, FeatureFileMeta meta) {
        for (OctaneScenario octaneScenario : octaneFeature.getScenarios()) {
            if (octaneScenario.getScenarioType().equals(GherkinMultiLingualService.SCENARIO_OUTLINE)) {
                scenarioNameToFeatureFileMetaMap.put(octaneScenario.getOutlineName(), meta);
            } else {
                scenarioNameToFeatureFileMetaMap.put(octaneScenario.getName(), meta);
            }
        }
    }
}
