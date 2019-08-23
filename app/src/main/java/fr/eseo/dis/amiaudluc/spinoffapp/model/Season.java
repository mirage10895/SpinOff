package fr.eseo.dis.amiaudluc.spinoffapp.model;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import fr.eseo.dis.amiaudluc.spinoffapp.api.beans.ApiObjectResponse;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.model.SeasonDatabase;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by lucasamiaud on 28/02/2018.
 */

@Getter
@Setter
public class Season extends SeasonDatabase {
    private Date airDate;
    private int episodeCount;
    private List<Episode> episodes;
    private String overview;
    private Credits<Artist> credits;
    private ApiObjectResponse<Video> videos;

    public Season(){
        super();
        this.airDate = new Date();
        this.episodeCount = 0;
    }

    public Episode getFutureEpisode(){
        final long now = System.currentTimeMillis();
        Optional<Episode> episode = this.episodes.stream().filter(episode1 -> episode1.getAirDate() != null
                && episode1.getAirDate().getTime() >= now).findFirst();
        return episode.orElse(new Episode());
    }

    public List<Episode> getOldEpisodes(){
        return this.episodes.stream().filter(s -> s.getAirDate() != null)
                .filter(s -> fr.eseo.dis.amiaudluc.spinoffapp.utils.DateUtils.isDayBefore(s.getAirDate()))
                .collect(Collectors.toList());
    }

    public List<Episode> getFutureEpisodes(){
        return this.episodes.stream()
                .filter(s -> s.getAirDate() != null
                        && !fr.eseo.dis.amiaudluc.spinoffapp.utils.DateUtils.isDayBefore(s.getAirDate()))
                .collect(Collectors.toList());
    }

    public Video getRightVideo(){
        Optional<Video> video = this.videos.getResults().stream().filter(video1 -> video1.getSite().equals("YouTube")
                && video1.getType().equals("Trailer")).findFirst();
        return video.orElse(new Video());
    }
}
