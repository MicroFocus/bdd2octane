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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GherkinScenario {

    private String flowContext;
    protected List<GherkinStep> stepsDescriptions = new ArrayList<>();
    protected GherkinDialect dialect;
    private Scenario scenario;
    private Optional<Rule> rule;

    public GherkinScenario(Background background, Rule rule, Background ruleBackground, Scenario scenario, GherkinDialect dialect) {
        this.scenario = scenario;
        this.dialect = dialect;
        this.rule = Optional.ofNullable(rule);
        initialize(background, ruleBackground, scenario);
    }

    public GherkinScenario(Background featureBackground, Scenario scenario, GherkinDialect dialect) {
        this(featureBackground, null, null, scenario, dialect);
    }

    private void initialize(Background background, Background ruleBackground, Scenario scenario) {
        flowContext = "";
        if (background != null) {
            background.getSteps().forEach(step -> {
                updateFlowContext(step);
                stepsDescriptions.add(new GherkinStep(step, flowContext, dialect));
            });
        }
        if (ruleBackground != null) {
            ruleBackground.getSteps().forEach(step -> {
                updateFlowContext(step);
                stepsDescriptions.add(new GherkinStep(step, flowContext, dialect));
            });
        }
        flowContext = "";
        scenario.getSteps().forEach(step -> {
            updateFlowContext(step);
            stepsDescriptions.add(new GherkinStep(step, flowContext, dialect));
        });
    }

    public String getType() {
        return scenario.getKeyword().trim();
    }

    public String getDescription() {
        return scenario.getDescription() != null ? scenario.getDescription() : "";
    }

    private String updateFlowContext(io.cucumber.messages.types.Step step) {
        String keyword = step.getKeyword();

        if (GherkinMultiLingualService.isAndStep(dialect, keyword) || GherkinMultiLingualService.isButStep(dialect, keyword)) {
            if (flowContext.isEmpty()) {
                if (!keyword.equals(GherkinMultiLingualService.STAR)) {
                    throw new RuntimeException("when using BUT, AND flowContext should not be empty");
                } else {
                    flowContext = dialect.getGivenKeywords().stream().filter(kw -> !kw.equals(keyword)).collect(Collectors.toList()).get(0);
                }
            }
            return flowContext;

        }
        flowContext = keyword;

        return flowContext;
    }

    private OctaneScenario createOctaneScenarioAndSteps() {
        OctaneScenario scenario = createOctaneScenario();
        scenario.setSteps(createSteps());
        return scenario;
    }

    private List<OctaneStep> createSteps() {
        return stepsDescriptions.stream().map(step ->
                new OctaneStep(step.getKeyword(), step.getName(), step.getStepTypeDefinition(),
                        step.getDocString(), step.getFlatDataTable())
        ).collect(Collectors.toList());
    }

    public List<OctaneScenario> createOctaneScenarios() {
        return Collections.singletonList(createOctaneScenarioAndSteps());
    }

    protected OctaneScenario createOctaneScenario() {
        OctaneScenario octaneScenario = new OctaneScenario();
        String name = scenario.getName();
        octaneScenario.setName(name);
        octaneScenario.setOutlineName(name);
        octaneScenario.setScenarioType(getScenarioTypeDefinition());
        return octaneScenario;
    }

    protected String getScenarioTypeDefinition() {
        return GherkinMultiLingualService.getTypeDefinition(dialect, getType());
    }

    public String getName() {
        return scenario.getName();
    }

    public Optional<String> getRuleName() {
        if (!rule.isPresent()) {
            return Optional.empty();
        }
        return Optional.of(rule.get().getName());
    }
}
