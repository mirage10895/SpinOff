package fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.library;

import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.FragmentType;

/**
 * Immutable description of a single tile of the library list screen, whatever the list it
 * belongs to.
 * <p>
 * It only carries what the local library actually stores: a watched date, a watch count or a
 * media format are not part of the model, so they are not exposed here. Built by
 * {@link LibraryBuilder} for that screen only, independently from the statistics
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
public record LibraryItem(
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

    /**
     * Whether the tile has an episode progress to render. A serie that was never started has a
     * known episode total but nothing watched, and shows no progress bar.
     */
    public boolean showsEpisodeProgress() {
        return isSerie() && totalEpisodes > 0 && watchedEpisodes > 0;
    }

    /** Whether the serie was started but is not finished, so the "in progress" badge applies. */
    public boolean isInProgress() {
        return showsEpisodeProgress() && !fullyWatched;
    }

    /**
     * Whether anything of this media has been watched. It drives which context menu entries make
     * sense: there is nothing to unmark on a title that was never watched, and nothing left to
     * mark as seen on a fully watched one.
     */
    public boolean hasWatchedContent() {
        return fullyWatched || watchedEpisodes > 0;
    }
}
