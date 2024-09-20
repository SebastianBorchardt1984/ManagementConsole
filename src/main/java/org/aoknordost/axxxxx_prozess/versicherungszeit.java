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
package org.aoknordost.axxxxx_prozess;

import java.io.Serializable;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection(serialization = true)
public class versicherungszeit implements Serializable {

    public String tbid;

    public String geschäftspartnernummer;

    public String versicherungszeitentyp;

    public String versicherungsart;

    public String storno;

    public String interimskennzeichen;

    public String beginn;

    public String ende;

    public versicherungszeit() {
    }

    public String gettbid() {
        return tbid;
    }

    public void settbid(String tbid) {
        this.tbid = tbid;
    }

    public String getgeschäftspartnernummer() {
        return geschäftspartnernummer;
    }

    public void setgeschäftspartnernummer(String geschäftspartnernummer) {
        this.geschäftspartnernummer = geschäftspartnernummer;
    }

    public String getversicherungszeitentyp() {
        return versicherungszeitentyp;
    }

    public void setversicherungszeitentyp(String versicherungszeitentyp) {
        this.versicherungszeitentyp = versicherungszeitentyp;
    }

    public String getversicherungsart() {
        return versicherungsart;
    }

    public void setversicherungsart(String versicherungsart) {
        this.versicherungsart = versicherungsart;
    }

    public String getstorno() {
        return storno;
    }

    public void setstorno(String storno) {
        this.storno = storno;
    }

    public String getinterimskennzeichen() {
        return interimskennzeichen;
    }

    public void setinterimskennzeichen(String interimskennzeichen) {
        this.interimskennzeichen = interimskennzeichen;
    }

    public String getbeginn() {
        return beginn;
    }

    public void setbeginn(String beginn) {
        this.beginn = beginn;
    }

    public String getende() {
        return ende;
    }

    public void setende(String ende) {
        this.ende = ende;
    }

    public versicherungszeit(String tbid, String geschäftspartnernummer, String versicherungszeitentyp, String versicherungsart, String storno, String interimskennzeichen,
            String beginn, String ende) {

        super();
        this.tbid = tbid;
        this.geschäftspartnernummer = geschäftspartnernummer;
        this.versicherungszeitentyp = versicherungszeitentyp;
        this.versicherungsart = versicherungsart;
        this.storno = storno;
        this.interimskennzeichen = interimskennzeichen;
        this.beginn = beginn;
        this.ende = ende;
    }

    @Override
    public String toString() {
        return "versicherungszeit [tbid=" + tbid + ", geschäftspartnernummer=" + geschäftspartnernummer + ", versicherungszeitentyp=" + versicherungszeitentyp + ", versicherungsart="
                + versicherungsart + ", storno=" + storno + ", interimskennzeichen=" + interimskennzeichen + ", beginn=" + beginn + ", ende=" + ende + "]";
    }
}
