package fr.eseo.dis.amiaudluc.spinoffapp.repositories;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import fr.eseo.dis.amiaudluc.spinoffapp.api.ApiService;
import fr.eseo.dis.amiaudluc.spinoffapp.api.TMDBApi;
import fr.eseo.dis.amiaudluc.spinoffapp.api.beans.ApiListResponse;
import fr.eseo.dis.amiaudluc.spinoffapp.api.beans.Artist;
import fr.eseo.dis.amiaudluc.spinoffapp.api.beans.Episode;
import fr.eseo.dis.amiaudluc.spinoffapp.api.beans.Media;
import fr.eseo.dis.amiaudluc.spinoffapp.api.beans.Movie;
import fr.eseo.dis.amiaudluc.spinoffapp.api.beans.Season;
import fr.eseo.dis.amiaudluc.spinoffapp.api.beans.Serie;
import fr.eseo.dis.amiaudluc.spinoffapp.api.beans.WatchProvider;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiRepository {
    private final ApiService<TMDBApi> tmdbApiService;

    private static ApiRepository INSTANCE;

    public static ApiRepository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ApiRepository();
        }
        return INSTANCE;
    }

    private ApiRepository() {
        this.tmdbApiService = new ApiService<>(TMDBApi.class);
    }

    public LiveData<List<Movie>> getMoviesByType(String type, Integer page, List<Movie> previous) {
        return executeAsync(
                this.tmdbApiService.api.getMovies(type, "FR", page),
                response -> {
                    if (response.getPage() == 1) {
                        return response.getResults();
                    }
                    previous.addAll(response.getResults());
                    return previous;
                }
        );
    }

    public LiveData<Movie> getMovieById(Integer id) {
        return executeAsync(this.tmdbApiService.api.getMovieById(id, "credits,videos,recommendations"));
    }

    public Optional<Movie> getMovieByIdSync(Integer id) {
        return execute(this.tmdbApiService.api.getMovieById(id, "credits,videos,recommendations"));
    }

    public LiveData<List<WatchProvider>> fetchMovieWatchProvider(Integer id) {
        return executeAsync(
                this.tmdbApiService.api.fetchMovieWatchProvider(id),
                watchProviderApiResponse -> {
                    WatchProvider.WatchProviders watchProviders = watchProviderApiResponse.results().get("FR");
                    if (watchProviders == null) {
                        return List.of();
                    }
                    return watchProviders.flatrate();
                }
        );
    }

    public LiveData<List<Serie>> getSeriesByType(String type, Integer page, List<Serie> previous) {
        return executeAsync(
                this.tmdbApiService.api.getSeries(type, "FR", page),
                response -> {
                    if (response.getPage() == 1) {
                        return response.getResults();
                    }
                    previous.addAll(response.getResults());
                    return previous;
                }
        );
    }

    public LiveData<Serie> getSerieById(Integer id) {
        return executeAsync(this.tmdbApiService.api.getSerieById(id, "credits,videos,recommendations"));
    }

    public Optional<Serie> getSerieByIdSync(Integer id) {
        return execute(this.tmdbApiService.api.getSerieById(id, "credits,videos,recommendations"));
    }

    public LiveData<List<WatchProvider>> fetchTvWatchProvider(Integer id) {
        return executeAsync(
                this.tmdbApiService.api.fetchTvWatchProvider(id),
                watchProviderApiResponse -> {
                    WatchProvider.WatchProviders watchProviders = watchProviderApiResponse.results().get("FR");
                    if (watchProviders == null) {
                        return List.of();
                    }
                    return watchProviders.flatrate();
                }
        );
    }

    public LiveData<Season> getSeasonBySerieId(Integer id, Integer seasonNumber) {
        return executeAsync(this.tmdbApiService.api.getSeasonBySerieId(id, seasonNumber, "credits,videos"));
    }

    public Optional<Season> getSeasonBySerieIdSync(Integer id, Integer seasonNumber) {
        return execute(this.tmdbApiService.api.getSeasonBySerieId(id, seasonNumber, "credits,videos"));
    }

    public LiveData<Episode> getEpisodeBySeasonNumberBySerieId(Integer id, Integer seasonNumber, Integer episodeNumber) {
        return executeAsync(this.tmdbApiService.api.getEpisodeBySeasonNumberBySerieId(id, seasonNumber, episodeNumber, "credits,videos"));
    }

    public LiveData<Artist> getArtistById(Integer id) {
        return executeAsync(this.tmdbApiService.api.getArtistById(id, "tv_credits,movie_credits"));
    }

    public LiveData<List<Media>> getSearchByQuery(String query) {
        return executeAsync(this.tmdbApiService.api.getSearchByQuery(query), ApiListResponse::getResults);
    }

    private static <T> Optional<T> execute(Call<T> call) {
        try {
            Response<T> response = call.execute();
            if (response.isSuccessful()) {
                return Optional.ofNullable(response.body());
            }
            return Optional.empty();
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    private static <T> LiveData<T> executeAsync(Call<T> call) {
        return executeAsync(call, Function.identity());
    }

    private static <T, R> LiveData<R> executeAsync(Call<T> call, Function<T, R> mapper) {
        final MutableLiveData<R> data = new MutableLiveData<>();
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
                if (response.body() != null) {
                    data.setValue(mapper.apply(response.body()));
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }

}
