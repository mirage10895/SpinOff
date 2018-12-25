package fr.eseo.dis.amiaudluc.spinoffapp.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

import fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.DBInitializer.RoomTypeConverter;

/**
 * Created by lucasamiaud on 28/02/2018.
 */

@Entity(tableName = "movies")
public class Movie implements Media{

    @ColumnInfo(name = "adult")
    private boolean adult;
    @ColumnInfo(name = "backdrop_path")
    private String backdropPath;
    @ColumnInfo(name = "budget")
    private int budget;
    @Ignore
    private ArrayList<Genre> genres = new ArrayList<>();
    @ColumnInfo(name = "home_page")
    private String homePage;
    @PrimaryKey
    private int id;
    @ColumnInfo(name = "imdb_id")
    private int imdbId;
    @Ignore
    private Language originalLanguage;
    @ColumnInfo(name = "original_tile")
    private String originalTitle;
    @ColumnInfo(name = "overview")
    private String overview;
    @ColumnInfo(name = "popularity")
    private double popularity;
    @ColumnInfo(name = "poster_path")
    private String posterPath;
    @TypeConverters(RoomTypeConverter.class)
    private ArrayList<ProductionCompany> productionCompanies = new ArrayList<>();
    @Ignore
    private ArrayList<ProductionCountry> productionCountry = new ArrayList<>();
    @ColumnInfo(name = "release_date")
    @TypeConverters(RoomTypeConverter.class)
    private Date releaseDate;
    @ColumnInfo(name = "revenue")
    private int revenue;
    @ColumnInfo(name = "runtime")
    private int runtime;
    @Ignore
    private ArrayList<Language> spokenLanguages = new ArrayList<>();
    @ColumnInfo(name = "status")
    private String status;
    @ColumnInfo(name = "tagline")
    private String tagline;
    @ColumnInfo(name = "title")
    private String title;
    @ColumnInfo(name = "video")
    private boolean video;
    @ColumnInfo(name = "vote_avg")
    private double voteAvg;
    @ColumnInfo(name = "vote_count")
    private int voteCount;
    @ColumnInfo(name = "media_type")
    private String mediaType;
    @Ignore
    private ArrayList<Artist> crew = new ArrayList<>();
    @Ignore
    private ArrayList<Artist> cast = new ArrayList<>();
    @Ignore
    private ArrayList<Video> videos = new ArrayList<>();

    public Movie(){
        this.id = -1;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public int getBudget() {
        return budget;
    }

    public void setBudget(int budget) {
        this.budget = budget;
    }

    public ArrayList<Genre> getGenres() {
        return genres;
    }
    public ArrayList<String> getGenresNames(){
        ArrayList<String> result = new ArrayList<>();
        for (Genre gere: getGenres()){
            result.add(gere.getName());
        }
        return result;
    }

    public void setGenres(ArrayList<Genre> genres) {
        this.genres = genres;
    }

    public String getHomePage() {
        return homePage;
    }

    public void setHomePage(String homePage) {
        this.homePage = homePage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getImdbId() {
        return imdbId;
    }

    public void setImdbId(int imdbId) {
        this.imdbId = imdbId;
    }

    public Language getOriginalLanguage() {
        return originalLanguage;
    }

    public void setOriginalLanguage(Language originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
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

    public ArrayList<ProductionCountry> getProductionCountry() {
        return productionCountry;
    }

    public void setProductionCountry(ArrayList<ProductionCountry> productionCountry) {
        this.productionCountry = productionCountry;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public int getRevenue() {
        return revenue;
    }

    public void setRevenue(int revenue) {
        this.revenue = revenue;
    }

    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    public ArrayList<Language> getSpokenLanguages() {
        return spokenLanguages;
    }

    public void setSpokenLanguages(ArrayList<Language> spokenLanguages) {
        this.spokenLanguages = spokenLanguages;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTagline() {
        return tagline;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isVideo() {
        return video;
    }

    public void setVideo(boolean video) {
        this.video = video;
    }

    public double getVoteAvg() {
        return voteAvg;
    }

    public void setVoteAvg(double voteAvg) {
        this.voteAvg = voteAvg;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public boolean isAdult() {

        return adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    public ArrayList<Artist> getCrew() {
        return crew;
    }

    public void setCrew(ArrayList<Artist> crew) {
        this.crew = crew;
    }

    public ArrayList<Artist> getCast() {
        return cast;
    }

    public ArrayList<Artist> getDirectors() {
        ArrayList<Artist> directors = new ArrayList<>();
        this.crew.forEach(artist -> {
            if (artist.getJob().equals("Director")){
                directors.add(artist);
            }
        });
        return directors;
    }

    public void setCast(ArrayList<Artist> cast) {
        this.cast = cast;
    }

    public ArrayList<Video> getVideos() {
        return videos;
    }

    public void setVideos(ArrayList<Video> videos) {
        this.videos = videos;
    }

    public Video getRightVideo(){
        Optional<Video> video = this.videos.stream().filter(video1 -> video1.getSite().equals("YouTube")
                && video1.getType().equals("Trailer")).findFirst();
        return video.orElse(new Video());
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
