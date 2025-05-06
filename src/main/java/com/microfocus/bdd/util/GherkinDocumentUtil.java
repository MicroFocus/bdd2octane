/**
 * Copyright 2021-2023 Open Text
 * <p>
 * The only warranties for products and services of Open Text and
 * its affiliates and licensors (“Open Text”) are as may be set forth
 * in the express warranty statements accompanying such products and services.
 * Nothing herein should be construed as constituting an additional warranty.
 * Open Text shall not be liable for technical or editorial errors or
 * omissions contained herein. The information contained herein is subject
 * to change without notice.
 * <p>
 * Except as specifically indicated otherwise, this document contains
 * confidential information and a valid license is required for possession,
 * use or copying. If this work is provided to the U.S. Government,
 * consistent with FAR 12.211 and 12.212, Commercial Computer Software,
 * Computer Software Documentation, and Technical Data for Commercial Items are
 * licensed to the U.S. Government under vendor's standard commercial license.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.microfocus.bdd.util;

import com.google.common.collect.LinkedListMultimap;
import com.microfocus.bdd.FeatureFileMeta;
import com.microfocus.bdd.api.*;
import com.microfocus.bdd.gherkin.GherkinFeature;
import com.microfocus.bdd.gherkin.GherkinFeatureRule;
import com.microfocus.bdd.gherkin.GherkinScenario;
import io.cucumber.messages.types.Envelope;
import io.cucumber.messages.types.GherkinDocument;
import io.cucumber.messages.types.Source;
import io.cucumber.gherkin.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static io.cucumber.messages.types.SourceMediaType.TEXT_X_CUCUMBER_GHERKIN_PLAIN;

public class GherkinDocumentUtil {
    public static OctaneFeature generateSkeletonFeature(FeatureFileMeta featureFileMeta) {
        List<OctaneScenario> octaneScenarios = new ArrayList<>();
        LinkedListMultimap<String, OctaneScenario> scenarioName2Scenario = LinkedListMultimap.create();
        GherkinDocument gherkinDocument = getDocument(featureFileMeta);
        Optional<GherkinFeature> featureOpt = getFeature(gherkinDocument);
        GherkinFeature gherkinTestFeature = featureOpt.get();

        OctaneFeature octaneFeature = new OctaneFeature();
        octaneFeature.setName(gherkinTestFeature.getName());
        for (GherkinScenario gherkinTestScenario : gherkinTestFeature.getScenarios()) {
            octaneScenarios.addAll(gherkinTestFeature.createOctaneScenarios(gherkinTestScenario));
        }
        for (GherkinFeatureRule gherkinFeatureRule : gherkinTestFeature.getRules()) {
            gherkinFeatureRule.getScenarios().forEach(scn->{
                octaneScenarios.addAll(scn.createOctaneScenarios());
            });
        }
        for (OctaneScenario octaneScenario : octaneScenarios) {
            scenarioName2Scenario.put(octaneScenario.getName(), octaneScenario);
        }
        octaneFeature.setScenarios(scenarioName2Scenario);
        octaneFeature.setGherkinDocument(gherkinDocument);
        octaneFeature.setFeatureFile(featureFileMeta.getFeatureFile());
        return octaneFeature;
    }

    private static GherkinDocument getDocument(FeatureFileMeta featureFileMeta) {
        try {
            Optional<GherkinDocument> gherkinDocument = parse(String.join("\n", Files.readAllLines(Paths.get(featureFileMeta.getFeatureFile()))), Optional.empty());
            if(!gherkinDocument.isPresent()){
                throw new RuntimeException("The Gherkin script contains errors. Fix them and then try again.");
            }
            return gherkinDocument.get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Optional<GherkinDocument> parse(String script, Optional<String> fileName) {
        Envelope envelope = Envelope.of(new Source(fileName.orElse(""), script, TEXT_X_CUCUMBER_GHERKIN_PLAIN));
        Optional<Envelope> gherkinDocumentEnv = GherkinParser.builder()
                .includeSource(false)
                .includePickles(false)
                .build()
                .parse(envelope)
                .findFirst();
        if (gherkinDocumentEnv.isPresent() && gherkinDocumentEnv.get().getParseError().isPresent()) {
            throw new RuntimeException(String.format("The Gherkin script contains errors. Fix them and then try again. %s.", gherkinDocumentEnv.get().getParseError().get().getMessage()));
        }

        return gherkinDocumentEnv.isPresent() ? gherkinDocumentEnv.get().getGherkinDocument() : Optional.empty();

    }

    private static Optional<GherkinFeature> getFeature(GherkinDocument gherkinDocument) {
        if(!gherkinDocument.getFeature().isPresent()){
            return Optional.empty();
        }
        GherkinFeature feature = new GherkinFeature(gherkinDocument.getFeature().get());
        return Optional.of(feature);
    }
}
