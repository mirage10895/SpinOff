package fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans;

import java.time.LocalDate;
import java.util.List;

import androidx.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by lucasamiaud on 08/03/2018.
 */

@Getter
@Setter
public class Episode {
    private String name;
    private LocalDate airDate;
    private List<Artist> crew;
    private Integer episodeNumber;
    private List<Artist> guestStars;
    private String overview;
    private String productionCode;
    private Integer seasonNumber;
    private String showId;
    private String stillPath;
    private Double voteAverage;
    private Integer voteCount;
    private Integer runtime;

    @Override
    public String toString() {
        return "S" + seasonNumber +
                "E" + episodeNumber +
                ": " + name;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return super.equals(obj);
    }
}
