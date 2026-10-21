package fr.eseo.dis.amiaudluc.spinoffapp.utils;

import java.text.Collator;
import java.text.Normalizer;
import java.util.Locale;

/**
 * Alphabetical grouping helpers used by the A-Z sections of the watched list.
 * <p>
 * Titles are never stripped of a leading article: the section of a title is its own first
 * letter, so "Les Misérables" belongs to "L" and "The Maze Runner" to "T". Titles are kept in
 * their original language, and only the letter used for grouping is accent-folded, so "Été"
 * is filed under "E".
 */
public final class AlphabetUtils {

    /** Bucket for titles whose first character is not a latin letter (digits, symbols, ...). */
    public static final char OTHER = '#';

    private static final Collator COLLATOR = Collator.getInstance(Locale.ROOT);

    static {
        // Accents must not create sub-groups inside a single letter section.
        COLLATOR.setStrength(Collator.PRIMARY);
    }

    private AlphabetUtils() {
    }

    /**
     * Section letter of a title: its first character, accent-folded and upper-cased, or
     * {@link #OTHER} when it is not a latin letter.
     */
    public static char letterOf(String title) {
        if (title == null) {
            return OTHER;
        }
        String folded = fold(title);
        if (folded.isEmpty()) {
            return OTHER;
        }
        char upper = Character.toUpperCase(folded.charAt(0));
        return (upper >= 'A' && upper <= 'Z') ? upper : OTHER;
    }

    /**
     * Orders titles by section letter first, so every section is a contiguous run of rows, then
     * alphabetically inside the section. The {@link #OTHER} bucket is pushed to the end, and a
     * raw comparison breaks collator ties so the order stays total and deterministic.
     */
    public static int compareTitles(String left, String right) {
        char leftLetter = letterOf(left);
        char rightLetter = letterOf(right);

        boolean leftOther = leftLetter == OTHER;
        boolean rightOther = rightLetter == OTHER;
        if (leftOther != rightOther) {
            return leftOther ? 1 : -1;
        }
        if (leftLetter != rightLetter) {
            return Character.compare(leftLetter, rightLetter);
        }

        int collated = COLLATOR.compare(left == null ? "" : left, right == null ? "" : right);
        if (collated != 0) {
            return collated;
        }
        return (left == null ? "" : left).compareTo(right == null ? "" : right);
    }

    /** Strips the diacritics of a title and trims it, leaving the characters untouched otherwise. */
    private static String fold(String value) {
        return Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .trim();
    }
}
