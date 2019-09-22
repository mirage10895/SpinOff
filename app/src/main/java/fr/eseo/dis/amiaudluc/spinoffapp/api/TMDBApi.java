package fr.eseo.dis.amiaudluc.spinoffapp.api;

import fr.eseo.dis.amiaudluc.spinoffapp.api.beans.ApiListResponse;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Artist;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Episode;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Media;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Movie;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Season;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Serie;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by lucasamiaud on 03/03/2019.
 */

public interface TMDBApi {

    @GET("movie/{type}")
    Call<ApiListResponse<Movie>> getMovies(@Path("type") String type, @Query("region") String region, @Query("page") Integer page);

    @GET("movie/{id}")
    Call<Movie> getMovieById(@Path("id") Integer id, @Query("append_to_response") String appendToResponse);

    @GET("movie/{id}/recommendations")
    Call<ApiListResponse<Movie>> getRecommandationsByMovieId(@Path("id") Integer id);

    @GET("tv/{type}")
    Call<ApiListResponse<Serie>> getSeries(@Path("type") String type, @Query("region") String region, @Query("page") Integer page);

    @GET("tv/{id}")
    Call<Serie> getSerieById(@Path("id") Integer id, @Query("append_to_response") String appendToResponse);

    @GET("tv/{id}/recommendations")
    Call<ApiListResponse<Serie>> getRecommandationsBySerieId(@Path("id") Integer id);

    @GET("tv/{id}/season/{seasonNumber}")
    Call<Season> getSeasonBySerieId(@Path("id") Integer id, @Path("seasonNumber") Integer seasonNumber, @Query("append_to_response") String appendToResponse);

    @GET("tv/{id}/season/{seasonNumber}/episode/{episodeNumber}")
    Call<Episode> getEpisodeBySeasonNumberBySerieId(@Path("id") Integer id, @Path("seasonNumber") Integer seasonNumber, @Path("episodeNumber") Integer episodeNumber,  @Query("append_to_response") String appendToResponse);

    @GET("person/{id}")
    Call<Artist> getArtistById(@Path("id") Integer id, @Query("append_to_response") String append_to_response);

    @GET("search/multi")
    Call<ApiListResponse<Media>> getSearchByQuery(@Query("query") String query);

}
