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
package com.microfocus.bdd.ut;

import com.microfocus.bdd.api.Element;
import com.microfocus.bdd.JunitReportReader;
import org.junit.Assert;
import org.junit.Test;

import javax.xml.stream.XMLStreamException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Iterator;

public class TestJunitReportReader {

    @Test
    public void testPassedXml() throws XMLStreamException, FileNotFoundException {
        JunitReportReader reader = new JunitReportReader(new FileInputStream("src/test/resources/cucumber-jvm/junit-passed.xml"),
                "testcase");
        Iterator<Element> iter = reader.iterator();
        iter.hasNext();
        Element element = iter.next();
        Assert.assertTrue("element has a name", element.getName().equals("testcase"));
        Assert.assertEquals("element has one child named system-out", "system-out", element.getChildren().get(0).getName());
        Assert.assertEquals("element has one child contains text", ("Given a global administrator named \"Greg\"...................................passed\n" +
                "* a blog named \"Greg's anti-tax rants\"......................................passed\n" +
                "* a customer named \"Wilson\".................................................passed\n" +
                "Given the following people exist:...........................................passed\n" +
                "And some precondition 1.....................................................passed\n" +
                "When some action by the actor...............................................passed\n" +
                "And some other action.......................................................passed\n" +
                "Then some testable outcome is achieved......................................passed\n" +
                "And something else we can check happens too.................................passed\n").trim(), element.getChildren().get(0).getText().trim());
    }

    @Test
    public void testFailedXml() throws XMLStreamException, FileNotFoundException {
        JunitReportReader reader = new JunitReportReader(
                new FileInputStream("src/test/resources/cucumber-jvm/junit-failed.xml"),
                "testcase");
        Iterator<Element> iter = reader.iterator();
        iter.hasNext();
        Element element = iter.next();
        Assert.assertTrue("element has a name", element.getName().equals("testcase"));
        Assert.assertEquals("element has one child named failure", "failure", element.getChildren().get(0).getName());
        Assert.assertEquals(("Given a global administrator named \"Greg\"...................................passed\n" +
                "* a blog named \"Greg's anti-tax rants\"......................................passed\n" +
                "* a customer named \"Wilson\".................................................passed\n" +
                "Given the following people exist:...........................................failed\n" +
                "And some precondition 1.....................................................skipped\n" +
                "When some action by the actor...............................................skipped\n" +
                "And some other action.......................................................skipped\n" +
                "Then some testable outcome is achieved......................................skipped\n" +
                "And something else we can check happens too.................................skipped\n" +
                "\n" +
                "StackTrace:\n" +
                "java.lang.AssertionError\n" +
                "\tat org.junit.Assert.fail(Assert.java:87)\n" +
                "\tat org.junit.Assert.assertTrue(Assert.java:42)\n" +
                "\tat org.junit.Assert.assertTrue(Assert.java:53)\n" +
                "\tat hellocucumber.failed.StepDefinitions.the_following_people_exist(StepDefinitions.java:12)\n" +
                "\tat ✽.the following people exist:(file:///C:/junit2octane/src/test/resources/features/robustgherkin.feature:14)\n").trim(), element.getChildren().get(0).getText().trim());
    }

    @Test
    public void testEntityEncodedAngleBracketsPreserveSpaces() throws XMLStreamException, FileNotFoundException {
        JunitReportReader reader = new JunitReportReader(
                new FileInputStream("src/test/resources/cucumber-jvm/entity-encoding-test.xml"),
                "testcase");
        Iterator<Element> iter = reader.iterator();
        Assert.assertTrue(iter.hasNext());
        Element element = iter.next();
        Element failure = element.getChildren().get(0);
        Assert.assertEquals("failure", failure.getName());
        String text = failure.getText();
        // Spaces between entity-decoded angle brackets must be preserved
        Assert.assertTrue("spaces between angle brackets must be preserved",
                text.contains("<Token A> <Token B> <Token C>"));
    }
}
