package fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.library;

import java.util.Comparator;
import java.util.List;

import fr.eseo.dis.amiaudluc.spinoffapp.utils.AlphabetUtils;

/**
 * Ordering and sectioning modes of the library list screen.
 * <p>
 * Pure presentation logic: it only reads the display fields carried by {@link LibraryItem}, so it
 * runs on any thread and is unit tested in isolation. The section labels themselves are resolved
 * by the UI layer, because the ViewModel has no {@code Context}: {@link #sectionValue} returns the
 * raw key (a letter, a year, a genre or a rating bucket) and {@code null} for the "unknown"
 * section, which the UI always pushes to the end.
 * <p>
 * A section is always a contiguous run: the comparator orders by section first, then
 * alphabetically inside the section, so the UI can group the list with a single pass.
 */
public enum LibrarySort {

    /** Alphabetical sections, from A to Z, with the non-latin bucket last. */
    ALPHABETICAL,
    /** Release (or first air) year, most recent first. */
    RELEASE_YEAR,
    /** First displayed genre, from A to Z. */
    GENRE,
    /** Whole-point rating bucket, best rated first. */
    RATING;

    /** Orders the list in place, sections first and titles alphabetically inside a section. */
    public void sort(List<LibraryItem> items) {
        items.sort(comparator());
    }

    private Comparator<LibraryItem> comparator() {
        return (left, right) -> {
            int sections = compareSections(left, right);
            return sections != 0
                    ? sections
                    : AlphabetUtils.compareTitles(left.title(), right.title());
        };
    }

    /**
     * Raw key of the section an item belongs to, or {@code null} when the sort field is missing.
     * The UI is responsible for turning that key into a localized label.
     */
    public String sectionValue(LibraryItem item) {
        switch (this) {
            case ALPHABETICAL:
                return String.valueOf(AlphabetUtils.letterOf(item.title()));
            case RELEASE_YEAR:
                return item.releaseYear() != null ? String.valueOf(item.releaseYear()) : null;
            case GENRE:
                return firstGenre(item.genres());
            case RATING:
                Integer bucket = ratingBucket(item.voteAverage());
                return bucket != null ? String.valueOf(bucket) : null;
            default:
                return null;
        }
    }

    private int compareSections(LibraryItem left, LibraryItem right) {
        switch (this) {
            case ALPHABETICAL:
                return compareLetters(left.title(), right.title());
            case RELEASE_YEAR:
                return compareDescending(left.releaseYear(), right.releaseYear());
            case GENRE:
                return AlphabetUtils.compareTitles(
                        firstGenre(left.genres()), firstGenre(right.genres()));
            case RATING:
                return compareDescending(
                        ratingBucket(left.voteAverage()), ratingBucket(right.voteAverage()));
            default:
                return 0;
        }
    }

    /** Compares two section letters, keeping the non-latin bucket ({@code #}) at the end. */
    private static int compareLetters(String leftTitle, String rightTitle) {
        char leftLetter = AlphabetUtils.letterOf(leftTitle);
        char rightLetter = AlphabetUtils.letterOf(rightTitle);
        boolean leftOther = leftLetter == AlphabetUtils.OTHER;
        boolean rightOther = rightLetter == AlphabetUtils.OTHER;
        if (leftOther != rightOther) {
            return leftOther ? 1 : -1;
        }
        return Character.compare(leftLetter, rightLetter);
    }

    /** Descending comparison with the missing values pushed to the end. */
    private static int compareDescending(Integer left, Integer right) {
        if (left == null || right == null) {
            if (left == null && right == null) {
                return 0;
            }
            return left == null ? 1 : -1;
        }
        return Integer.compare(right, left);
    }

    /** Whole-point bucket of a rating, so 9.0 to 9.9 share the same section. */
    private static Integer ratingBucket(Double voteAverage) {
        return voteAverage != null ? (int) Math.floor(voteAverage) : null;
    }

    /**
     * The stored genres are comma separated without spaces. Only the first one is used, so a title
     * appears in a single section instead of being duplicated across its genres.
     */
    private static String firstGenre(String genres) {
        if (genres == null || genres.isBlank()) {
            return null;
        }
        int comma = genres.indexOf(',');
        String first = (comma >= 0 ? genres.substring(0, comma) : genres).trim();
        return first.isEmpty() ? null : first;
    }
}
