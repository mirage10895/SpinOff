package fr.eseo.dis.amiaudluc.spinoffapp.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.model.SerieDatabase;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by lucasamiaud on 28/02/2018.
 */

@Getter
@Setter
public class Serie extends SerieDatabase implements Media {
    private static final String INPRODUCTION = "In Production";
    private static final String PLANNED = "Planned";
    private static final String RETURNING = "Returning Series";

    private String backdropPath;
    private ArrayList<Artist> createdBy;
    private List<Integer> episodeRunTime;
    private LocalDate firstAirDate;
    private List<Genre> genres;
    private String homepage;
    private Boolean inProduction;
    private List<Language> languages;
    private LocalDate lastAirDate;
    private Episode lastEpisodeToAir;
    private Episode nextEpisodeToAir;
    private List<Network> networks;
    private Integer numberOfSeasons;
    private List<String> originCountry;
    private Language originalLanguage;
    private String originalName;
    private String overview;
    private Double popularity;
    private List<ProductionCompany> productionCompanies;
    private List<Season> seasons;
    private String status;
    private String type;
    private Double voteAverage;
    private Integer voteCount;
    private String mediaType;

    public Serie(){
        super();
    }

    public SerieDatabase toDataBaseFormat() {
        SerieDatabase serieDatabase = new SerieDatabase();
        serieDatabase.setId(this.getId());
        serieDatabase.setName(this.getName());
        serieDatabase.setPosterPath(this.getPosterPath());
        return serieDatabase;
    }

    @Override
    public String getMediaType() {
        return this.mediaType;
    }
}
