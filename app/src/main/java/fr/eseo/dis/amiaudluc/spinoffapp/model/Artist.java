package fr.eseo.dis.amiaudluc.spinoffapp.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by lucasamiaud on 28/02/2018.
 */

@Getter
@Setter
public class Artist implements Media {
    private int id;
    private String name;
    private String profilePath;
    private String creditId;
    private String department;
    private String job;
    private String character;
    private int gender;
    private List<Movie> movies;
    private List<Serie> series;
    private String biography;
    private String placeOfBirth;
    private String type;

    public Artist() {
        this.id = 0;
        this.name = "";
        this. profilePath = "";
    }

    @Override
    public String getMediaType() {
        return this.type;
    }

    @Override
    public void setMediaType(String mediaType) {
        this.type = mediaType;
    }
}
