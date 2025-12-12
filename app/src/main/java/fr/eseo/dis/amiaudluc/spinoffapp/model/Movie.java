package fr.eseo.dis.amiaudluc.spinoffapp.model;

import android.support.annotation.Nullable;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import fr.eseo.dis.amiaudluc.spinoffapp.api.beans.ApiObjectResponse;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.model.MovieDatabase;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by lucasamiaud on 28/02/2018.
 */

@Getter
@Setter
public class Movie extends MovieDatabase implements Media {
    private Boolean adult;
    private String backdropPath;
    private Long budget;
    private List<Genre> genres;
    private String homepage;
    private String imdbId;
    private Language originalLanguage;
    private String originalTitle;
    private String overview;
    private Double popularity;
    private List<ProductionCompany> productionCompanies;
    private List<ProductionCountry> productionCountry;
    private LocalDate releaseDate;
    private Long revenue;
    private List<SpokenLanguage> spokenLanguages;
    private String status;
    private String tagline;
    private Boolean video;
    private Double voteAverage;
    private Integer voteCount;
    private String mediaType;
    private Credits<Artist> credits;
    private ApiObjectResponse<Video> videos;
    private ApiObjectResponse<Movie> recommendations;

    public Movie() {
        super();
    }

    public List<Artist> getDirectors() {
        return this.credits.getCrew()
                .stream()
                .filter(artist -> "Director".equals(artist.getJob()))
                .collect(Collectors.toList());
    }

    @Nullable
    public Video getRightVideo() {
        return this.videos.getResults().stream()
                .filter(video -> "YouTube".equals(video.getSite())
                        && "Trailer".equals(video.getType()))
                .findFirst()
                .orElse(null);
    }

    public MovieDatabase toDatabaseFormat() {
        MovieDatabase movieDatabase = new MovieDatabase();
        movieDatabase.setId(this.getId());
        movieDatabase.setTitle(this.getTitle());
        movieDatabase.setPosterPath(this.getPosterPath());
        movieDatabase.setRuntime(this.getRuntime());
        return movieDatabase;
    }

    @Override
    public String getMediaType() {
        return this.mediaType;
    }
}
