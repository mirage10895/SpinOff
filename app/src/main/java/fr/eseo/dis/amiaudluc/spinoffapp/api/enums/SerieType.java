package fr.eseo.dis.amiaudluc.spinoffapp.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SerieType {
    POPULAR("popular"),
    TOP_RATED("top_rated"),
    ON_AIR("on_the_air"),
    ;

    private final String name;
}
