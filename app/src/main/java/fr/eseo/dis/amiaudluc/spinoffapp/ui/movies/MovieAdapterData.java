package fr.eseo.dis.amiaudluc.spinoffapp.ui.movies;

import lombok.Value;

@Value(staticConstructor = "of")
public class MovieAdapterData {
    int id;
    String posterPath;
}
