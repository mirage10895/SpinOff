package fr.eseo.dis.amiaudluc.spinoffapp.api.enums;

public enum MovieType {
    POPULAR("popular"),
    TOP_RATED("top_rated"),
    ON_AIR("now_playing"),
    ;

    String name;

    MovieType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
