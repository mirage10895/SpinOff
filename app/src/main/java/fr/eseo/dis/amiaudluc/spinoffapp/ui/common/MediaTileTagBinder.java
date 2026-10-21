package fr.eseo.dis.amiaudluc.spinoffapp.ui.common;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import java.util.Locale;

import fr.eseo.dis.amiaudluc.R;

/**
 * Binds the rating tag shared by the media tiles, so every tile formats the TMDB rating the same
 * way and hides the tag when the value is missing.
 */
public final class MediaTileTagBinder {

    private MediaTileTagBinder() {
    }

    /**
     * Shows the rating tag when the value is usable, hides it otherwise.
     *
     * @return whether the tag is visible, so the caller can hide an empty tags row
     */
    public static boolean bindRating(Context context, TextView rating, Double voteAverage) {
        boolean hasRating = voteAverage != null && voteAverage > 0;
        rating.setVisibility(hasRating ? View.VISIBLE : View.GONE);
        if (hasRating) {
            rating.setText(context.getString(R.string.media_tile_rating,
                    String.format(Locale.getDefault(), "%.1f", voteAverage)));
        }
        return hasRating;
    }
}
