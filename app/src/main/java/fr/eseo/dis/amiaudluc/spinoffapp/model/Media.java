package fr.eseo.dis.amiaudluc.spinoffapp.model;

/**
 * Created by lucasamiaud on 02/03/2018.
 */

public interface Media{

    String MOVIE =  "movie";
    String SERIE = "tv";
    String ARTIST= "artist";

    String getMediaType();
    void setMediaType(String mediaType);
}
