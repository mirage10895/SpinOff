package fr.eseo.dis.amiaudluc.spinoffapp.api.enums;

public enum SerieType {
    POPULAR("popular"),
    TOP_RATED("top_rated"),
    ON_AIR("on_the_air"),
            ;

    String name;

    SerieType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
