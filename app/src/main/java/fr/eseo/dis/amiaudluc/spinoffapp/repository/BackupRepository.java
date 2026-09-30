package fr.eseo.dis.amiaudluc.spinoffapp.repository;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.time.Instant;
import java.time.ZoneId;

import fr.eseo.dis.amiaudluc.spinoffapp.database.DBInitializer.AppDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.EpisodeDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.MovieDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.SeasonDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.SerieDatabase;

public class BackupRepository {

    private static final int BACKUP_VERSION = 2;

    private final ContentResolver contentResolver;
    private final AppDatabase database;
    private final Gson gson;

    public BackupRepository(ContentResolver contentResolver, AppDatabase database) {
        this.contentResolver = contentResolver;
        this.database = database;
        this.gson = new Gson();
    }

    public void exportLibrary(Uri uri) throws IOException {
        try (OutputStream outputStream = contentResolver.openOutputStream(uri);
             JsonWriter writer = new JsonWriter(new OutputStreamWriter(outputStream))) {
            
            writer.setIndent("  ");
            writer.beginObject();

            writer.name("version");
            writer.value(BACKUP_VERSION);

            // Export Movies
            writer.name("movies");
            writer.beginArray();
            try (Cursor cursor = database.moviesDAO().getAllCursor()) {
                while (cursor.moveToNext()) {
                    MovieDatabase movie = mapMovie(cursor);
                    gson.toJson(movie, MovieDatabase.class, writer);
                }
            }
            writer.endArray();

            // Export Series
            writer.name("series");
            writer.beginArray();
            try (Cursor cursor = database.serieDAO().getAllCursor()) {
                while (cursor.moveToNext()) {
                    SerieDatabase serie = mapSerie(cursor);
                    gson.toJson(serie, SerieDatabase.class, writer);
                }
            }
            writer.endArray();

            // Export Seasons
            writer.name("seasons");
            writer.beginArray();
            try (Cursor cursor = database.seasonDAO().getAllCursor()) {
                while (cursor.moveToNext()) {
                    SeasonDatabase season = mapSeason(cursor);
                    gson.toJson(season, SeasonDatabase.class, writer);
                }
            }
            writer.endArray();

            // Export Episodes
            writer.name("episodes");
            writer.beginArray();
            try (Cursor cursor = database.episodeDAO().getAllCursor()) {
                while (cursor.moveToNext()) {
                    EpisodeDatabase episode = mapEpisode(cursor);
                    gson.toJson(episode, EpisodeDatabase.class, writer);
                }
            }
            writer.endArray();

            writer.endObject();
        }
    }

    public void importLibrary(Uri uri) throws IOException {
        try (InputStream inputStream = contentResolver.openInputStream(uri);
             JsonReader reader = new JsonReader(new InputStreamReader(inputStream))) {
            
            database.runInTransaction(() -> {
                try {
                    // Purge child first if there are FKs
                    database.episodeDAO().deleteAll();
                    database.seasonDAO().deleteAll();
                    database.moviesDAO().deleteAll();
                    database.serieDAO().deleteAll();

                    reader.beginObject();
                    while (reader.hasNext()) {
                        String name = reader.nextName();
                        switch (name) {
                            case "movies":
                                reader.beginArray();
                                while (reader.hasNext()) {
                                    MovieDatabase movie = gson.fromJson(reader, MovieDatabase.class);
                                    database.moviesDAO().insertMovie(movie);
                                }
                                reader.endArray();
                                break;
                            case "series":
                            case "serie":
                                reader.beginArray();
                                while (reader.hasNext()) {
                                    SerieDatabase serie = gson.fromJson(reader, SerieDatabase.class);
                                    database.serieDAO().insertSerie(serie);
                                }
                                reader.endArray();
                                break;
                            case "seasons":
                                reader.beginArray();
                                while (reader.hasNext()) {
                                    SeasonDatabase season = gson.fromJson(reader, SeasonDatabase.class);
                                    database.seasonDAO().upsertSeason(
                                            season.getSerieId(), season.getSeasonNumber(),
                                            season.getEpisodeCount() != null ? season.getEpisodeCount() : 0);
                                    if (season.isWatched()) {
                                        database.seasonDAO().setSeasonWatched(
                                                season.getSerieId(), season.getSeasonNumber(), true);
                                    }
                                }
                                reader.endArray();
                                break;
                            case "episodes":
                                reader.beginArray();
                                while (reader.hasNext()) {
                                    JsonObject episodeJson = gson.fromJson(reader, JsonObject.class);
                                    // Legacy backups used a boolean "number" field and no "watched": skip them
                                    if (episodeJson != null && episodeJson.has("episode_number")) {
                                        EpisodeDatabase episode = gson.fromJson(episodeJson, EpisodeDatabase.class);
                                        database.episodeDAO().insertEpisode(episode);
                                    }
                                }
                                reader.endArray();
                                break;
                            default:
                                reader.skipValue();
                                break;
                        }
                    }
                    reader.endObject();
                } catch (IOException e) {
                    throw new RuntimeException("Erreur de lecture du fichier JSON", e);
                }
            });
        } catch (RuntimeException e) {
            if (e.getCause() instanceof IOException) {
                throw (IOException) e.getCause();
            }
            throw e;
        }
    }

