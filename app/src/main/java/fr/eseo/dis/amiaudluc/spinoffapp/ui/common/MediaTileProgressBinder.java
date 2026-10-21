package fr.eseo.dis.amiaudluc.spinoffapp.ui.common;

import android.view.View;

import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.databinding.ViewMediaTileProgressBinding;

/**
 * Drives the episode progress block shared by the media tiles.
 * <p>
 * It owns the percentage computation so the caption, the percentage and the bar always agree.
 */
public final class MediaTileProgressBinder {

    private MediaTileProgressBinder() {
    }

    public static void bind(
            ViewMediaTileProgressBinding binding,
            boolean visible,
            int watchedEpisodes,
            int totalEpisodes
    ) {
        binding.getRoot().setVisibility(visible ? View.VISIBLE : View.GONE);
        if (!visible) {
            return;
        }

        binding.episodesProgress.setText(binding.getRoot().getContext().getString(
                R.string.media_tile_episodes_progress, watchedEpisodes, totalEpisodes));
        binding.percent.setText(binding.getRoot().getContext().getString(
                R.string.media_tile_percent, percent(watchedEpisodes, totalEpisodes)));
        binding.progress.setMax(totalEpisodes);
        binding.progress.setProgress(watchedEpisodes);
    }

    /** Watched episodes ratio as a whole percentage, {@code 0} when the total is unknown. */
    public static int percent(int watchedEpisodes, int totalEpisodes) {
        if (totalEpisodes <= 0) {
            return 0;
        }
        return (int) Math.round(watchedEpisodes * 100.0 / totalEpisodes);
    }
}
