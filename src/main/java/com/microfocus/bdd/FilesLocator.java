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

import org.apache.tools.ant.DirectoryScanner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FilesLocator {

    public static List<String> getFeatureFiles(String featureFile) {
        return getAllFilesUnderFolder(featureFile, ".feature");
    }

    public static List<String> getReportFiles(String reportFile) {
        return getAllFilesUnderFolder(reportFile, ".xml");
    }

    private static List<String> getAllFilesUnderFolder(String filePattern, String fileExtension) {
        File file = new File(filePattern);
        if (file.isAbsolute()) {
            throw new RuntimeException("Please enter a file path relative to current working directory.");
        }
        List<String> result = new ArrayList<>();
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null) {
                throw new RuntimeException("Cannot read directory: " + filePattern);
            }
            for (File subFile : files) {
                final String fileName = subFile.getName();
                if (fileName.endsWith(fileExtension)) {
                    result.add(subFile.getPath());
                }
            }
            if (result.isEmpty()) {
                throw new RuntimeException("There exists no " + fileExtension + " files under folder: " + filePattern);
            }
            return result;
        }
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setIncludes(new String[]{filePattern});
        scanner.setBasedir(".");
        scanner.setCaseSensitive(false);
        scanner.scan();
        String[] files = scanner.getIncludedFiles();
        if (files.length == 0) {
            throw new RuntimeException("No file matches pattern \"" + filePattern + "\".");
        }
        for (String f: files) {
            if (f.endsWith(fileExtension)) {
                result.add(f);
            }
        }
        return result;
    }
}
