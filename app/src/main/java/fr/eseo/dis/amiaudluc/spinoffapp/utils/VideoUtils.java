package fr.eseo.dis.amiaudluc.spinoffapp.utils;

import java.util.Comparator;

import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.ApiObjectResponse;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Video;

public class VideoUtils {
    private VideoUtils() {
        // Utility class
    }

    public static Video getYoutubeTrailer(ApiObjectResponse<Video> videos) {
        if (videos == null || videos.getResults() == null) {
            return null;
        }
        return videos.getResults().stream()
                .filter(video -> "YouTube".equals(video.getSite())
                        && "Trailer".equals(video.getType()))
                .max(Comparator.comparing(Video::getSize))
                .orElse(null);
    }
}
