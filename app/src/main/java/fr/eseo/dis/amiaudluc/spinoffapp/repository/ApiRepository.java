package fr.eseo.dis.amiaudluc.spinoffapp.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import java.util.List;

import fr.eseo.dis.amiaudluc.spinoffapp.api.beans.ApiListResponse;
import fr.eseo.dis.amiaudluc.spinoffapp.api.ApiService;
import fr.eseo.dis.amiaudluc.spinoffapp.api.TMDBApi;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Artist;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Media;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Movie;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Season;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Serie;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiRepository {
    private ApiService<TMDBApi> tmdbApiService;

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
        Call<ApiListResponse<Movie>> call = this.tmdbApiService.api.getMovies(type, "FR", page);
        final MutableLiveData<List<Movie>> data = new MutableLiveData<>();
        call.enqueue(new Callback<ApiListResponse<Movie>>() {
            @Override
            public void onResponse(@NonNull Call<ApiListResponse<Movie>> call, @NonNull Response<ApiListResponse<Movie>> response) {
                if (response.body() != null) {
                    if (response.body().getPage() == 1) {
                        data.setValue(response.body().getResults());
                    } else {
                        previous.addAll(response.body().getResults());
                        data.setValue(previous);
                    }
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiListResponse<Movie>> call, @NonNull Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }

    public LiveData<Movie> getMovieById(Integer id) {
        Call<Movie> call = this.tmdbApiService.api.getMovieById(id, "credits,videos");
        final MutableLiveData<Movie> data = new MutableLiveData<>();
        call.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(@NonNull Call<Movie> call, @NonNull Response<Movie> response) {
                if (response.body() != null) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Movie> call, @NonNull Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }

    public LiveData<List<Serie>> getSeriesByType(String type, Integer page, List<Serie> previous) {
        Call<ApiListResponse<Serie>> call = this.tmdbApiService.api.getSeries(type, "FR", page);
        final MutableLiveData<List<Serie>> data = new MutableLiveData<>();
        call.enqueue(new Callback<ApiListResponse<Serie>>() {
            @Override
            public void onResponse(@NonNull Call<ApiListResponse<Serie>> call, @NonNull Response<ApiListResponse<Serie>> response) {
                if (response.body() != null) {
                    if (response.body().getPage() == 1) {
                        data.setValue(response.body().getResults());
                    } else {
                        previous.addAll(response.body().getResults());
                        data.setValue(previous);
                    }
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiListResponse<Serie>> call, @NonNull Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }

    public LiveData<Serie> getSerieById(Integer id) {
        Call<Serie> call = this.tmdbApiService.api.getSerieById(id, "credits,videos");
        final MutableLiveData<Serie> data = new MutableLiveData<>();
        call.enqueue(new Callback<Serie>() {
            @Override
            public void onResponse(@NonNull Call<Serie> call, @NonNull Response<Serie> response) {
                if (response.body() != null) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Serie> call, @NonNull Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }

    public LiveData<Season> getSeasonBySerieId(Integer id, Integer seasonNumber) {
        Call<Season> call = this.tmdbApiService.api.getSeasonBySerieId(id, seasonNumber, "credits,videos");
        final MutableLiveData<Season> data = new MutableLiveData<>();
        call.enqueue(new Callback<Season>() {
            @Override
            public void onResponse(@NonNull Call<Season> call, @NonNull Response<Season> response) {
                if (response.body() != null) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Season> call, @NonNull Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }

    public LiveData<Artist> getArtistById(Integer id) {
        Call<Artist> call = this.tmdbApiService.api.getArtistById(id, "tv_credits,movie_credits");
        final MutableLiveData<Artist> data = new MutableLiveData<>();
        call.enqueue(new Callback<Artist>() {
            @Override
            public void onResponse(@NonNull Call<Artist> call, @NonNull Response<Artist> response) {
                if (response.body() != null) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Artist> call, @NonNull Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }

    public LiveData<List<Media>> getSearchByQuery(String query) {
        Call<ApiListResponse<Media>> call = this.tmdbApiService.api.getSearchByQuery(query);
        final MutableLiveData<List<Media>> data = new MutableLiveData<>();
        call.enqueue(new Callback<ApiListResponse<Media>>() {
            @Override
            public void onResponse(@NonNull Call<ApiListResponse<Media>> call, @NonNull Response<ApiListResponse<Media>> response) {
                if (response.body() != null) {
                    data.setValue(response.body().getResults());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiListResponse<Media>> call, @NonNull Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }
}
