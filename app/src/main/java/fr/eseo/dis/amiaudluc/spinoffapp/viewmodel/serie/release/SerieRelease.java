package fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.serie.release;

import java.time.LocalDate;

/**
 * A newly released episode or season of a serie kept in the library.
 * <p>
 * A single release is emitted per serie and per synchronisation: when an episode starts a brand
 * new season, only the season flavour is kept, because it already covers the episode.
 *
 * @param serieId       TMDB identifier of the serie
 * @param serieName     displayed name of the serie
 * @param posterPath    poster path of the serie, used by the notification
 * @param kind          whether a whole season or a single episode is released
 * @param seasonNumber  released season number
 * @param episodeNumber released episode number, {@code null} for a season release
 * @param episodeName   released episode name, {@code null} for a season release
 * @param airDate       release date, never in the future
 */
public record SerieRelease(
        int serieId,
        String serieName,
        String posterPath,
        Kind kind,
        int seasonNumber,
        Integer episodeNumber,
        String episodeName,
        LocalDate airDate
) {

    /**
     * Nature of the release.
     */
    public enum Kind {
        SEASON,
        EPISODE
    }

    public static SerieRelease season(int serieId, String serieName, String posterPath,
                                      int seasonNumber, LocalDate airDate) {
        return new SerieRelease(serieId, serieName, posterPath, Kind.SEASON,
                seasonNumber, null, null, airDate);
    }

    public static SerieRelease episode(int serieId, String serieName, String posterPath,
                                       int seasonNumber, int episodeNumber, String episodeName,
                                       LocalDate airDate) {
        return new SerieRelease(serieId, serieName, posterPath, Kind.EPISODE,
                seasonNumber, episodeNumber, episodeName, airDate);
    }

    public boolean isSeason() {
        return kind == Kind.SEASON;
    }
}
