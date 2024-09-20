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
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import org.jbpm.ruleflow.instance.RuleFlowProcessInstance;
import org.kie.api.event.process.ProcessNodeEvent;
import org.kie.kogito.internal.process.runtime.KogitoProcessContext;
import org.kie.kogito.process.ProcessInstance;
import org.mvel2.MVEL;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.persistence.EntityExistsException;

public class ProcessStatusListenerService {

    public static void UpdateProcessStatus(ProcessNodeEvent event, Map<String, Object> metadata) {
        
        
        String BusinessKey = SearchBusinessKey(event, metadata);
        if (SearchBusinessKey(event, metadata) == null) {
            throw new MissingReferenceException("Kein Businesskey/Reference vorhanden");
        } 

        ProcessStatus entity = ProcessStatus.findById(BusinessKey);
        if (entity == null) {
            throw new MissingReferenceException("Kein Datenbankeintrag zum Businesskey/reference gefunden");
        }

        //alle Properties müssen übergeben werden -> status, currentTask und Businesskey (ist der DB Key). 
        //Sofern Status nicht übergeben wird, wird alles übersprungen da davon ausgegangen wird das ein Status gespeichert wird. 

        if (metadata.containsKey("status") && ProcessStatus.ValidStatusCheck(metadata.get("status").toString().toUpperCase())) {
            String status = metadata.get("status").toString();
            entity.status = status;
        } else {
            throw new InvalidMetadataException("Status muss als Attribut übergeben werden!");
        }  

        if (!metadata.containsKey("currentTask")) {
            throw new InvalidMetadataException("currentTask muss als Attribut übergeben werden!");
        }

        String currentTask = metadata.get("currentTask").toString();
        if (currentTask == null || currentTask.isEmpty() || currentTask.isBlank()) {
            throw new InvalidMetadataException("currentTask ist ungültig (null, leer oder nur Leerzeichen)");
        }

        entity.currentTask = currentTask;


        if (metadata.containsKey("lastSuccessfulTask")) {
            entity.lastSuccessfulTask = metadata.get("lastSuccessfulTask").toString();
        }
        
        if (metadata.containsKey("exceptionType")) {
            String exceptionType = metadata.get("exceptionType").toString();
            if (exceptionType == null || exceptionType.isEmpty() || exceptionType.isBlank()) {
                entity.exceptionType = "";
            } else {
                ProcessStatus.ValidExceptionType(exceptionType);
                entity.exceptionType = exceptionType;
            }
        }

        //Plausicheck muss noch erfolgen auf Status und Exceptiontype
        
        if (metadata.containsKey("exceptionReason")) {
            String exceptionReason = metadata.get("exceptionReason").toString();
            if (exceptionReason == null || exceptionReason.isEmpty() || exceptionReason.isBlank()) {
                entity.exceptionReason = "";
            } else {            
               entity.exceptionReason = exceptionReason;
            }
        }

        //Plausicheck muss noch erfolgen auf Exceptiontype und Exception Reason 

        if (metadata.containsKey("subTask")) {
            String subTask = metadata.get("subTask").toString();
            if (subTask == null || subTask.isEmpty() || subTask.isBlank()) {
                entity.subTask = "";
            } else {            
               entity.subTask = subTask;
            }
        }       
        
        if (metadata.containsKey("referenceDate")) {
            String referenceDate = metadata.get("referenceDate").toString();
            if (referenceDate == null || referenceDate.isEmpty() || referenceDate.isBlank()) {
                throw new InvalidMetadataException("referenceDate ist ungültig (null, leer oder nur Leerzeichen)");
            } else {            
                entity.referenceDate = LocalDate.parse(referenceDate);
            }
        }
            //Entity updaten und persitieren
            ZoneId berlinZone = ZoneId.of("Europe/Berlin");
            entity.updated = ZonedDateTime.now(berlinZone);

            //Last Check
            ProcessStatus.ValidProcessstatusCheck(entity);
            //Persist
            ProcessStatus.update(BusinessKey, entity);
            //Fallstatus in Variable zurückschreiben
            ProcessStatusListenerService.CurrentProcessStatusToVariable((ProcessNodeEvent) event, metadata);
            if (metadata.containsKey("setState")) {
                String KogitoProcessInstanceState = metadata.get("setState").toString();
                if (KogitoProcessInstanceState == null || KogitoProcessInstanceState.isEmpty() || KogitoProcessInstanceState.isBlank()) {
                    throw new InvalidMetadataException("KogitoProcessInstanceState ist ungültig (null, leer oder nur Leerzeichen)");
                } else {            
                    RuleFlowProcessInstance processInstance = (RuleFlowProcessInstance) event.getNodeInstance().getProcessInstance();
                    
                                        //
                    //int STATE_PENDING = 0;
                    //int STATE_ACTIVE = 1;
                    //int STATE_COMPLETED = 2;
                    //int STATE_ABORTED = 3;
                    //int STATE_SUSPENDED = 4;
                    //int STATE_ERROR = 5;
                    //

                    switch (KogitoProcessInstanceState) {
                        case "STATE_PENDING":
                          processInstance.setState(ProcessInstance.STATE_ABORTED);
                          break;
                        case "STATE_ACTIVE":
                          processInstance.setState(ProcessInstance.STATE_ACTIVE);
                          break;                        
                        case "STATE_COMPLETED":
                          processInstance.setState(ProcessInstance.STATE_COMPLETED);
                          break; 
                        case "STATE_ABORTED":
                          processInstance.setState(ProcessInstance.STATE_ABORTED);
                          break; 
                        case "STATE_SUSPENDED":
                          processInstance.setState(ProcessInstance.STATE_SUSPENDED);
                          break; 
                        case "STATE_ERROR":
                          processInstance.setState(ProcessInstance.STATE_ERROR);
                          break; 
                        default: 
                          throw new InvalidMetadataException("KogitoProcessInstanceState ist ungültig, bitte nur erlaubte States eintragen: STATE_PENDING, STATE_ACTIVE, STATE_COMPLETED, STATE_ABORTED, STATE_SUSPENDED, STATE_ERROR )");
                        }
                    
                }   
            }

        }
            
