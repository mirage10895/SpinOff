package fr.eseo.dis.amiaudluc.spinoffapp.ui.common;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

/**
 * Helper class to manage YouTube player lifecycle and video loading.
 */
public class YoutubeConnector {

    private YouTubePlayer youTubePlayer;
    private String currentVideoId;

    public YoutubeConnector(@NonNull YouTubePlayerView playerView, @NonNull Lifecycle lifecycle) {
        lifecycle.addObserver(playerView);
        playerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer initializedYouTubePlayer) {
                youTubePlayer = initializedYouTubePlayer;
                if (currentVideoId != null) {
                    youTubePlayer.cueVideo(currentVideoId, 0);
                }
            }
        });
    }

    /**
     * Cues a video by its ID. If the player is not ready yet, it will be cued as soon as it is.
     * @param videoId The YouTube video ID.
     */
    public void loadVideo(@Nullable String videoId) {
        if (videoId != null && !videoId.equals(currentVideoId)) {
            if (youTubePlayer != null) {
                youTubePlayer.cueVideo(videoId, 0);
            }
        }
        currentVideoId = videoId;
    }
}
