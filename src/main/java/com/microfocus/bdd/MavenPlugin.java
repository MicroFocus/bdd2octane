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

import com.microfocus.bdd.util.FileUtil;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

@Mojo(name = "run", requiresProject = false)
public class MavenPlugin extends AbstractMojo {
    @Parameter(property = "reportFiles")
    String reportFilesPath;
    @Parameter(property = "featureFiles")
    String featureFilesPath;
    @Parameter(property = "framework")
    String framework;
    @Parameter(property = "resultFile")
    String resultFilePath;

    @Override
    public void execute() throws ParameterMissingException {
        validateParameter(reportFilesPath, "-DreportFiles");
        validateParameter(featureFilesPath, "-DfeatureFiles");
        validateParameter(framework, "-Dframework");
        validResultFilePathParameter(resultFilePath, "-DresultFile");
        Boolean systemErrorsValue = getParameterExistence("systemErrors");
        List<String> reportFiles = FilesLocator.getReportFiles(reportFilesPath);
        FileUtil.printFiles(reportFiles, "xml", reportFilesPath);
        List<String> featureFiles = FilesLocator.getFeatureFiles(featureFilesPath);
        FileUtil.printFiles(featureFiles, "feature", featureFilesPath);
        try {
            new Bdd2Octane(reportFiles, featureFiles, resultFilePath, framework,systemErrorsValue).run();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private static void validateParameter(String paramValue, String paraName) throws ParameterMissingException {
        if (paramValue == null) {
            throw new ParameterMissingException("Parameter " + paraName + " is missing.");
        }
    }

    private static Boolean getParameterExistence(String paramName) {
        String paramProperty = System.getProperty(paramName);
        if(paramProperty != null) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    private static void validResultFilePathParameter(String paramValue, String paramName) throws ParameterMissingException {
        if (paramValue != null && !FileUtil.isPathValid(paramValue)) {
            System.err.println("Parameter " + paramName + " is invalid");
            throw new ParameterMissingException("Parameter " + paramName + " is invalid.");
        }
    }

    public static class ParameterMissingException extends MojoExecutionException {
        private static final String parameterErrorIndicate = "Required parameters:\n" +
                "-DreportFiles=<pattern>\n" +
                "-DfeatureFiles=<pattern>\n" +
                "-Dframework=<bdd framework>\n" +
                "All optional parameters:\n" +
                "-DresultFile=<pattern>\n" +
                "-DsystemErrors";

        public ParameterMissingException(String message) {
            super(message + '\n' + parameterErrorIndicate);
        }
    }

}
