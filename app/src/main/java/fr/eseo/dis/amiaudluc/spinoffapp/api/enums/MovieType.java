package fr.eseo.dis.amiaudluc.spinoffapp.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MovieType {
    POPULAR("popular"),
    TOP_RATED("top_rated"),
    ON_AIR("now_playing"),
    ;

    private final String name;
}
