package fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb;

import java.util.Map;

import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.ApiListResponse;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Artist;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Episode;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Media;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Movie;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Season;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Serie;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.WatchProvider;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by lucasamiaud on 03/03/2019.
 */

public interface TmdbApi {

    @GET("discover/movie")
    Call<ApiListResponse<Movie>> discoverMovie(
            @QueryMap Map<String, Object> filters
    );

    @GET("movie/{id}")
    Call<Movie> getMovieById(@Path("id") Integer id, @Query("append_to_response") String appendToResponse);

    @GET("movie/{id}/watch/providers")
    Call<WatchProvider.WatchProviderApiResponse> fetchMovieWatchProvider(
            @Path("id") Integer id
    );


    @GET("discover/tv")
    Call<ApiListResponse<Serie>> discoverSerie(
            @QueryMap Map<String, Object> filters
    );

    @GET("tv/{id}")
    Call<Serie> getSerieById(@Path("id") Integer id, @Query("append_to_response") String appendToResponse);

    @GET("tv/{id}/season/{seasonNumber}")
    Call<Season> getSeasonBySerieId(
            @Path("id") Integer id,
            @Path("seasonNumber") Integer seasonNumber,
            @Query("append_to_response") String appendToResponse
    );

    @GET("tv/{id}/season/{seasonNumber}/episode/{episodeNumber}")
    Call<Episode> getEpisodeBySeasonNumberBySerieId(
            @Path("id") Integer id,
            @Path("seasonNumber") Integer seasonNumber,
            @Path("episodeNumber") Integer episodeNumber,
            @Query("append_to_response") String appendToResponse
    );

    @GET("tv/{id}/watch/providers")
    Call<WatchProvider.WatchProviderApiResponse> fetchTvWatchProvider(
            @Path("id") Integer id
    );

    @GET("person/{id}")
    Call<Artist> getArtistById(
            @Path("id") Integer id,
            @Query("append_to_response") String appendToResponse
    );

    @GET("search/multi")
    Call<ApiListResponse<Media>> getSearchByQuery(@Query("query") String query);
}
