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
package com.microfocus.bdd.it;

import com.microfocus.bdd.Bdd2Octane;
import com.microfocus.bdd.FilesLocator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runners.Parameterized;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

 public class Bdd2OctaneSysErrHandlerITCase extends Bdd2OctaneHandlerITCase {
     private List<String> reportFiles, featureFiles;

     @Parameterized.Parameter(5)
     public Boolean systemErrors;

     @Parameterized.Parameters(name = "test{index} - framework - {0}")
     public static List<Object[]> data() {
         return Arrays.asList(new Object[][]{
                 //single feature file
                 {Framework.CUCUMBER_JVM, "src/test/resources/cucumber-jvm/separate-test8/junit.xml", "src/test/resources/features/ContainHyphens*.feature", "separateResults/cucumber-jvm-final18.xml", "**/separateResults/standard-cucumber-jvm-final18.xml",Boolean.TRUE},
         });

     }

     @Before
     public void setup() {
         reportFiles = FilesLocator.getReportFiles(reportFilesPath);
         featureFiles = FilesLocator.getFeatureFiles(featureFilesPath);
         reportFiles.forEach(System.out::println);
         featureFiles.forEach(System.out::println);
     }

     @Test
     public void bdd2OctaneITCase() throws CompareXmlException, ParserConfigurationException, SAXException, IOException, IllegalAccessException, XMLStreamException, InstantiationException, InvocationTargetException, NoSuchMethodException {
         String resultFilePath = "target/generated-test-sources-" + resultFilesPath;
         System.out.println("Comparing result file " + resultFilePath + " with expected file " + standardResultPath);

         new Bdd2Octane(reportFiles, featureFiles, resultFilePath, framework.getValue(),systemErrors).run();

         validate(resultFilePath, standardResultPath);
     }
 }

