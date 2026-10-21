package fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.library;

import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.FragmentType;

/**
 * Immutable description of a single tile of the watched list screen.
 * <p>
 * It only carries what the local library actually stores: a watched date, a watch count or a
 * media format are not part of the model, so they are not exposed here. Built by
 * {@link WatchedListBuilder} for that screen only, independently from the statistics
 * aggregation that feeds the library dashboards.
 *
 * @param id              local identifier of the movie or serie
 * @param title           original title, used as-is for display and alphabetical grouping
 * @param posterPath      TMDB poster path, may be {@code null}
 * @param fragmentType    {@link FragmentType#MOVIE} or {@link FragmentType#SERIE}
 * @param runtimeMinutes  movie runtime in minutes, {@code null} for series
 * @param voteAverage     TMDB rating, may be {@code null}
 * @param releaseYear     release year for a movie, first air year for a serie, may be {@code null}
 * @param creators        first credited director or creator, may be {@code null}
 * @param genres          comma separated genres, may be {@code null}
 * @param seasonCount     number of seasons, {@code null} for movies
 * @param watchedEpisodes watched episode count, series only
 * @param totalEpisodes   total episode count, series only
 * @param fullyWatched    {@code true} when nothing is left to watch
 */
public record WatchedMediaItem(
        int id,
        String title,
        String posterPath,
        FragmentType fragmentType,
        Integer runtimeMinutes,
        Double voteAverage,
        Integer releaseYear,
        String creators,
        String genres,
        Integer seasonCount,
        int watchedEpisodes,
        int totalEpisodes,
        boolean fullyWatched
) {

    public boolean isSerie() {
        return fragmentType == FragmentType.SERIE;
    }

    /** Whether the episode progress can be rendered. */
    public boolean hasEpisodeProgress() {
        return isSerie() && totalEpisodes > 0;
    }

    /** Watched episodes ratio as a whole percentage, {@code 0} when the total is unknown. */
    public int progressPercent() {
        if (totalEpisodes <= 0) {
            return 0;
        }
        return (int) Math.round(watchedEpisodes * 100.0 / totalEpisodes);
    }
}
