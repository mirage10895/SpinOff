package fr.eseo.dis.amiaudluc.spinoffapp.ui.common.adapter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.FragmentType;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.library.LibraryItem;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.library.LibrarySort;

/**
 * Unit tests of the section grouping of the library list screen: one divider per contiguous run,
 * the raw section key, the tile count, and the "unknown" section pushed to the end.
 */
public class LibraryRowTest {

    @Test
    public void group_alphabetical_createsOneHeaderPerLetter() {
        List<LibraryRow> rows = rows(LibrarySort.ALPHABETICAL,
                item(1, "Alien"), item(2, "Aliens"), item(3, "Zodiac"));

        List<LibraryRow.SectionHeader> headers = headers(rows);
        assertEquals(2, headers.size());
        assertEquals("A", headers.get(0).value());
        assertEquals(2, headers.get(0).count());
        assertEquals("Z", headers.get(1).value());
        assertEquals(1, headers.get(1).count());
        assertEquals(5, rows.size());
    }

    @Test
    public void group_releaseYear_usesTheYearAsSectionAndKeepsUnknownLast() {
        List<LibraryRow> rows = rows(LibrarySort.RELEASE_YEAR,
                item(1, "New", 2024, null, null),
                item(2, "Old", 1979, null, null),
                item(3, "Unknown", null, null, null));

        assertEquals(Arrays.asList("2024", "1979", null), values(headers(rows)));
    }

    @Test
    public void group_genre_usesTheFirstGenreAsSection() {
        List<LibraryRow> rows = rows(LibrarySort.GENRE,
                item(1, "Drama", null, "Drama,Thriller", null),
                item(2, "No Genre", null, null, null));

        assertEquals(Arrays.asList("Drama", null), values(headers(rows)));
    }

    @Test
    public void group_rating_usesTheWholePointBucketAsSection() {
        List<LibraryRow> rows = rows(LibrarySort.RATING,
                item(1, "Nine", null, null, 9.4),
                item(2, "Eight", null, null, 8.2),
                item(3, "Unrated", null, null, null));

        assertEquals(Arrays.asList("9", "8", null), values(headers(rows)));
    }

    @Test
    public void diffCallback_sameSectionWithAnotherCountKeepsTheItemButChangesTheContent() {
        LibraryRow.SectionHeader twoTiles =
                new LibraryRow.SectionHeader(LibrarySort.ALPHABETICAL, "A", 2);
        LibraryRow.SectionHeader threeTiles =
                new LibraryRow.SectionHeader(LibrarySort.ALPHABETICAL, "A", 3);

        assertTrue(LibraryRow.DIFF_CALLBACK.areItemsTheSame(twoTiles, threeTiles));
        assertFalse(LibraryRow.DIFF_CALLBACK.areContentsTheSame(twoTiles, threeTiles));
    }

    @Test
    public void diffCallback_sameSectionKeyUnderAnotherSortIsAnotherItem() {
        LibraryRow.SectionHeader alphabetical =
                new LibraryRow.SectionHeader(LibrarySort.ALPHABETICAL, "2024", 2);
        LibraryRow.SectionHeader byYear =
                new LibraryRow.SectionHeader(LibrarySort.RELEASE_YEAR, "2024", 2);

        assertFalse(LibraryRow.DIFF_CALLBACK.areItemsTheSame(alphabetical, byYear));
    }

    private static List<LibraryRow> rows(LibrarySort sort, LibraryItem... items) {
        List<LibraryItem> sorted = new ArrayList<>(List.of(items));
        sort.sort(sorted);
        return LibraryRow.group(sorted, sort);
    }

    private static List<LibraryRow.SectionHeader> headers(List<LibraryRow> rows) {
        return rows.stream()
                .filter(LibraryRow.SectionHeader.class::isInstance)
                .map(LibraryRow.SectionHeader.class::cast)
                .toList();
    }

    private static List<String> values(List<LibraryRow.SectionHeader> headers) {
        return headers.stream().map(LibraryRow.SectionHeader::value).toList();
    }

    private static LibraryItem item(int id, String title) {
        return item(id, title, null, null, null);
    }

    private static LibraryItem item(
            int id, String title, Integer year, String genres, Double rating) {
        return new LibraryItem(
                id, title, null, FragmentType.MOVIE, null, rating, year, null, genres,
                null, 0, 0, false);
    }
}
