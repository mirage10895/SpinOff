package fr.eseo.dis.amiaudluc.spinoffapp.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Credits {
    private List<Artist> crew;
    private List<Artist> cast;
}
