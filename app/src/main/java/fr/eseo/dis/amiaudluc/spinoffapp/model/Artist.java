package fr.eseo.dis.amiaudluc.spinoffapp.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by lucasamiaud on 28/02/2018.
 */

@Getter
@Setter
public class Artist implements Media {
    private Integer id;
    private String name;
    private String profilePath;
    private String creditId;
    private String department;
    private String job;
    private String character;
    private Integer gender;
    private List<String> alsoKnownAs;
    private String biography;
    private String placeOfBirth;
    private String mediaType;
    private Date birthday;
    @SerializedName("movie_credits")
    private Credits<Movie> movies;
    @SerializedName("tv_credits")
    private Credits<Serie> series;

    public Artist() {
        this.id = 0;
        this.name = "";
        this. profilePath = "";
    }

    @Override
    public String getMediaType() {
        return this.mediaType;
    }
}