    private MovieDatabase mapMovie(Cursor cursor) {
        MovieDatabase movie = new MovieDatabase();
        movie.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
        movie.setTitle(cursor.getString(cursor.getColumnIndexOrThrow("title")));
        movie.setPosterPath(cursor.getString(cursor.getColumnIndexOrThrow("poster_path")));
        movie.setRuntime(cursor.isNull(cursor.getColumnIndexOrThrow("runtime")) ? null : cursor.getInt(cursor.getColumnIndexOrThrow("runtime")));
        movie.setWatched(cursor.getInt(cursor.getColumnIndexOrThrow("watched")) == 1);
        
        if (!cursor.isNull(cursor.getColumnIndexOrThrow("last_synchronisation_time"))) {
            movie.setLastSynchronisationTime(Instant.ofEpochMilli(cursor.getLong(cursor.getColumnIndexOrThrow("last_synchronisation_time"))));
        }
        
        movie.setGenres(cursor.getString(cursor.getColumnIndexOrThrow("genres")));
        
        if (!cursor.isNull(cursor.getColumnIndexOrThrow("release_date"))) {
            movie.setReleaseDate(Instant.ofEpochMilli(cursor.getLong(cursor.getColumnIndexOrThrow("release_date")))
                    .atZone(ZoneId.systemDefault()).toLocalDate());
        }
        
        movie.setVoteAverage(cursor.isNull(cursor.getColumnIndexOrThrow("vote_average")) ? null : cursor.getDouble(cursor.getColumnIndexOrThrow("vote_average")));
        movie.setOriginalLanguage(cursor.getString(cursor.getColumnIndexOrThrow("original_language")));
        movie.setPopularity(cursor.isNull(cursor.getColumnIndexOrThrow("popularity")) ? null : cursor.getDouble(cursor.getColumnIndexOrThrow("popularity")));
        movie.setProductionCountries(cursor.getString(cursor.getColumnIndexOrThrow("production_countries")));
        movie.setActors(cursor.getString(cursor.getColumnIndexOrThrow("actors")));
        movie.setDirectors(cursor.getString(cursor.getColumnIndexOrThrow("directors")));
        
        return movie;
    }

    private SerieDatabase mapSerie(Cursor cursor) {
        SerieDatabase serie = new SerieDatabase();
        serie.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
        serie.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
        serie.setPosterPath(cursor.getString(cursor.getColumnIndexOrThrow("poster_path")));
        serie.setRuntime(cursor.getInt(cursor.getColumnIndexOrThrow("runtime")));
        serie.setWatched(cursor.getInt(cursor.getColumnIndexOrThrow("watched")) == 1);

        if (!cursor.isNull(cursor.getColumnIndexOrThrow("last_synchronisation_time"))) {
            serie.setLastSynchronisationTime(Instant.ofEpochMilli(cursor.getLong(cursor.getColumnIndexOrThrow("last_synchronisation_time"))));
        }

        serie.setGenres(cursor.getString(cursor.getColumnIndexOrThrow("genres")));
        serie.setSeasonCount(cursor.isNull(cursor.getColumnIndexOrThrow("season_count")) ? null : cursor.getInt(cursor.getColumnIndexOrThrow("season_count")));
        serie.setEpisodeCount(cursor.isNull(cursor.getColumnIndexOrThrow("episode_count")) ? null : cursor.getInt(cursor.getColumnIndexOrThrow("episode_count")));

        if (!cursor.isNull(cursor.getColumnIndexOrThrow("first_air_date"))) {
            serie.setFirstAirDate(Instant.ofEpochMilli(cursor.getLong(cursor.getColumnIndexOrThrow("first_air_date")))
                    .atZone(ZoneId.systemDefault()).toLocalDate());
        }

        serie.setVoteAverage(cursor.isNull(cursor.getColumnIndexOrThrow("vote_average")) ? null : cursor.getDouble(cursor.getColumnIndexOrThrow("vote_average")));
        serie.setOriginalLanguage(cursor.getString(cursor.getColumnIndexOrThrow("original_language")));
        serie.setPopularity(cursor.isNull(cursor.getColumnIndexOrThrow("popularity")) ? null : cursor.getDouble(cursor.getColumnIndexOrThrow("popularity")));
        serie.setProductionCountries(cursor.getString(cursor.getColumnIndexOrThrow("production_countries")));
        serie.setActors(cursor.getString(cursor.getColumnIndexOrThrow("actors")));
        serie.setDirectors(cursor.getString(cursor.getColumnIndexOrThrow("directors")));

        return serie;
    }

    private SeasonDatabase mapSeason(Cursor cursor) {
        SeasonDatabase season = new SeasonDatabase();
        season.setSerieId(cursor.getInt(cursor.getColumnIndexOrThrow("serie_id")));
        season.setSeasonNumber(cursor.getInt(cursor.getColumnIndexOrThrow("season_number")));
        season.setEpisodeCount(cursor.isNull(cursor.getColumnIndexOrThrow("episode_count")) ? null : cursor.getInt(cursor.getColumnIndexOrThrow("episode_count")));
        season.setWatched(cursor.getInt(cursor.getColumnIndexOrThrow("watched")) == 1);
        return season;
    }

    private EpisodeDatabase mapEpisode(Cursor cursor) {
        EpisodeDatabase episode = new EpisodeDatabase();
        episode.setSerieId(cursor.getInt(cursor.getColumnIndexOrThrow("serie_id")));
        episode.setSeasonNumber(cursor.getInt(cursor.getColumnIndexOrThrow("season_number")));
        episode.setEpisodeNumber(cursor.getInt(cursor.getColumnIndexOrThrow("episode_number")));
        return episode;
    }
}
