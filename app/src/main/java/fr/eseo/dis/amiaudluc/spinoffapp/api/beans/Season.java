package fr.eseo.dis.amiaudluc.spinoffapp.api.beans;

import java.time.LocalDate;
import java.util.List;

import androidx.annotation.Nullable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by lucasamiaud on 28/02/2018.
 */

@Getter
@Setter
@NoArgsConstructor
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

    public static int computeEpisodesAverageRuntime(Season season) {
        if (season.episodes != null && !season.episodes.isEmpty()) {
            return (int) season.episodes.stream().mapToInt(Episode::getRuntime).average().orElse(0);
        }
        return 0;
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
