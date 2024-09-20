package org.aoknordost.axxxxx_prozess;

import java.io.Serializable;

public class businesspartner implements Serializable {

    public String gpnr;
    public String pkrv;
    public String pkkv;
    public String pkkvde;
    public String geburtsdatum;
    public String vorname;
    public String nachname;

    public businesspartner() {
    }

    public businesspartner(String gpnr, String pkrv, String pkkv, String pkkvde, String geburtsdatum, String vorname,
            String nachname) {

        super();
        this.gpnr = gpnr;
        this.pkrv = pkrv;
        this.pkkvde = pkkvde;
        this.geburtsdatum = geburtsdatum;
        this.vorname = vorname;
        this.nachname = nachname;
    }

    // Getter Methods 

    public String getGpnr() {
        return gpnr;
    }

    public String getPkrv() {
        return pkrv;
    }

    public String getPkkv() {
        return pkkv;
    }

    public String getPkkvde() {
        return pkkvde;
    }

    public String getGeburtsdatum() {
        return geburtsdatum;
    }

    public String getVorname() {
        return vorname;
    }

    public String getNachname() {
        return nachname;
    }

    // Setter Methods 

    public void setGpnr(String gpnr) {
        this.gpnr = gpnr;
    }

    public void setPkrv(String pkrv) {
        this.pkrv = pkrv;
    }

    public void setPkkv(String pkkv) {
        this.pkkv = pkkv;
    }

    public void setPkkvde(String pkkvde) {
        this.pkkvde = pkkvde;
    }

    public void setGeburtsdatum(String geburtsdatum) {
        this.geburtsdatum = geburtsdatum;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    public void setNachname(String nachname) {
        this.nachname = nachname;
    }

    @Override
    public String toString() {
        return "businesspartner [gpnr=" + gpnr + ", pkrv=" + pkrv + ", pkkv=" + pkkv + ", pkkvde=" + pkkvde + ", geburtsdatum=" + geburtsdatum + ", vorname=" + vorname + ", nachname=" + nachname
                + "]";
    }

    
}
