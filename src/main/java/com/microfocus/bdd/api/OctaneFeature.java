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

import com.google.common.collect.LinkedListMultimap;
import io.cucumber.messages.types.GherkinDocument;
import io.cucumber.messages.types.Tag;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class OctaneFeature {
    public static final String BSPID_PREFIX = "@BSPID";

    String name;
    Map<String, ScenarioTracker> scenarios;
    GherkinDocument gherkinDocument;
    String featureFile;
    String started = "";

    public String getStarted() {
        return started;
    }

    public void setStarted(String started) {
        this.started = started;
    }

    public String getFeatureFile() {
        return featureFile;
    }

    public void setFeatureFile(String featureFile) {
        this.featureFile = featureFile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<OctaneScenario> getScenarios() {
        Comparator<OctaneScenario> comparator = Comparator.comparing(sce -> -sce.getOutlineName().length());
        return scenarios.values().stream()
                .map(s -> s.scenarios)
                .flatMap(List::stream)
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    public Optional<OctaneScenario> findScenarioAndUpdateOutlineIndex(String scenarioName) {
        List<OctaneScenario> scenarios = getScenarios(scenarioName);
        if (scenarios.size() == 1) {
            return Optional.of(scenarios.get(0));
        } else {
            final int index = nextOutlineIndex(scenarioName);
            return scenarios.stream().filter(sce -> sce.getOutlineIndex() == index).findFirst();
        }
    }

    private int nextOutlineIndex(String scenarioName) {
        return ++scenarios.get(scenarioName).outlineIndex;
    }

    public void setScenarios(LinkedListMultimap<String, OctaneScenario> scenarios) {
        this.scenarios = new LinkedHashMap<>();
        for (String key : scenarios.keys()) {
            ScenarioTracker scenarioTracker = new ScenarioTracker();
            scenarioTracker.outlineIndex = 0;
            scenarioTracker.scenarios = scenarios.get(key);
            this.scenarios.put(key, scenarioTracker);
        }
    }

    public List<OctaneScenario> getScenarios(String scenarioName) {
        if (scenarios.containsKey(scenarioName)) {
            return scenarios.get(scenarioName).scenarios;
        }
        return Collections.emptyList();
    }

    public boolean isAllScenariosMerged() {
        return scenarios.values().stream().map(s -> s.scenarios).flatMap(List::stream).allMatch(s -> s.isMerged());
    }

    public void writeToXml(XMLStreamWriter writer) throws XMLStreamException, IOException {
        writeFeatureHeader(writer);
        List<OctaneScenario> sces = scenarios.values().stream().map(s -> s.scenarios).flatMap(List::stream).collect(Collectors.toList());
        for (OctaneScenario scenario : sces) {
            if (scenario.isMerged()) {
                scenario.writeToXml(writer);
                writer.writeCharacters("\n");
            }
        }
        writeFeatureEnd(writer);
    }

    private void writeFeatureEnd(XMLStreamWriter writer) throws XMLStreamException {
        writer.writeEndElement();//scenarios
        writer.writeCharacters("\n");
        writer.writeEndElement();//feature
        writer.writeCharacters("\n");
    }

    private void writeFeatureHeader(XMLStreamWriter writer) throws XMLStreamException, IOException {
        writer.writeStartElement("feature");
        writer.writeAttribute("name", name);
        writer.writeAttribute("path", featureFile);
        writer.writeAttribute("started", started);
        writer.writeAttribute("tag", getTag());
        writer.writeCharacters("\n");
        writer.writeStartElement("file");
        writer.writeCharacters("\n");
        writer.writeCData(getCData());
        writer.writeCharacters("\n");
        writer.writeEndElement();//file
        writer.writeCharacters("\n");
        writer.writeStartElement("scenarios");
        writer.writeCharacters("\n");
    }

    private String getTag() {
        if(gherkinDocument.getFeature().isPresent()) {
            List<Tag> tags = gherkinDocument.getFeature().get().getTags();
            for (Tag tag : tags) {
                String name = tag.getName();
                if (name.startsWith(BSPID_PREFIX)) {
                    return name.substring(1);
                }
            }
        }
        return "";
    }

    private String getCData() throws IOException {
        //todo, we might need to handle the file encoding here. such UTF8 or Unicode
        return String.join("\n", Files.readAllLines(Paths.get(featureFile)));
    }

    public GherkinDocument getGherkinDocument() {
        return gherkinDocument;
    }

    public void setGherkinDocument(GherkinDocument gherkinDocument) {
        this.gherkinDocument = gherkinDocument;
    }
}

class ScenarioTracker {
    public int outlineIndex = 0;
    public List<OctaneScenario> scenarios;
}
