package fr.eseo.dis.amiaudluc.spinoffapp.database.dao;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Guards the split between the daily release check and the full metadata synchronisation.
 * <p>
 * The release check only refreshes the columns it owns through {@code updateSerieReleaseSnapshot}.
 * If a metadata column leaked into that statement, the lightweight TMDB payload would erase the
 * genres, cast, directors or runtime collected by the full synchronisation.
 * <p>
 * Room annotations have class retention, so the statement is read from the source file instead of
 * being introspected.
 */
public class SerieDAOQueryTest {

    private static final String TARGET_METHOD = "updateSerieReleaseSnapshot";

    @Test
    public void releaseSnapshot_refreshesTheTrackingColumns() throws IOException {
        String query = releaseSnapshotQuery();

        assertTrue(query.contains("season_count"));
        assertTrue(query.contains("episode_count"));
        assertTrue(query.contains("last_synchronisation_time"));
        assertTrue(query.contains("poster_path"));
        assertTrue(query.contains("set name"));
    }

    @Test
    public void releaseSnapshot_neverTouchesTheMetadataColumns() throws IOException {
        String query = releaseSnapshotQuery();

        for (String metadataColumn : new String[]{
                "genres", "actors", "directors", "runtime", "vote_average", "popularity",
                "original_language", "production_countries", "first_air_date", "watched"}) {
            assertFalse("the daily release check must not overwrite " + metadataColumn,
                    query.contains(metadataColumn));
        }
    }

    /**
     * Extract the SQL of the annotated query that directly precedes the target method.
     */
    private static String releaseSnapshotQuery() throws IOException {
        String source = Files.readString(sourceFile(), StandardCharsets.UTF_8);
        int methodIndex = source.indexOf(TARGET_METHOD + "(");
        assertTrue("method " + TARGET_METHOD + " not found", methodIndex > 0);

        String before = source.substring(0, methodIndex);
        int annotationIndex = before.lastIndexOf("@Query(");
        assertTrue("no @Query annotation above " + TARGET_METHOD, annotationIndex > 0);

        Matcher matcher = Pattern.compile("\"((?:[^\"\\\\]|\\\\.)*)\"").matcher(before.substring(annotationIndex));
        StringBuilder sql = new StringBuilder();
        while (matcher.find()) {
            sql.append(matcher.group(1));
        }
        return sql.toString().toLowerCase(Locale.ROOT);
    }

    private static Path sourceFile() {
        Optional<Path> direct = Optional.of(Paths.get(
                "src", "main", "java", "fr", "eseo", "dis", "amiaudluc", "spinoffapp",
                "database", "dao", "SerieDAO.java"));
        if (Files.exists(direct.get())) {
            return direct.get();
        }
        return Paths.get("app", "src", "main", "java", "fr", "eseo", "dis", "amiaudluc", "spinoffapp",
                "database", "dao", "SerieDAO.java");
    }
}
