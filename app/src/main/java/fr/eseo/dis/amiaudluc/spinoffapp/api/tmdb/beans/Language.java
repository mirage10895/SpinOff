package fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lucasamiaud on 28/02/2018.
 */

public enum Language {
    @SerializedName("en")
    EN,
    @SerializedName("fr")
    FR,
    @SerializedName("es")
    ES,
    @SerializedName("us")
    US,
    @SerializedName("gb")
    GB,
    DEFAULT
    ;
}
