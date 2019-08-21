package fr.eseo.dis.amiaudluc.spinoffapp.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Credits<T> {
    private List<Artist> crew;
    private List<T> cast;
}
