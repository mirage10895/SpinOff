package fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.library;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.FragmentType;

/**
 * Unit tests of the ordering and sectioning modes of the library list screen: section order,
 * title order inside a section, and the placement of the missing values in the "unknown" section.
 */
public class LibrarySortTest {

    @Test
    public void alphabetical_sectionsFollowTheLettersWithTheOtherBucketLast() {
        List<LibraryItem> items = sorted(LibrarySort.ALPHABETICAL,
                item(1, "Zodiac", null, null, null),
                item(2, "Les Misérables", null, null, null),
                item(3, "12 Angry Men", null, null, null),
                item(4, "Alien", null, null, null));

        assertEquals(List.of("Alien", "Les Misérables", "Zodiac", "12 Angry Men"), titles(items));
        assertEquals(List.of("A", "L", "Z", "#"), sections(LibrarySort.ALPHABETICAL, items));
    }

    @Test
    public void releaseYear_ordersMostRecentFirstAndPutsMissingYearsLast() {
        List<LibraryItem> items = sorted(LibrarySort.RELEASE_YEAR,
                item(1, "Old", 1979, null, null),
                item(2, "Unknown", null, null, null),
                item(3, "New", 2024, null, null),
                item(4, "Middle", 2000, null, null));

        assertEquals(List.of("New", "Middle", "Old", "Unknown"), titles(items));
        assertEquals(Arrays.asList("2024", "2000", "1979", null),
                sections(LibrarySort.RELEASE_YEAR, items));
    }

    @Test
    public void genre_ordersTheFirstGenreAlphabeticallyAndPutsMissingGenresLast() {
        List<LibraryItem> items = sorted(LibrarySort.GENRE,
                item(1, "War Movie", null, "War", null),
                item(2, "No Genre", null, null, null),
                item(3, "Drama Movie", null, "Drama,Thriller", null),
                item(4, "Comedy Movie", null, "Comedy", null));

        assertEquals(List.of("Comedy Movie", "Drama Movie", "War Movie", "No Genre"), titles(items));
        assertEquals(Arrays.asList("Comedy", "Drama", "War", null),
                sections(LibrarySort.GENRE, items));
    }

    @Test
    public void rating_ordersByWholePointBucketAndPutsMissingRatingsLast() {
        List<LibraryItem> items = sorted(LibrarySort.RATING,
                item(1, "Nine B", null, null, 9.2),
                item(2, "Unrated", null, null, null),
                item(3, "Nine A", null, null, 9.8),
                item(4, "Eight", null, null, 8.4));

        // Same bucket: titles stay alphabetical.
        assertEquals(List.of("Nine A", "Nine B", "Eight", "Unrated"), titles(items));
        assertEquals(Arrays.asList("9", "9", "8", null), sections(LibrarySort.RATING, items));
    }

    private static LibraryItem item(
            int id, String title, Integer year, String genres, Double rating) {
        return new LibraryItem(
                id, title, null, FragmentType.MOVIE, null, rating, year, null, genres,
                null, 0, 0, false);
    }

    private static List<LibraryItem> sorted(LibrarySort sort, LibraryItem... items) {
        List<LibraryItem> list = new ArrayList<>(List.of(items));
        sort.sort(list);
        return list;
    }

    private static List<String> titles(List<LibraryItem> items) {
        return items.stream().map(LibraryItem::title).toList();
    }

    private static List<String> sections(LibrarySort sort, List<LibraryItem> items) {
        return items.stream().map(sort::sectionValue).toList();
    }
}
