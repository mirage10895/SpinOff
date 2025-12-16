package fr.eseo.dis.amiaudluc.spinoffapp.api.beans;

/**
 * Created by lucasamiaud on 02/03/2018.
 */

public interface Media {
    String MOVIE = "movie";
    String SERIE = "tv";
    String ARTIST = "person";

    String getMediaType();
}
