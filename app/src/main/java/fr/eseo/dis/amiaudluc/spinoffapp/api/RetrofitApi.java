package fr.eseo.dis.amiaudluc.spinoffapp.api;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RetrofitApi {

    private RetrofitApi() {
        // unused
    }

    public static OkHttpClient.Builder httpClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(interceptor);
    }


    public static <T> LiveData<T> executeAsync(Call<T> call) {
        return executeAsync(call, Function.identity());
    }

    public static <T, R> LiveData<R> executeAsync(Call<T> call, Function<T, R> mapper) {
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

    public static <T> Optional<T> execute(Call<T> call) {
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
}
