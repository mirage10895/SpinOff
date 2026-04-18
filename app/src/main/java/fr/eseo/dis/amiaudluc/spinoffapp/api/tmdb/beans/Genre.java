package fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by lucasamiaud on 28/02/2018.
 */

@Getter
@Setter
@NoArgsConstructor
public class Genre {
    private int id;
    private String name;

    @Getter
    @Setter
    public static class GenreListResponse {
        private List<Genre> genres;
    }
}
