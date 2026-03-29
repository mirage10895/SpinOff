package fr.eseo.dis.amiaudluc.spinoffapp.ui.series;

import lombok.Value;

@Value(staticConstructor = "of")
public class SerieAdapterData {
    int id;
    String posterPath;
    boolean inLibrary;
    boolean watched;

    public static SerieAdapterData of(int id, String posterPath) {
        return new SerieAdapterData(id, posterPath, false, false);
    }
}
