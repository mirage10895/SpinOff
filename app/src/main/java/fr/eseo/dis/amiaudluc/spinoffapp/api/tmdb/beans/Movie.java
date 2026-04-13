package fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.adapter.AdapterData;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.FragmentType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by lucasamiaud on 28/02/2018.
 */

@Getter
@Setter
@NoArgsConstructor
public class Movie implements Media {
    private String title;
    private String posterPath;
    private Integer runtime;
    private Boolean adult;
    private String backdropPath;
    private Long budget;
    private List<Genre> genres;
    private String homepage;
    private Integer id;
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
    private Credits<Artist> credits;
    private ApiObjectResponse<Video> videos;
    private ApiObjectResponse<Movie> recommendations;

    public List<Artist> getDirectors() {
        return this.credits.getCrew()
                .stream()
                .filter(artist -> "Director".equals(artist.getJob()))
                .collect(Collectors.toList());
    }

    public AdapterData toAdapterFormat() {
        return new AdapterData(
                id,
                originalTitle,
                posterPath,
                false,
                false,
                FragmentType.MOVIE
        );
    }

    @Override
    public String getMediaType() {
        return Media.MOVIE;
    }
}
