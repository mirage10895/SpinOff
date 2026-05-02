package fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.discovery.beans;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.function.Supplier;

import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.DiscoverFilters;
import fr.eseo.dis.amiaudluc.spinoffapp.utils.DateUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MovieType {
    POPULAR(
            "popular",
            () -> DiscoverFilters.builder()
                    .includeAdult(false)
                    .includeVideo(false)
                    .region("FR")
                    .sortBy(DiscoverSortPath.POPULARITY.withSort(DiscoverSort.DESC))
    ),
    TOP_RATED(
            "top_rated",
            () -> DiscoverFilters.builder()
                    .includeAdult(false)
                    .includeVideo(false)
                    .region("FR")
                    .sortBy(DiscoverSortPath.VOTE_AVERAGE.withSort(DiscoverSort.DESC))
                    .withoutGenres("99,10755")
                    .voteCountGte(200)
    ),
    ON_AIR(
            "now_playing",
            () -> DiscoverFilters.builder()
                    .includeAdult(false)
                    .includeVideo(false)
                    .region("FR")
                    .sortBy(DiscoverSortPath.POPULARITY.withSort(DiscoverSort.DESC))
                    .withReleaseType("2|3")
                    .releaseDateGte(
                            LocalDate.now()
                                    .minusWeeks(6)
                                    .with(TemporalAdjusters.nextOrSame(DayOfWeek.WEDNESDAY))
                                    .format(DateUtils.CLASSIC_DATE_FORMATTER)
                    )
                    .releaseDateLte(
                            LocalDate.now()
                                    .with(TemporalAdjusters.nextOrSame(DayOfWeek.WEDNESDAY))
                                    .format(DateUtils.CLASSIC_DATE_FORMATTER)
                    )
    ),
    ;

    private final String name;
    private final Supplier<DiscoverFilters.DiscoverFiltersBuilder> discoverFilters;
}
