package fr.eseo.dis.amiaudluc.spinoffapp.ui.common;

/**
 * Callback used by adapters to toggle the "watched" state of a season or an episode.
 */
public interface WatchedToggleListener {
    void onWatchedToggle(int id, boolean watched);
}
