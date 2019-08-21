package fr.eseo.dis.amiaudluc.spinoffapp.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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
    private Date releaseDate;
    private Long revenue;
    private Integer runtime;
    private List<SpokenLanguage> spokenLanguages;
    private String status;
    private String tagline;
    private Boolean video;
    private Double voteAverage;
    private Integer voteCount;
    private String mediaType;
    private Credits<Artist> credits;
    private ApiObjectResponse<Video> videos;

    public Movie(){
        super();
    }

    public List<Artist> getDirectors() {
        List<Artist> directors = new ArrayList<>();
        this.credits.getCrew().forEach(artist -> {
            if (artist.getJob().equals("Director")){
                directors.add(artist);
            }
        });
        return directors;
    }

    public Video getRightVideo(){
        Optional<Video> video = this.videos.getResults().stream().filter(video1 -> video1.getSite().equals("YouTube")
                && video1.getType().equals("Trailer")).findFirst();
        return video.orElse(new Video());
    }

    public MovieDatabase toDatabaseFormat() {
        MovieDatabase movieDatabase = new MovieDatabase();
        movieDatabase.setId(this.getId());
        movieDatabase.setTitle(this.getTitle());
        movieDatabase.setPosterPath(this.getPosterPath());
        return movieDatabase;
    }

    @Override
    public String getMediaType() {
        return this.mediaType;
    }
}
