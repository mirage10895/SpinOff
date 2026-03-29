package fr.eseo.dis.amiaudluc.spinoffapp.ui.movies;

import lombok.Value;

@Value(staticConstructor = "of")
public class MovieAdapterData {
    int id;
    String posterPath;
    boolean inLibrary;
    boolean watched;

    public static MovieAdapterData of(int id, String posterPath) {
        return new MovieAdapterData(id, posterPath, false, false);
    }
}