    public static String SearchBusinessKey(ProcessNodeEvent event, Map<String, Object> metadata) {

        RuleFlowProcessInstance processInstance = (RuleFlowProcessInstance) event.getNodeInstance().getProcessInstance();
        
        //CorrelationKey ist technisch der Businesskey den man z.B. über den Webservice setzen kann 
        if (processInstance.getCorrelationKey() == null || processInstance.getCorrelationKey().isEmpty() || (processInstance.getCorrelationKey().isBlank())) {

            //es gibt zwar diese Methode, aber ich weiß nicht ob die jemals befüllt ist. Hab sie aber implementiert vorsichtshalber
            if (processInstance.getBusinessKey() == null || processInstance.getBusinessKey().isEmpty() || (processInstance.getBusinessKey().isBlank())) {

                //  wenn beides nicht befüllt ist (Correlation oder Businesskey) dann noch überprüfen ob der Businesskey in den Metadaten mit übergeben wurde als "BusinessKey"
                if (metadata.get("BusinessKey") == null || metadata.get("BusinessKey").toString().isEmpty() || (metadata.get("BusinessKey").toString().isBlank())) {
                    return null;
                } else {
                    return metadata.get("BusinessKey").toString();
                }
            } else {
                return processInstance.getBusinessKey();
            }
        } else {
            return processInstance.getCorrelationKey();
        }
    }

    public static void SetBusinessKey(ProcessNodeEvent event, Map<String, Object> metadata) {

        RuleFlowProcessInstance processInstance = (RuleFlowProcessInstance) event.getNodeInstance().getProcessInstance();
        String BusinessKey = SearchBusinessKey(event, metadata);

        if (BusinessKey == null || BusinessKey.isEmpty() || BusinessKey.isBlank()) {
            BusinessKey = metadata.get("setBusinessKey").toString();
        } else {
            System.out.println("Methode setBusinessKey wurde übersprungen, da der Busineskey bereits gesetzt wurde");
            return;
        }

        if (BusinessKey.startsWith("#{")) {

            processInstance.setCorrelationKey(ExececuteSimpleMVELexpression(BusinessKey, event).toString());

        } else {
            processInstance.setCorrelationKey(BusinessKey);
        }
    }

    public static void CurrentProcessStatusToVariable(ProcessNodeEvent event, Map<String, Object> metadata) {

        RuleFlowProcessInstance processInstance = (RuleFlowProcessInstance) event.getNodeInstance().getProcessInstance();
        String BusinessKey = SearchBusinessKey(event, metadata);
        ProcessStatus entity = ProcessStatus.findById(BusinessKey);
        
        if (entity == null) {
            throw new RuntimeException("Kein Datenbankeintrag zum Businesskey gefunden");
        }
        Map<String, Object> test = processInstance.getVariables();

        FallStatus test2 = new FallStatus();

        test2.reference = entity.reference;
        test2.referenceDate = entity.referenceDate;
        test2.status = entity.status;
        test2.currentTask = entity.currentTask;
        test2.lastSuccessfulTask = entity.lastSuccessfulTask;
        test2.exceptionType = entity.exceptionType;
        test2.exceptionReason = entity.exceptionReason;
        test2.subTask = entity.subTask;
        test2.created = entity.created;
        test2.updated = entity.updated;
        

        //Set Kogito Variablie
        processInstance.setVariable("fallStatus", test2);
        
        if (processInstance.getVariables().containsKey("fallStatus")) {

            FallStatus fallStatus = new FallStatus();

            fallStatus.reference = entity.reference;
            fallStatus.referenceDate = entity.referenceDate;
            fallStatus.status = entity.status;
            fallStatus.currentTask = entity.currentTask;
            fallStatus.lastSuccessfulTask = entity.lastSuccessfulTask;
            fallStatus.exceptionType = entity.exceptionType;
            fallStatus.exceptionReason = entity.exceptionReason;
            fallStatus.subTask = entity.subTask;
            fallStatus.created = entity.created;
            fallStatus.updated = entity.updated;
            

            //Set Kogito Variablie
            processInstance.setVariable("fallStatus", fallStatus);
        } else {
            throw new RuntimeException("Die Variable-> fallStatus mit typ database.FallStatus <- muss für diese Methode gesetzt sein");
        }

    }

