package fr.eseo.dis.amiaudluc.spinoffapp.ui.episode;

import java.util.Objects;

import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Episode;

/**
 * Wrapper carrying an episode and its watched state so the DiffUtil can detect changes.
 */
public class EpisodeItem {

    private final Episode episode;
    private final boolean watched;
    private final boolean controlsEnabled;

    public EpisodeItem(Episode episode, boolean watched, boolean controlsEnabled) {
        this.episode = episode;
        this.watched = watched;
        this.controlsEnabled = controlsEnabled;
    }

    public Episode getEpisode() {
        return episode;
    }

    public boolean isWatched() {
        return watched;
    }

    public boolean isControlsEnabled() {
        return controlsEnabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EpisodeItem that = (EpisodeItem) o;
        return watched == that.watched
                && controlsEnabled == that.controlsEnabled
                && Objects.equals(episode, that.episode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(episode, watched, controlsEnabled);
    }
}
