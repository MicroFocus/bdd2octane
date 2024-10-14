package com.microfocus.bdd.gherkin;

import io.cucumber.gherkin.GherkinDialect;
import io.cucumber.messages.types.Background;
import io.cucumber.messages.types.Rule;
import io.cucumber.messages.types.RuleChild;
import io.cucumber.messages.types.Scenario;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GherkinFeatureRule {
    private Rule rule;
    private final List<GherkinScenario> scenarios = new ArrayList<>();

    public GherkinFeatureRule(Background featureBackground, Rule rule, GherkinDialect dialect) {
        this.rule = rule;
        initialize(featureBackground, rule, dialect);
    }

    private void initialize(Background featureBackground, Rule rule, GherkinDialect dialect) {
        List<RuleChild> children = rule.getChildren();
        if (!children.isEmpty()) {
            Background ruleBackground = null;
            for (RuleChild child : children) {
                if (GherkinMultiLingualService.hasBackground(child)) {
                    ruleBackground = child.getBackground().get();
                }

                if (GherkinMultiLingualService.hasScenario(child)) {
                    Scenario scenario = child.getScenario().get();
                    if (GherkinMultiLingualService.isOutlineScenario(scenario)) {
                        scenarios.add(new GherkinScenarioOutline(featureBackground,rule, ruleBackground, scenario, dialect));
                    } else {
                        scenarios.add(new GherkinScenario(featureBackground,rule , ruleBackground, scenario, dialect));
                    }
                }
            }
        }
    }

    public List<GherkinScenario> getScenarios() {
        return scenarios;
    }

    public Rule getRule() {
        return rule;
    }

    public void setRule(Rule rule) {
        this.rule = rule;
    }

    public String getName() {
        return rule.getName();
    }

    public Set<String> getTagNames() {
        return GherkinMultiLingualService.getTagNames(rule.getTags());
    }
}

