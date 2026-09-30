package fr.eseo.dis.amiaudluc.spinoffapp.ui.season;

import java.util.Objects;

import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Season;

/**
 * Wrapper carrying a season and its watched state so the DiffUtil can detect changes.
 */
public class SeasonItem {

    private final Season season;
    private final boolean watched;
    private final boolean controlsEnabled;

    public SeasonItem(Season season, boolean watched, boolean controlsEnabled) {
        this.season = season;
        this.watched = watched;
        this.controlsEnabled = controlsEnabled;
    }

    public Season getSeason() {
        return season;
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
        SeasonItem that = (SeasonItem) o;
        return watched == that.watched
                && controlsEnabled == that.controlsEnabled
                && Objects.equals(season, that.season);
    }

    @Override
    public int hashCode() {
        return Objects.hash(season, watched, controlsEnabled);
    }
}
