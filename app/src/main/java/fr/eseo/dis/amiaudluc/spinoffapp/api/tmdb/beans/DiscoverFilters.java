package fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.util.Map;

import lombok.Builder;


@Builder(toBuilder = true)
public record DiscoverFilters(
        String certification,
        @SerializedName("certification.gte")
        String certificationGte,
        @SerializedName("certification.lte")
        String certificationLte,
        String certificationCountry,
        Boolean includeAdult,
        Boolean includeVideo,
        String language,
        Integer page,
        Integer primaryReleaseYear,
        @SerializedName("primary_release_date.gte")
        String primaryReleaseDateGte,
        @SerializedName("primary_release_date.lte")
        String primaryReleaseDateLte,
        String region,
        @SerializedName("release_date.gte")
        String releaseDateGte,
        @SerializedName("release_date.lte")
        String releaseDateLte,
        @SerializedName("air_date.gte")
        String airDateGte,
        @SerializedName("air_date.lte")
        String airDateLte,
        String sortBy,
        @SerializedName("vote_average.gte")
        Double voteAverageGte,
        @SerializedName("vote_average.lte")
        Double voteAverageLte,
        @SerializedName("vote_count.gte")
        Integer voteCountGte,
        @SerializedName("vote_count.lte")
        Integer voteCountLte,
        String watchRegion,
        String withCast,
        String withCompanies,
        String withCrew,
        String withGenres,
        String withoutGenres,
        String withKeywords,
        String withoutKeywords,
        String withOriginalLanguage,
        String withPeople,
        String withReleaseType,
        @SerializedName("with_runtime.gte")
        Integer withRuntimeGte,
        @SerializedName("with_runtime.lte")
        Integer withRuntimeLte,
        String withWatchMonetizationTypes,
        String withWatchProviders,
        String withNetworks,
        Integer year
) {

    private static final Gson GSON = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

    public Map<String, Object> toMap() {
        return GSON.fromJson(GSON.toJson(this), new TypeToken<Map<String, Object>>() {}.getType());
    }
}
