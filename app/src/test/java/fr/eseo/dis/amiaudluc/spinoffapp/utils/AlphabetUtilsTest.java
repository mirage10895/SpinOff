package fr.eseo.dis.amiaudluc.spinoffapp.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Unit tests of the A-Z grouping rules: titles keep their leading article, are grouped by their
 * own first letter, and the non-latin bucket goes last.
 */
public class AlphabetUtilsTest {

    @Test
    public void letterOf_keepsTheLeadingArticle() {
        assertEquals('L', AlphabetUtils.letterOf("Les Misérables"));
        assertEquals('T', AlphabetUtils.letterOf("The Maze Runner"));
    }

    @Test
    public void letterOf_foldsAccents() {
        assertEquals('E', AlphabetUtils.letterOf("Été"));
        assertEquals('A', AlphabetUtils.letterOf("À la folie"));
        assertEquals('C', AlphabetUtils.letterOf("Ça"));
    }

    @Test
    public void letterOf_fallsBackToOtherBucket() {
        assertEquals(AlphabetUtils.OTHER, AlphabetUtils.letterOf("12 Hommes en colère"));
        assertEquals(AlphabetUtils.OTHER, AlphabetUtils.letterOf("« Citation »"));
        assertEquals(AlphabetUtils.OTHER, AlphabetUtils.letterOf(null));
        assertEquals(AlphabetUtils.OTHER, AlphabetUtils.letterOf("   "));
    }

    @Test
    public void letterOf_ignoresSurroundingSpaces() {
        assertEquals('Z', AlphabetUtils.letterOf("  Zodiac"));
    }

    @Test
    public void compareTitles_ordersByLetterAndPushesOtherLast() {
        assertTrue(AlphabetUtils.compareTitles("Alien", "Zodiac") < 0);
        assertTrue(AlphabetUtils.compareTitles("Zodiac", "Alien") > 0);
        assertTrue(AlphabetUtils.compareTitles("Zodiac", "12 Angry Men") < 0);
        assertTrue(AlphabetUtils.compareTitles("12 Angry Men", "Zodiac") > 0);
        assertEquals(0, AlphabetUtils.compareTitles("Alien", "Alien"));
    }

    @Test
    public void compareTitles_sortsInsideTheSameLetter() {
        assertTrue(AlphabetUtils.compareTitles("Alien", "Aliens") < 0);
        assertTrue(AlphabetUtils.compareTitles("Été", "Ezio") < 0);
    }
}
