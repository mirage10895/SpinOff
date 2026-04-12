package fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans;

import java.time.LocalDate;
import java.util.List;

import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.adapter.AdapterData;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.FragmentType;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by lucasamiaud on 28/02/2018.
 */

@Getter
@Setter
public class Serie implements Media {
    private Integer id;
    private String name;
    private String backdropPath;
    private String posterPath;
    private Integer numberOfEpisodes;
    private List<Artist> createdBy;
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
    private ApiObjectResponse<Video> videos;
    private ApiObjectResponse<Movie> recommendations;

    public Serie() {
        super();
    }

    public AdapterData toAdapterFormat() {
        return new AdapterData(
                this.id,
                this.originalName,
                this.posterPath,
                FragmentType.SERIE
        );
    }

    public static int computeTotalRuntime(Serie serie) {
        if (serie.episodeRunTime != null && !serie.episodeRunTime.isEmpty()) {
            return serie.numberOfEpisodes * (int) serie.episodeRunTime.stream().mapToInt(i -> i).average().orElse(0);
        }
        return 0;
    }

    @Override
    public String getMediaType() {
        return Media.SERIE;
    }
}
