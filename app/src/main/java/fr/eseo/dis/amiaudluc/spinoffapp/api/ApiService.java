package fr.eseo.dis.amiaudluc.spinoffapp.api;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import fr.eseo.dis.amiaudluc.spinoffapp.api.gson.DateAdapter;
import fr.eseo.dis.amiaudluc.spinoffapp.api.gson.InterfaceAdapter;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Media;
import fr.eseo.dis.amiaudluc.spinoffapp.utils.ConstUtils;
import lombok.Getter;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by lucasamiaud on 03/03/2019.
 */

@Getter
public class ApiService<T> {
    public final T api;
    public final Retrofit retrofit;

    private static final String CONTENT_TYPE_HEADER = "Content-MovieType";
    private static final String APPLICATION_JSON = "application/json";
    private static final String APPLICATION_JSON_CHARSET_UTF8 = APPLICATION_JSON + "; charset=utf-8";
    private static final String BASE_URL = "https://api.themoviedb.org/3/";



    public ApiService(Class<T> typeClass) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Media.class, new InterfaceAdapter<Media>())
                .registerTypeAdapter(Date.class, new DateAdapter())
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();


        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        this.retrofit = new Retrofit
                .Builder()
                .baseUrl(BASE_URL)
                .client(
                        new OkHttpClient
                                .Builder()
                                .connectTimeout(15, TimeUnit.SECONDS)
                                .writeTimeout(15, TimeUnit.SECONDS)
                                .readTimeout(60, TimeUnit.SECONDS)
                                .addInterceptor(interceptor)
                                .addInterceptor(chain -> {
                                    Request original = chain.request();
                                    HttpUrl originalHttpUrl = original.url();

                                    HttpUrl url =
                                            originalHttpUrl
                                                    .newBuilder()
                                                    .addQueryParameter("api_key", ConstUtils.API_KEY)
                                                    .addQueryParameter("language", "en-US")
                                                    .build();

                                    Request request =
                                            original
                                                    .newBuilder()
                                                    .url(url)
                                                    .addHeader(CONTENT_TYPE_HEADER, APPLICATION_JSON_CHARSET_UTF8)
                                                    .build();

                                    return chain.proceed(request);
                                })
                                .build()
                )
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        this.api = this.retrofit.create(typeClass);
    }
}
