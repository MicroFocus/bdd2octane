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
package com.microfocus.bdd.api;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.Objects;
import java.util.Optional;

public class OctaneStep {

    // step name does not contain keywords like Given, Then, *
    private final String name;
    // keyword is a localized keyword
    private final String keyword;
    // step type is Given, When, Then, But (* is translated as well)
    private final String stepType;
    private Status status;
    private Long duration = 0l;
    private String errorMessage = null;
    private final Optional<String> docString;
    private final Optional<String> dataTable;
    private Boolean addSystemErrors = false;

    public OctaneStep(String keyword, String name, String stepType, Optional<String> docString, Optional<String> dataTable) {
        this.name = name;
        this.keyword = keyword;
        this.stepType = stepType;
        this.docString = docString;
        this.dataTable = dataTable;
    }

    public String getStepType() {
        return stepType;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getName() {
        return name;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Boolean getAddSystemErrors() {
        return addSystemErrors;
    }

    public void setAddSystemErrors(Boolean addSystemErrors) {
        this.addSystemErrors = addSystemErrors;
    }

    public void writeToXml(XMLStreamWriter writer) throws XMLStreamException {
        StringBuilder stepName = new StringBuilder(getCompleteStepName());
        docString.ifPresent(ds-> {
            stepName.append('\n');
            stepName.append(ds);
        });
        dataTable.ifPresent(dt -> {
            stepName.append('\n');
            stepName.append(dt);
        });

        writer.writeStartElement("step");
        writer.writeAttribute("name", stepName.toString());
        writer.writeAttribute("duration", Long.toString(duration));
        writer.writeAttribute("status", status == null ? "undefined" : status.toString());

        if (status == Status.FAILED) {
            writer.writeCharacters("\n");
            writer.writeStartElement("error_message");
            writer.writeCData(errorMessage);
            writer.writeEndElement();//error_message
            writer.writeCharacters("\n");
        }
        writer.writeEndElement();
        writer.writeCharacters("\n");
    }

    public String getCompleteStepName() {
        return keyword + " " + name;
    }
}
