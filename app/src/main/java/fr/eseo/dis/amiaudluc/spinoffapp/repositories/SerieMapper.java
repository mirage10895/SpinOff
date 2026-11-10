package fr.eseo.dis.amiaudluc.spinoffapp.repositories;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.TmdbApiRepository;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Artist;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Genre;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.ProductionCountry;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Season;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Serie;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.SerieDatabase;

/**
 * Converts a TMDB serie payload into the library entity, including the aggregated runtime.
 * <p>
 * The runtime may need an extra season request when TMDB does not expose the episode duration at
 * the serie level, which is why this mapper receives the API repository.
 */
final class SerieMapper {

    private SerieMapper() {
        // utility class
    }

    static SerieDatabase toDatabase(Serie apiSerie, boolean isWatched, TmdbApiRepository apiRepository) {
        SerieDatabase serieDatabase = new SerieDatabase();
        serieDatabase.setId(apiSerie.getId());
        serieDatabase.setName(apiSerie.getName());
        serieDatabase.setPosterPath(apiSerie.getPosterPath());
        serieDatabase.setWatched(isWatched);
        serieDatabase.setGenres(TmdbApiRepository.formatList(apiSerie.getGenres(), Genre::getName));
        serieDatabase.setEpisodeCount(apiSerie.getNumberOfEpisodes());
        serieDatabase.setSeasonCount(apiSerie.getNumberOfSeasons());
        serieDatabase.setFirstAirDate(apiSerie.getFirstAirDate());
        serieDatabase.setVoteAverage(apiSerie.getVoteAverage());
        serieDatabase.setOriginalLanguage(apiSerie.getOriginalLanguage());
        serieDatabase.setPopularity(apiSerie.getPopularity());
        serieDatabase.setProductionCountries(
                TmdbApiRepository.formatList(apiSerie.getProductionCountries(), ProductionCountry::getName));
        serieDatabase.setActors(TmdbApiRepository.formatList(apiSerie.getCredits().getCast(), Artist::getName));
        serieDatabase.setDirectors(TmdbApiRepository.formatList(apiSerie.getCreatedBy(), Artist::getName));
        serieDatabase.setLastSynchronisationTime(Instant.now());
        serieDatabase.setRuntime(computeRuntime(apiSerie, apiRepository));
        return serieDatabase;
    }

    private static int computeRuntime(Serie apiSerie, TmdbApiRepository apiRepository) {
        if (apiSerie.getNumberOfSeasons() == null || apiSerie.getNumberOfSeasons() == 0) {
            return 0;
        }
        List<Integer> episodeRunTime = apiSerie.getEpisodeRunTime();
        if (episodeRunTime != null && !episodeRunTime.isEmpty()) {
            return Serie.computeTotalRuntime(apiSerie);
        }
        Optional<Season> firstSeason = apiRepository.getSeasonBySerieIdSync(apiSerie.getId(), 1);
        return firstSeason
                .map(season -> Season.computeEpisodesAverageRuntime(season) * apiSerie.getNumberOfEpisodes())
                .orElse(0);
    }
}
