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
import java.time.ZonedDateTime;
import java.util.Map;

public class FallStatus {

    public String reference;
    public LocalDate referenceDate;
    public String status;
    public String currentTask;
    public String lastSuccessfulTask;
    public String exceptionType;
    public String exceptionReason;
    public String subTask;
    ZonedDateTime created;
    ZonedDateTime updated;
   
    // reference
    public String getReference() {
        return reference;
    }

    //test
    public void setReference(String reference) {
        this.reference = reference;
    }

    // referenceDate
    public LocalDate getReferenceDate() {
        return referenceDate;
    }
    public void setReferenceDate(LocalDate referenceDate) {
        this.referenceDate = referenceDate;
    }

    // status
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    // currentTask
    public String getCurrentTask() {
        return currentTask;
    }
    public void setCurrentTask(String currentTask) {
        this.currentTask = currentTask;
    }

    // lastSuccessfulTask
    public String getLastSuccessfulTask() {
        return lastSuccessfulTask;
    }
    public void setLastSuccessfulTask(String lastSuccessfulTask) {
        this.lastSuccessfulTask = lastSuccessfulTask;
    }

    // exceptionType
    public String getExceptionType() {
        return exceptionType;
    }
    public void setExceptionType(String exceptionType) {
        this.exceptionType = exceptionType;
    }

    // exceptionReason
    public String getExceptionReason() {
        return exceptionReason;
    }
    public void setExceptionReason(String exceptionReason) {
        this.exceptionReason = exceptionReason;
    }

    // subTask
    public String getSubTask() {
        return subTask;
    }
    public void setSubTask(String subTask) {
        this.subTask = subTask;
    }

    // created
    public ZonedDateTime getCreated() {
        return created;
    }
    public void setCreated(ZonedDateTime created) {
        this.created = created;
    }

    // updated
    public ZonedDateTime getUpdated() {
        return updated;
    }
    public void setUpdated(ZonedDateTime updated) {
        this.updated = updated;
    }


}
