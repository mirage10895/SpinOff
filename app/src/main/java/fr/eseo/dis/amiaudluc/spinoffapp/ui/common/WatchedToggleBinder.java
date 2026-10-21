package fr.eseo.dis.amiaudluc.spinoffapp.ui.common;

import android.view.View;
import android.widget.ImageView;

import fr.eseo.dis.amiaudluc.R;

/**
 * Drives the watched toggle shared by the season and episode tiles.
 * <p>
 * The control is hidden while the serie is not in the library, and otherwise shows the watched
 * state and relays the tap. Keeping it here means both tiles behave identically.
 */
public final class WatchedToggleBinder {

    private WatchedToggleBinder() {
    }

    /**
     * @param container       the touch target wrapping the badge
     * @param icon            the badge whose drawable reflects the state
     * @param controlsEnabled whether the toggle applies at all
     * @param watched         current state
     * @param onToggle        invoked with the new state when the user taps
     */
    public static void bind(
            View container,
            ImageView icon,
            boolean controlsEnabled,
            boolean watched,
            Runnable onToggle
    ) {
        if (!controlsEnabled) {
            container.setVisibility(View.GONE);
            container.setOnClickListener(null);
            return;
        }

        container.setVisibility(View.VISIBLE);
        container.setContentDescription(container.getContext().getString(
                watched ? R.string.chip_watched : R.string.media_tile_not_watched));
        icon.setImageResource(watched ? R.drawable.ic_watched : R.drawable.ic_unwatched);
        container.setOnClickListener(view -> onToggle.run());
    }
}
