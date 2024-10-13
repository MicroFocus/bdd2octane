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

import com.microfocus.bdd.api.BddFrameworkHandler;
import com.microfocus.bdd.util.FileUtil;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Properties;
import java.util.Set;


public class App {
    private static Properties properties;

    public static void main(String[] args) throws XMLStreamException, IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        // --reportFiles="**/*junit.xml" --featureFiles="**/*.feature"
        // --reportFiles="junit_xml" --featureFiles="features"
        properties = parseArgs(args);
        final String reportFilePath = validateParameter("--reportFiles", "-rf");
        final String featureFilePath = validateParameter("--featureFiles", "-ff");
        final String framework = validateParameter("--framework", "-f");
        final String resultFilePath = validResultFilePathParameter("--resultFile", "-r");
        List<String> reportFiles = FilesLocator.getReportFiles(reportFilePath);
        FileUtil.printFiles(reportFiles, "xml", reportFilePath);
        List<String> featureFiles = FilesLocator.getFeatureFiles(featureFilePath);
        FileUtil.printFiles(featureFiles, "feature", featureFilePath);
        new Bdd2Octane(reportFiles, featureFiles, resultFilePath, framework).run();
    }

    private static String validateParameter(String... parameters) {
        for (String param : parameters) {
            if (properties.getProperty(param) != null) {
                return properties.getProperty(param);
            }
        }
        System.err.println("Parameter " + parameters[0] + " or " + parameters[1] + " is missing");
        printArgumentMessage();
        System.exit(1);
        return null;
    }

    private static String validResultFilePathParameter(String... parameters) {
        String filePath = null;
        for (String param : parameters) {
            if (properties.getProperty(param) != null) {
                filePath = properties.getProperty(param);
            }
        }
        if (filePath != null && !FileUtil.isPathValid(filePath)) {
            System.err.println("Result file path is not valid");
            printArgumentMessage();
            System.exit(1);
        }
        return filePath;
    }

    private static void printArgumentMessage() {
        System.err.println("Required arguments:\n" +
                "--reportFiles=<pattern>" +
                " or " +
                "-rf=<pattern> for short\n" +
                "--featureFiles=<pattern>" +
                " or " +
                "-ff=<pattern> for short\n" +
                "--framework=<bdd framework>" +
                " or " +
                "-f=<pattern> for short\n" +
                "Optional arguments:\n" +
                "--resultFile=<pattern>" +
                " or " +
                "-r=<pattern> for short\n" +
                "\nDon't add space before or after equal sign (=)\n");
        System.err.println("Supported BDD frameworks: ");
        System.err.print(getAllHandlerNames(getBddHandlerImplementations()));
    }

    private static Properties parseArgs(String[] args) {
        Properties props = new Properties();
        for (String arg : args) {
            String[] split = arg.split("=");
            if (split.length < 2 || split[1].trim().isEmpty()) {
                printArgumentMessage();
                System.exit(1);
            }
            props.setProperty(split[0], split[1].trim());
        }
        return props;
    }


    public static String getAllHandlerNames(Set<Class<? extends BddFrameworkHandler>> implementations) {
        StringBuilder sb = new StringBuilder();
        for (Class<? extends BddFrameworkHandler> impl : implementations) {
            try {
                sb.append("    " + impl.getDeclaredConstructor().newInstance().getName());
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    public static Set<Class<? extends BddFrameworkHandler>> getBddHandlerImplementations() {
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.addUrls(ClasspathHelper.forClass(BddFrameworkHandler.class));
        Reflections reflections = new Reflections(configurationBuilder);
        Set<Class<? extends BddFrameworkHandler>> implementations = reflections.getSubTypesOf(BddFrameworkHandler.class);
        return implementations;
    }
}
