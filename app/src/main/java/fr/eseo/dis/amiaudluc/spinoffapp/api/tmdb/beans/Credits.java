package fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Credits<T> {
    private List<Artist> crew;
    private List<T> cast;
}
