/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package database;

import java.util.Arrays;
import java.util.Map;

import org.kie.api.event.process.*;
import org.kie.kogito.internal.process.event.KogitoProcessEventListener;

public class ProcessStatusListener implements KogitoProcessEventListener {

    public void afterNodeLeft(ProcessNodeLeftEvent event) {

    }

    public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {

        Map<String, Object> metadata = event.getNodeInstance().getNode().getMetaData();

        if (metadata.containsKey("debug")) {
            //ProcessStatusListenerService.SetBusinessKey(event, metadata);
            String test = "test";
        }

    }// itentionally left blank

    public void afterProcessCompleted(ProcessCompletedEvent event) {
        // intentionally left blank
    }

    public void afterProcessStarted(ProcessStartedEvent event) {

        // Map<String, Object> metadata = event.getProcessInstance().getProcess().getMetaData();
        // // Map Debugmap = event.getProcessInstance().getProcess().getMetaData();

        // if (metadata.containsKey("setBusinessKey")) {
        //     ProcessStatusListenerService.SetBusinessKey((ProcessNodeEvent) event, metadata);
        // }

        // if (metadata.containsKey("CreateProcessStatus")) {
        //     ProcessStatusListenerService.CreateProcessStatus((ProcessNodeEvent) event, metadata);
        // }

        // String[] keysToCheck = { "status", "currentTask", "reference", "rerencdeDate", "lastSuccessfulTask",
        //         "exceptionType", "exceptionReason", "subTask", "setState" };
        // if (Arrays.stream(keysToCheck).anyMatch(metadata::containsKey)) {
        //     ProcessStatusListenerService.SearchBusinessKey((ProcessNodeEvent) event, metadata);
        //     ProcessStatusListenerService.UpdateProcessStatus((ProcessNodeEvent) event, metadata);
        // }

        // if (metadata.containsKey("getProcessStatus")) {
        //     ProcessStatusListenerService.CurrentProcessStatusToVariable((ProcessNodeEvent) event, metadata);
        // }

    }

    public void afterVariableChanged(ProcessVariableChangedEvent event) {
        // System.out.println("VariableName: " + event.getVariableId());

    }

    public void beforeNodeLeft(ProcessNodeLeftEvent event) {

                        // Rheinfolge ist absichtlich so gewählt ggf. kann man später alle Metadaten
        // über Sichtbare Rheinfolge als Logik implementieren. Metadaten kommen als
        // hashMap zurück, allerdings weiß ich nicht ob die indexartig erzeugt werden

        Map<String, Object> metadata = event.getNodeInstance().getNode().getMetaData();

        if (metadata.containsKey("setBusinessKey")) {
            ProcessStatusListenerService.SetBusinessKey(event, metadata);
        }

        if (metadata.containsKey("CreateProcessStatus")) {
            ProcessStatusListenerService.CreateProcessStatus(event, metadata);
        }

        String[] keysToCheck = { "status", "currentTask", "reference", "rerencdeDate", "lastSuccessfulTask",
                "exceptionType", "exceptionReason", "subTask", "setState" };
        if (Arrays.stream(keysToCheck).anyMatch(metadata::containsKey)) {
            ProcessStatusListenerService.SearchBusinessKey(event, metadata);
            ProcessStatusListenerService.UpdateProcessStatus(event, metadata);
        }

        if (metadata.containsKey("getProcessStatus")) {
            ProcessStatusListenerService.CurrentProcessStatusToVariable(event, metadata);
        }

    }

    public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
        // Map<String, Object> metadata =
        // event.getNodeInstance().getProcessInstance().getProcess().getMetaData();
    }

    public void beforeProcessCompleted(ProcessCompletedEvent event) {
        // // intentionally left blank
    }

    public void beforeProcessStarted(ProcessStartedEvent event) {

        Map<String, Object> metadata = event.getProcessInstance().getProcess().getMetaData();
        // Map Debugmap = event.getProcessInstance().getProcess().getMetaData();

        if (metadata.containsKey("setBusinessKey")) {
            ProcessStatusListenerService.SetBusinessKey((ProcessNodeEvent) event, metadata);
        }

        if (metadata.containsKey("CreateProcessStatus")) {
            ProcessStatusListenerService.CreateProcessStatus((ProcessNodeEvent) event, metadata);
        }

        String[] keysToCheck = { "status", "currentTask", "reference", "rerencdeDate", "lastSuccessfulTask",
                "exceptionType", "exceptionReason", "subTask", "setState" };
        if (Arrays.stream(keysToCheck).anyMatch(metadata::containsKey)) {
            ProcessStatusListenerService.SearchBusinessKey((ProcessNodeEvent) event, metadata);
            ProcessStatusListenerService.UpdateProcessStatus((ProcessNodeEvent) event, metadata);
        }

        if (metadata.containsKey("getProcessStatus")) {
            ProcessStatusListenerService.CurrentProcessStatusToVariable((ProcessNodeEvent) event, metadata);
        }

    }

    public void beforeVariableChanged(ProcessVariableChangedEvent event) {
        // intentionally left blank
    }
}
