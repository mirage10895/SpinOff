package fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb;

import androidx.lifecycle.LiveData;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import fr.eseo.dis.amiaudluc.BuildConfig;
import fr.eseo.dis.amiaudluc.spinoffapp.api.RetrofitApi;
import fr.eseo.dis.amiaudluc.spinoffapp.api.gson.InterfaceAdapter;
import fr.eseo.dis.amiaudluc.spinoffapp.api.gson.LocalDateAdapter;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.ApiListResponse;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Artist;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.DiscoverFilters;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Episode;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Media;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Movie;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Season;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Serie;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.adapter.WatchProviderAdapterData;
import fr.eseo.dis.amiaudluc.spinoffapp.utils.WatchProviderUtils;
import lombok.Getter;
import okhttp3.HttpUrl;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by lucasamiaud on 03/03/2019.
 */

@Getter
public class TmdbApiRepository {
    private static TmdbApiRepository INSTANCE = null;

    public final TmdbApi api;
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Media.class, new InterfaceAdapter<Media>())
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";
    private static final String APPLICATION_JSON_CHARSET_UTF8 = APPLICATION_JSON + "; charset=utf-8";
    private static final String BASE_URL = "https://api.themoviedb.org/3/";

    public static TmdbApiRepository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TmdbApiRepository();
        }
        return INSTANCE;
    }
    private TmdbApiRepository() {
        Retrofit retrofit = new Retrofit
                .Builder()
                .baseUrl(BASE_URL)
                .client(
                        RetrofitApi.httpClient()
                                .addInterceptor(chain -> {
                                    Request original = chain.request();
                                    HttpUrl originalHttpUrl = original.url();

                                    HttpUrl url = originalHttpUrl.newBuilder()
                                            .addQueryParameter("api_key", BuildConfig.TMDB_API_KEY)
                                            .addQueryParameter("language", "en-US")
                                            .build();

                                    Request request = original.newBuilder()
                                            .url(url)
                                            .addHeader(CONTENT_TYPE_HEADER, APPLICATION_JSON_CHARSET_UTF8)
                                            .build();

                                    return chain.proceed(request);
                                })
                                .build()
                )
                .addConverterFactory(GsonConverterFactory.create(GSON))
                .build();

        this.api = retrofit.create(TmdbApi.class);
    }

    public LiveData<List<Movie>> discoverMovie(DiscoverFilters filters) {
        return RetrofitApi.executeAsync(
                this.api.discoverMovie(filters.toMap()),
                ApiListResponse::getResults
        );
    }

    public LiveData<Movie> getMovieById(Integer id) {
        return RetrofitApi.executeAsync(this.api.getMovieById(id, "credits,videos,recommendations"));
    }

    public Optional<Movie> getMovieByIdSync(Integer id) {
        return RetrofitApi.execute(this.api.getMovieById(id, "credits,videos,recommendations"));
    }

    public LiveData<List<WatchProviderAdapterData>> fetchMovieWatchProvider(Integer id, String imdbId) {
        return RetrofitApi.executeAsync(
                this.api.fetchMovieWatchProvider(id),
                watchProviderApiResponse -> WatchProviderUtils.formatWatchProviders(
                        watchProviderApiResponse,
                        Media.MOVIE,
                        imdbId
                )
        );
    }

    public LiveData<List<Serie>> discoverSerie(DiscoverFilters filters) {
        return RetrofitApi.executeAsync(
                this.api.discoverSerie(
                        filters.toMap()
                ),
                ApiListResponse::getResults
        );
    }

    public LiveData<Serie> getSerieById(Integer id) {
        return RetrofitApi.executeAsync(this.api.getSerieById(id, "credits,videos,recommendations,external_ids"));
    }

    public Optional<Serie> getSerieByIdSync(Integer id) {
        return RetrofitApi.execute(this.api.getSerieById(id, "credits,videos,recommendations"));
    }

    public LiveData<List<WatchProviderAdapterData>> fetchTvWatchProvider(Integer id, String imdbId) {
        return RetrofitApi.executeAsync(
                this.api.fetchTvWatchProvider(id),
                watchProviderApiResponse -> WatchProviderUtils.formatWatchProviders(
                        watchProviderApiResponse,
                        Media.SERIE,
                        imdbId
                )
        );
    }

    public LiveData<Season> getSeasonBySerieId(Integer id, Integer seasonNumber) {
        return RetrofitApi.executeAsync(this.api.getSeasonBySerieId(id, seasonNumber, "credits,videos"));
    }

    public Optional<Season> getSeasonBySerieIdSync(Integer id, Integer seasonNumber) {
        return RetrofitApi.execute(this.api.getSeasonBySerieId(id, seasonNumber, "credits,videos"));
    }

    public LiveData<Episode> getEpisodeBySeasonNumberBySerieId(Integer id, Integer seasonNumber, Integer episodeNumber) {
        return RetrofitApi.executeAsync(this.api.getEpisodeBySeasonNumberBySerieId(id, seasonNumber, episodeNumber, "credits,videos"));
    }

    public LiveData<Artist> getArtistById(Integer id) {
        return RetrofitApi.executeAsync(this.api.getArtistById(id, "tv_credits,movie_credits"));
    }

    public LiveData<List<Media>> getSearchByQuery(String query) {
        return RetrofitApi.executeAsync(this.api.getSearchByQuery(query), ApiListResponse::getResults);
    }

    public static <T> String formatList(List<T> list, Function<T, String> toString) {
        return list.stream().map(toString).collect(Collectors.joining(","));
    }
}
