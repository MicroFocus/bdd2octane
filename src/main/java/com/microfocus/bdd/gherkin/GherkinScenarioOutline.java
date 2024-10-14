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
package com.microfocus.bdd.gherkin;

import com.microfocus.bdd.api.OctaneScenario;
import com.microfocus.bdd.api.OctaneStep;
import io.cucumber.gherkin.GherkinDialect;
import io.cucumber.messages.types.Background;
import io.cucumber.messages.types.Rule;
import io.cucumber.messages.types.Scenario;
import io.cucumber.messages.types.TableCell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

public class GherkinScenarioOutline extends GherkinScenario {

    private List<Map<String, String>> examplesRowsValues = new ArrayList<>();

    public GherkinScenarioOutline(Background background, Rule rule, Background ruleBackground, Scenario scenario, GherkinDialect dialect) {
        super(background, rule , ruleBackground, scenario, dialect);
        initialize(scenario);
    }

    public GherkinScenarioOutline(Background featureBackground, Scenario scenario,GherkinDialect dialect) {
        this(featureBackground,null,null,scenario,dialect);
    }

    private void initialize(Scenario scenarioOutline) {
        scenarioOutline.getExamples().forEach(example -> {
            List<TableCell> headers = example.getTableHeader().get().getCells();
            example.getTableBody().forEach(row -> {
                Map<String, String> values = new HashMap<>();
                List<TableCell> cells = row.getCells();
                for (int i = 0; i < headers.size(); i++) {
                    values.put(headers.get(i).getValue(), cells.get(i).getValue());
                }
                examplesRowsValues.add(values);
            });
        });
    }

    @Override
    public List<OctaneScenario> createOctaneScenarios() {
        int scenarioMinorIndex = 0;
        List<OctaneScenario> octaneScenarios = new ArrayList<>();

        for (Map<String, String> rowParametersValues : examplesRowsValues) {
            scenarioMinorIndex++;
            OctaneScenario octaneScenario = createOctaneScenarioAndSteps(scenarioMinorIndex, rowParametersValues);
            octaneScenarios.add(octaneScenario);
        }
        return octaneScenarios;
    }

    private OctaneScenario createOctaneScenarioAndSteps(int index, Map<String, String> rowParametersValues) {
        OctaneScenario scenario = createOctaneScenario(index, rowParametersValues);
        scenario.setSteps(createSteps(rowParametersValues));
        return scenario;
    }


    private OctaneScenario createOctaneScenario(int index, Map<String, String> rowParametersValues) {
        OctaneScenario scenario = createOctaneScenario();
        scenario.setOutlineIndex(index);
        String name = getName();
        for (Map.Entry<String, String> entry : rowParametersValues.entrySet()) {
            name = replaceParameter(name, entry);
        }
        scenario.setOutlineName(name);
        scenario.setName(getName());
        return scenario;
    }

    private List<OctaneStep> createSteps(Map<String, String> rowParametersValues) {
        return stepsDescriptions.stream().map(step -> createOctaneStep(step, rowParametersValues)).collect(Collectors.toList());
    }

    protected OctaneStep createOctaneStep(GherkinStep step, Map<String, String> nameValueMap) {
        String stepName = step.getName();
        for (Map.Entry<String, String> entry : nameValueMap.entrySet()) {
            stepName = replaceParameter(stepName, entry);
        }
        return new OctaneStep(step.getKeyword(), stepName, step.getStepTypeDefinition(),
                step.getDocString(), step.getFlatDataTable());
    }

    private String replaceParameter(String text, Map.Entry<String, String> parameterEntry) {
        return text.replaceAll(
                "<" + parameterEntry.getKey() + ">", Matcher.quoteReplacement(parameterEntry.getValue()));
    }
}
