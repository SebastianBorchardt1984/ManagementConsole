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

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

@Entity
@NamedQuery(name = "ProcessStatus.findAll", query = "SELECT f FROM ProcessStatus f ORDER BY f.id", hints = @QueryHint(name = "org.hibernate.cacheable", value = "true"))
@Cacheable
@Table(name = "ProcessStatus")
public class ProcessStatus extends PanacheEntityBase {

    public enum ErlaubteStatus {
        NEW,
        PROGRESS,
        POSTPONED,
        ABANDONED,
        WITHDRAWN,
        SUCCESSFUL,
        FAILED,
        DELETED
    }

    public enum ErlaubteExceptionTypes {
        AE,
        BE,
        EX
        
    }


    @Id
    @Column(name = "reference") 
    public String reference;

    @Column(name = "reference_date") 
    public LocalDate referenceDate;

    @Column(name = "status") 
    public String status;

    @Column(name = "current_task") 
    public String currentTask;

    @Column(name = "last_successful_task")
    public String lastSuccessfulTask;
    
    @Column(name = "exception_type")
    public String exceptionType;

    @Column(name = "exception_reason")
    public String exceptionReason;

    @Column(name = "sub_task")
    public String subTask;

    @JsonIgnore
    @CreationTimestamp
    @Column(name = "created", columnDefinition = "timestamp with time zone") 
    ZonedDateTime created;

    @JsonIgnore
    @UpdateTimestamp
    @Column(name = "updated", columnDefinition = "timestamp with time zone") 
    ZonedDateTime updated;

    @Transactional
    public static ProcessStatus findById(String reference) {
        return find("reference", reference).firstResult();
    }

    public static List<ProcessStatus> findStatusComplete() {
        return list("status", "5");
    }

    public static void deleteIDs(String reference) {
        delete("reference", reference);
    }

    @Transactional
    public static ProcessStatus create(ProcessStatus processStatus) {

        ProcessStatus entity = ProcessStatus.findById(processStatus.reference);

        if (entity != null) {
            throw new EntityExistsException("BusinessKey existiert bereits");
        }
        ZoneId berlinZone = ZoneId.of("Europe/Berlin");
        processStatus.created = ZonedDateTime.now(berlinZone);
        processStatus.updated = processStatus.created;
        processStatus.persist();
        return processStatus;

    }

    @Transactional
    public static ProcessStatus update(String reference, ProcessStatus processStatus) {
        ProcessStatus entity = ProcessStatus.findById(reference);        
        ZoneId berlinZone = ZoneId.of("Europe/Berlin");
       
        if (entity == null) {
            throw new NotFoundException();
        }

        entity.status = processStatus.status;
        entity.currentTask = processStatus.currentTask;
        entity.lastSuccessfulTask = processStatus.lastSuccessfulTask;
        entity.exceptionType = processStatus.exceptionType;
        entity.exceptionReason = processStatus.exceptionReason;
        entity.subTask = processStatus.subTask;
        entity.updated = ZonedDateTime.now(berlinZone);;
        ProcessStatus.ValidProcessstatusCheck(entity); 
        return entity;
    }


    public static void ValidProcessstatusCheck (ProcessStatus processStatus) { 

        //check ob Status gefüllt und einen korrekten Wert enthält
        if (!ProcessStatus.ValidStatusCheck(processStatus.status)) {
            throw new RuntimeException("Dieser Status ist nicht erlaubt");
        }

        if (processStatus.exceptionType != null) {
            if (!ProcessStatus.ValidExceptionType(processStatus.exceptionType)) {
                throw new RuntimeException("Dieser Exceptiontype ist nicht erlaubt. Erlaubt sind u.a. BE, AE oder EX");
            } else {
                if ((processStatus.exceptionReason == null || (processStatus.exceptionReason.isBlank()) || processStatus.exceptionReason.isEmpty())) {
                    throw new RuntimeException("Wenn eine Exception gespeichert werden soll, muss auch ein Detailmeldung im Feld exceptionReason erfolgen");
                }
            }
        }
    }

    public static boolean ValidStatusCheck(String status)
    {
        for (ErlaubteStatus erlaubteStatus : ErlaubteStatus.values())
        {
            if (erlaubteStatus.name().equals(status))
            {
                return true;
            }
        }
    
        return false;
    }


    public static boolean ValidExceptionType(String exceptionType)
    {
        for (ErlaubteExceptionTypes erlaubteExceptionType : ErlaubteExceptionTypes.values())
        {
            if (erlaubteExceptionType.name().equals(exceptionType))
            {
                return true;
            }
        }
    
        return false;
    }

    public static boolean isNullOrEmptyOrBlank(String str) {
        return str == null || StringUtils.isBlank(str); 
    }

    public static ProcessStatus mapFromProcessStatus(ProcessStatus source) {
        ProcessStatus target = new ProcessStatus();

        for (Field field : ProcessStatus.class.getDeclaredFields()) {
            try {
                field.setAccessible(true);
                Object value = field.get(source);
                if (value != null) {
                    field.set(target, value);
                }
            } catch (IllegalAccessException e) {
                Logger.getLogger(ProcessStatus.class.getName()).log(Level.WARNING, 
                        "Failed to access field: " + field.getName(), e);
                        throw new RuntimeException("ProcessStatus Mapping fehlerhaft");
            }
        }

        return target;
    }

}
