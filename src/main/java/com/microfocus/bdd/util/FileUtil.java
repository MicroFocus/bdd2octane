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
package com.microfocus.bdd.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

public class FileUtil {

    public static String getFileCreationTime(String filePath) {
        File file = new File(filePath);
        Path path = file.toPath();
        try {
            BasicFileAttributes fileAttributes = Files.readAttributes(path, BasicFileAttributes.class);
            return String.valueOf(fileAttributes.creationTime().toMillis());
        } catch (IOException e) {
            return ""; //TODO:error just return empty, log error
        }

    }

    public static String getResultFilePath(String resultFilePath, String framework) {
        if (resultFilePath == null || resultFilePath.isEmpty()) {
            return framework + "-result.xml";
        }

        File resultFile = new File(resultFilePath);
        if (resultFile.getParentFile() != null && !resultFile.getParentFile().exists()) {
            boolean isCreated = resultFile.getParentFile().mkdirs();
            if (!isCreated) {
                throw new RuntimeException("failed to create result output file: " + resultFilePath);
            }
        }
        return resultFilePath;
    }

    public static boolean isPathValid(String path) {
        File file = new File(path);
        if (file.isAbsolute()) {
            return false;
        }

        try {
            Path normalized = file.toPath().normalize();
            if (normalized.startsWith("..")) {
                return false;
            }
        } catch (InvalidPathException e) {
            return false;
        }
        return true;
    }

    public static void printFiles(List<String> files, String type, String path) {
        System.out.println("All " + type + " files matching pattern \"" + path + "\":");
        for (String file : files) {
            System.out.println("\t" + file);
        }
    }
}
