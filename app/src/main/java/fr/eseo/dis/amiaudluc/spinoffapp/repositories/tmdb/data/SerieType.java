package fr.eseo.dis.amiaudluc.spinoffapp.repositories.tmdb.data;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.function.Function;

import fr.eseo.dis.amiaudluc.spinoffapp.utils.DateUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SerieType {
    POPULAR(
            "popular",
            page -> DiscoverFilters.builder()
                    .includeAdult(false)
                    .includeVideo(false)
                    .region("FR")
                    .page(page)
                    .sortBy(DiscoverSortPath.POPULARITY.withSort(DiscoverSort.DESC))
                    .build()
    ),
    TOP_RATED(
            "top_rated",
            page -> DiscoverFilters.builder()
                    .includeAdult(false)
                    .includeVideo(false)
                    .region("FR")
                    .page(page)
                    .sortBy(DiscoverSortPath.VOTE_AVERAGE.withSort(DiscoverSort.DESC))
                    .withoutGenres("99,10755")
                    .voteCountGte(200)
                    .build()
    ),
    ON_AIR(
            "on_the_air",
            page -> DiscoverFilters.builder()
                    .includeAdult(false)
                    .includeVideo(false)
                    .region("FR")
                    .page(page)
                    .sortBy(DiscoverSortPath.POPULARITY.withSort(DiscoverSort.DESC))
                    .withReleaseType("2|3")
                    .airDateGte(
                            LocalDate.now()
                                    .minusWeeks(6)
                                    .with(TemporalAdjusters.nextOrSame(DayOfWeek.WEDNESDAY))
                                    .format(DateUtils.CLASSIC_DATE_FORMATTER)
                    )
                    .airDateLte(
                            LocalDate.now()
                                    .with(TemporalAdjusters.nextOrSame(DayOfWeek.WEDNESDAY))
                                    .format(DateUtils.CLASSIC_DATE_FORMATTER)
                    )
                    .build()
    ),
    ;

    private final String name;
    private final Function<Integer, DiscoverFilters> discoverFilters;
}
