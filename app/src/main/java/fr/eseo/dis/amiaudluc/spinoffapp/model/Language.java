package fr.eseo.dis.amiaudluc.spinoffapp.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lucasamiaud on 28/02/2018.
 */

public enum Language {

    @SerializedName("en")
    EN("English","en"),
    @SerializedName("fr")
    FR("French","fr"),
    @SerializedName("es")
    ES("Spanish","es"),
    @SerializedName("us")
    US("USA","us"),
    @SerializedName("gb")
    GB("Great Britain","gb"),
    DEFAULT("UNKNOWN","unknown");

    private String fullName;
    private String name;

    Language(String fullName, String majThis){
        this.fullName = fullName;
        this.name = majThis;
    }

    public String getFullName() {
        return fullName;
    }

    public String getName() {
        return name;
    }
}
