/**
 *
 * Copyright 2021-2023 Open Text
 *
 * The only warranties for products and services of Open Text and
 * its affiliates and licensors ("Open Text") are as may be set forth
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
package com.microfocus.bdd.ut;

import com.microfocus.bdd.Bdd2Octane;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Bdd2OctaneTest {

    @Test
    public void testFailedTestWithUnmatchedStepNameIsNotReportedAsPassed() throws Exception {
        List<String> reportFiles = Arrays.asList(
                "src/test/resources/cucumber-jvm/unmatched-step/TEST-unmatched-step.xml");
        List<String> featureFiles = Arrays.asList(
                "src/test/resources/features/robustgherkin.feature");
        String outputFile = "target/test-unmatched-step-result.xml";

        Bdd2Octane bdd2Octane = new Bdd2Octane(reportFiles, featureFiles, outputFile, "cucumber-jvm");
        bdd2Octane.run();

        File result = new File(outputFile);
        Assert.assertTrue("Output file should exist", result.exists());

        String content = new String(Files.readAllBytes(result.toPath()));
        Assert.assertTrue("Output should contain a failed step",
                content.contains("status=\"failed\""));
        Assert.assertTrue("Steps after failure should be skipped",
                content.contains("status=\"skipped\""));

        // Verify BDD invariant: no PASSED step should appear after a FAILED step
        int failedPos = content.indexOf("status=\"failed\"");
        Assert.assertTrue("Should have a failed step", failedPos >= 0);
        String afterFailure = content.substring(failedPos);
        Assert.assertFalse("No step should be PASSED after a FAILED step (BDD invariant)",
                afterFailure.contains("status=\"passed\""));
    }

    @Test
    public void testCucumberJsUnmatchedFailureProducesPassPassFailSkipPattern() throws Exception {
        // CucumberJs tracks passed steps individually, so when the failed step name
        // doesn't match (e.g. trailing space), passed steps are still correctly identified.
        // This proves the exact pass/pass/fail/skip/skip pattern the user reported.
        List<String> reportFiles = Arrays.asList(
                "src/test/resources/cucumber-js/unmatched-failure/cucumber-js-unmatched-failure.xml");
        List<String> featureFiles = Arrays.asList(
                "src/test/resources/features/robustgherkin.feature");
        String outputFile = "target/test-cucumberjs-unmatched-failure-result.xml";

        Bdd2Octane bdd2Octane = new Bdd2Octane(reportFiles, featureFiles, outputFile, "cucumber-js");
        bdd2Octane.run();

        File result = new File(outputFile);
        Assert.assertTrue("Output file should exist", result.exists());

        String content = new String(Files.readAllBytes(result.toPath()));

        // Extract all step statuses in order
        List<String> statuses = new ArrayList<>();
        Matcher m = Pattern.compile("status=\"(\\w+)\"").matcher(content);
        while (m.find()) {
            statuses.add(m.group(1));
        }

        // 3 background + 6 scenario = 9 steps
        // Steps 1-5 passed (in XML), step 6 failed (name mismatch), steps 7-9 not in XML
        // Expected: passed(x5), failed, skipped(x3)
        Assert.assertEquals("Should have 9 steps, got: " + statuses, 9, statuses.size());

        for (int i = 0; i < 5; i++) {
            Assert.assertEquals("Step " + (i + 1) + " should be passed", "passed", statuses.get(i));
        }
        Assert.assertEquals("Step 6 should be failed (first unmatched step with error)",
                "failed", statuses.get(5));
        for (int i = 6; i < 9; i++) {
            Assert.assertEquals("Step " + (i + 1) + " should be skipped", "skipped", statuses.get(i));
        }
    }
}
