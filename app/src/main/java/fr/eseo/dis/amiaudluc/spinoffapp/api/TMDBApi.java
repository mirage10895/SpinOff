package fr.eseo.dis.amiaudluc.spinoffapp.api;

import fr.eseo.dis.amiaudluc.spinoffapp.api.beans.ApiListResponse;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Media;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Movie;
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

    @GET("tv/{type}")
    Call<ApiListResponse<Serie>> getSeries(@Path("type") String type, @Query("region") String region, @Query("page") Integer page);

    @GET("tv/{id}")
    Call<Serie> getSerieById(@Path("id") Integer id);

    @GET("search/multi")
    Call<ApiListResponse<Media>> getSearchByQuery(@Query("query") String query);

}
