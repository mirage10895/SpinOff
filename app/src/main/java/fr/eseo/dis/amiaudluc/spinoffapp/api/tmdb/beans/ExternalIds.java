package fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans;

import com.google.gson.annotations.SerializedName;

public record ExternalIds(
        @SerializedName("imdb_id")
        String imdbId
) {
}