    public static void CreateProcessStatus(ProcessNodeEvent event, Map<String, Object> metadata) {

        String BusinessKey = SearchBusinessKey(event, metadata);
        ProcessStatus processStatus = new ProcessStatus();
        ProcessStatus entity = ProcessStatus.findById(BusinessKey);
        RuleFlowProcessInstance processInstance = (RuleFlowProcessInstance) event.getNodeInstance().getProcessInstance();

        String status = "NEW";        
        String currentTask = "start";
        LocalDate referenceDate = LocalDate.parse("9999-12-31");

        if (entity != null) {
            if (metadata.get("CreateProcessStatus").toString() == "CatchEntityExistsException") {
                System.out.println(
                        "EntityExistsException wurde durch CatchEntityExistsException in Metadata CreateProcessStatus abgefangen. Ursache: es ist bereits eine ID der Process_Status Tabelle enthalten");
                return;
            }
            throw new EntityExistsException("BusinessKey existiert bereits");
        }

        if (processInstance.getVariables().containsKey("fallStatus")) {
            if (metadata.get("status") == null || metadata.get("status").toString().isEmpty() || (metadata.get("status").toString().isBlank())) {
                status = "NEW";
            } else {
                status = metadata.get("status").toString().toUpperCase();
            }
        }

        if (processInstance.getVariables().containsKey("fallStatus")) {
            if (metadata.get("currentTask") == null || metadata.get("currentTask").toString().isEmpty() || (metadata.get("currentTask").toString().isBlank())) {
                currentTask = "start";
            } else {
                currentTask = metadata.get("currentTask").toString();
            }
        }

        if (processInstance.getVariables().containsKey("fallStatus")) {
            if (metadata.get("referenceDate") == null || metadata.get("referenceDate").toString().isEmpty() || (metadata.get("referenceDate").toString().isBlank())) {
                throw new RuntimeException("Es ist kein reference_date gesetzt");
            } else {
                
                if (metadata.get("referenceDate").toString().startsWith("#{")) {
                    String expression = metadata.get("referenceDate").toString();
                    referenceDate = LocalDate.parse(ExececuteSimpleMVELexpression(expression, event).toString());
        
                } else {
                    referenceDate = LocalDate.parse((metadata.get("referenceDate").toString()));
                }
                
            }
        }

        ZoneId berlinZone = ZoneId.of("Europe/Berlin");
        processStatus.created = ZonedDateTime.now(berlinZone);
        processStatus.updated = processStatus.created;
        processStatus.reference = BusinessKey;
        processStatus.status = status;
        processStatus.currentTask = currentTask;
        processStatus.referenceDate = referenceDate;

        // Check 
        ProcessStatus.ValidProcessstatusCheck(processStatus);
        //Persist
        ProcessStatus.create(processStatus);
    }

    public static Object ExececuteSimpleMVELexpression(String expressionString, ProcessNodeEvent event) {
        try {
            // Sicheren Parser-Kontext erstellen
            RuleFlowProcessInstance processInstance = (RuleFlowProcessInstance) event.getNodeInstance().getProcessInstance();
            
            expressionString = expressionString.replace("#{", "");
            expressionString = expressionString.replace("}", "");

            int iend = expressionString.indexOf(".");
            String VariableName = new String();
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());

             if (iend != -1) {

                VariableName = expressionString.substring(0, iend);
                Map<String, Object> map = objectMapper.convertValue(processInstance.getVariable(VariableName), new TypeReference<Map<String, Object>>() {});
                Map<String, Object> finalmap = new HashMap();
                finalmap.put(VariableName, map);
                return MVEL.eval(expressionString, finalmap);

            } else {
                VariableName = expressionString;
                return processInstance.getVariable(VariableName).toString();              
            }
        } catch (Exception e) {
            // Fehlerbehandlung (z.B. Logging, benutzerdefinierte Exceptions)
            throw new RuntimeException("Fehler bei der Auswertung des MVEL-Ausdrucks: " + e.getMessage(), e);
        }
    }
}

class InvalidMetadataException extends RuntimeException {
    InvalidMetadataException(String message) {
        super(message);
    }
}

class MissingReferenceException extends RuntimeException {
    MissingReferenceException(String message) {
        super(message);
    }
}
