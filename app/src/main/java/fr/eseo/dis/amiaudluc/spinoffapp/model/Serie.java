package fr.eseo.dis.amiaudluc.spinoffapp.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.DBInitializer.RoomTypeConverter;

/**
 * Created by lucasamiaud on 28/02/2018.
 */

@Entity(tableName = "series")
public class Serie implements Media{

    @ColumnInfo(name = "backdrop_path")
    private String backdropPath;
    @Ignore
    private ArrayList<Artist> creators;
    @ColumnInfo(name = "runtime")
    private int runtime;
    @ColumnInfo(name = "first_air_date")
    @TypeConverters(RoomTypeConverter.class)
    private Date firstAirDate;
    @Ignore
    private ArrayList<Genre> genres = new ArrayList<>();
    @ColumnInfo(name = "home_page")
    private String homepage;
    @PrimaryKey
    private int id;
    @ColumnInfo(name = "in_production")
    private boolean inProduction;
    @Ignore
    private ArrayList<Language> languages = new ArrayList<>();
    @ColumnInfo(name = "last_air_date")
    @TypeConverters(RoomTypeConverter.class)
    private Date lastAirDate;
    @ColumnInfo(name = "name")
    private String name;
    @Ignore
    private ArrayList<Network> networks = new ArrayList<>();
    @ColumnInfo(name = "number_of_episodes")
    private int numberOfEpisodes;
    @ColumnInfo(name = "number_of_seasons")
    private int numberOfSeasons;
    @Ignore
    private Language originCountry;
    @Ignore
    private Language originalLanguage;
    @ColumnInfo(name = "original_name")
    private String originalName;
    @ColumnInfo(name = "overview")
    private String overview;
    @ColumnInfo(name = "popularity")
    private double popularity;
    @ColumnInfo(name = "poster_path")
    private String posterPath;
    @TypeConverters(RoomTypeConverter.class)
    @ColumnInfo(name = "production_companies")
    private ArrayList<ProductionCompany> productionCompanies = new ArrayList<>();
    @Nullable
    @TypeConverters(RoomTypeConverter.class)
    private ArrayList<Season> seasons = new ArrayList<>();
    @ColumnInfo(name = "status")
    private String status;
    @ColumnInfo(name = "type")
    private String type;
    @ColumnInfo(name = "note_avg")
    private double noteAvg;
    @ColumnInfo(name = "vote_count")
    private int voteCount;
    @ColumnInfo(name = "media_type")
    private String mediaType;

    private static final String INPRODUCTION = "In Production";
    private static final String PLANNED = "Planned";
    private static final String RETURNING = "Returning Series";

    public Serie(){
        this.id = -1;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public ArrayList<Artist> getCreators() {
        return creators;
    }

    public void setCreators(ArrayList<Artist> creators) {
        this.creators = creators;
    }

    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    public Date getFirstAirDate() {
        return firstAirDate;
    }

    public void setFirstAirDate(Date firstAirDate) {
        this.firstAirDate = firstAirDate;
    }

    public ArrayList<Genre> getGenres() {
        return genres;
    }

    public void setGenres(ArrayList<Genre> genres) {
        this.genres = genres;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isInProduction() {
        return inProduction;
    }

    public void setInProduction(boolean inProduction) {
        this.inProduction = inProduction;
    }

    public ArrayList<Language> getLanguages() {
        return languages;
    }

    public void setLanguages(ArrayList<Language> languages) {
        this.languages = languages;
    }

    public Date getLastAirDate() {
        return lastAirDate;
    }

    public void setLastAirDate(Date lastAirDate) {
        this.lastAirDate = lastAirDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Network> getNetworks() {
        return networks;
    }

    public void setNetworks(ArrayList<Network> networks) {
        this.networks = networks;
    }

    public int getNumberOfEpisodes() {
        return numberOfEpisodes;
    }

    public void setNumberOfEpisodes(int numberOfEpisodes) {
        this.numberOfEpisodes = numberOfEpisodes;
    }

    public int getNumberOfSeasons() {
        return numberOfSeasons;
    }

    public void setNumberOfSeasons(int numberOfSeasons) {
        this.numberOfSeasons = numberOfSeasons;
    }

    public Language getOriginCountry() {
        return originCountry;
    }

    public void setOriginCountry(Language originCountry) {
        this.originCountry = originCountry;
    }

    public Language getOriginalLanguage() {
        return originalLanguage;
    }

    public void setOriginalLanguage(Language originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public ArrayList<ProductionCompany> getProductionCompanies() {
        return productionCompanies;
    }

    public void setProductionCompanies(ArrayList<ProductionCompany> productionCompanies) {
        this.productionCompanies = productionCompanies;
    }

    public ArrayList<Season> getSeasons() {
        return seasons;
    }

    public void setSeasons(ArrayList<Season> seasons) {
        this.seasons = seasons;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getNoteAvg() {
        return noteAvg;
    }

    public void setNoteAvg(double noteAvg) {
        this.noteAvg = noteAvg;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public List<String> getSeasonsNumbers(){
        List<String> numbers = new ArrayList<>();
        if (seasons != null) {
             numbers = seasons.stream().map(Season::getSeasonNumber).map(String::valueOf).collect(Collectors.toList());
        }
        return numbers;
    }

    @Override
    public String getMediaType() {
        return this.mediaType;
    }

    @Override
    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }
}
