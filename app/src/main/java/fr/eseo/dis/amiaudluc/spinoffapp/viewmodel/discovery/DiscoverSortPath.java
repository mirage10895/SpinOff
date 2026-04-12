package fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.discovery;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DiscoverSortPath {
    ORIGINAL_TILE("original_title"),
    POPULARITY("popularity"),
    RELEASE_DATE("primary_release_date"),
    REVENUE("revenue"),
    VOTE_AVERAGE("vote_average"),
    VOTE_COUNT("vote_count")
    ;

    private final String sortPath;

    public String withSort(DiscoverSort sort) {
        return sortPath + (sort == DiscoverSort.ASC ? ".asc" : ".desc");
    }
}
