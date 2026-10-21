package fr.eseo.dis.amiaudluc.spinoffapp.ui.common;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import fr.eseo.dis.amiaudluc.R;

/**
 * Text helpers shared by the media tiles, so the library, season and episode rows build their
 * metadata lines the same way.
 */
public final class MediaTileText {

    private MediaTileText() {
    }

    /** Joins the non blank parts with the tile separator, skipping the missing ones. */
    public static String join(Context context, String... parts) {
        List<String> kept = new ArrayList<>();
        for (String part : parts) {
            if (part != null && !part.isBlank()) {
                kept.add(part);
            }
        }
        return String.join(context.getString(R.string.media_tile_separator), kept);
    }
}
