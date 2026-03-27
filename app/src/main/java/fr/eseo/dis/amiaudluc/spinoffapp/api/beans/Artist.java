package fr.eseo.dis.amiaudluc.spinoffapp.api.beans;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDate;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by lucasamiaud on 28/02/2018.
 */

@Getter
@Setter
@NoArgsConstructor
public class Artist implements Media {
    private int id;
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
    private LocalDate birthday;
    private String homepage;
    @SerializedName("movie_credits")
    private Credits<Movie> movies;
    @SerializedName("tv_credits")
    private Credits<Serie> series;

    @Override
    public String getMediaType() {
        return Media.ARTIST;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return super.equals(obj);
    }
}
