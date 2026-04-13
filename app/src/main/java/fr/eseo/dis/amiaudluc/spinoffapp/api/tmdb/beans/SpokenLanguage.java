package fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SpokenLanguage {
    @SerializedName("iso_639_1")
    private String iso6391;
    private String name;
}
