package fr.eseo.dis.amiaudluc.spinoffapp.model;

/**
 * Created by lucasamiaud on 28/02/2018.
 */

public enum Language {

    en("English","EN"),
    fr("French","FR"),
    es("Spanish","ES"),
    us("USA","US"),
    unknown("UNKNOWN","unknown");

    private String fullName;
    private String majThis;

    Language(String fullName,String majThis){
        this.fullName = fullName;
        this.majThis = majThis;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getMajThis() {
        return majThis;
    }

    public void setMajThis(String majThis) {
        this.majThis = majThis;
    }
}
