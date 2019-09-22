package fr.eseo.dis.amiaudluc.spinoffapp.common;

/**
 * Created by lucasamiaud on 06/03/2018.
 */

public interface SearchInterface extends ItemInterface {

    void setType(FragmentType type);

    enum FragmentType {
        MOVIE("movie"),
        SERIE("tv"),
        ARTIST("person"),
        ACTOR( "actor"),
        NETWORK( "network"),
        SEASON("season"),
        EPISODE("episode"),
        EVENT("event"),
        DEFAULT("");

        String name;

        FragmentType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public static FragmentType getValueOf(String name) {
            for (int i = 0; i < FragmentType.values().length; i++) {
                if (name.equals(FragmentType.values()[i].name)) {
                    return FragmentType.values()[i];
                }
            }
            return FragmentType.DEFAULT;
        }
    }
}
