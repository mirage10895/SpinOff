package fr.eseo.dis.amiaudluc.spinoffapp.model;

import android.support.annotation.Nullable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import fr.eseo.dis.amiaudluc.spinoffapp.api.beans.ApiObjectResponse;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by lucasamiaud on 28/02/2018.
 */

@Getter
@Setter
public class Season {
    private Integer id;
    private String name;
    private Integer seasonNumber;
    private String posterPath;
    private Integer serieId;
    private LocalDate airDate;
    private int episodeCount;
    private List<Episode> episodes;
    private String overview;
    private Credits<Artist> credits;
    private ApiObjectResponse<Video> videos;

    public Season() {
        super();
        this.airDate = LocalDate.now();
        this.episodeCount = 0;
    }

    public Episode getFutureEpisode() {
        Optional<Episode> episode = this.episodes.stream()
                .filter(episode1 -> episode1.getAirDate() != null
                        && episode1.getAirDate().isAfter(LocalDate.now())
                )
                .findFirst();
        return episode.orElse(new Episode());
    }

    public List<Episode> getOldEpisodes() {
        return this.episodes.stream()
                .filter(s -> s.getAirDate() != null)
                .filter(s -> LocalDate.now().isAfter(s.getAirDate()))
                .collect(Collectors.toList());
    }

    public List<Episode> getFutureEpisodes() {
        return this.episodes.stream()
                .filter(s -> s.getAirDate() != null
                        && LocalDate.now().isBefore(s.getAirDate())
                )
                .collect(Collectors.toList());
    }

    @Nullable
    public Video getRightVideo() {
        return this.videos.getResults().stream()
                .filter(video1 -> "YouTube".equals(video1.getSite())
                        && "Trailer".equals(video1.getType()))
                .findFirst()
                .orElse(null);
    }
}
