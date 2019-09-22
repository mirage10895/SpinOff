package fr.eseo.dis.amiaudluc.spinoffapp.model;

import java.util.List;

import fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.model.EpisodeDatabase;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by lucasamiaud on 08/03/2018.
 */

@Getter
@Setter
public class Episode extends EpisodeDatabase {
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

    @Override
    public String toString() {
        return "S" + seasonNumber +
                "E" + episodeNumber +
                ": " + super.getName();
    }
}
