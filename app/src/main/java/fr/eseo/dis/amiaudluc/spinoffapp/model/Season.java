package fr.eseo.dis.amiaudluc.spinoffapp.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.DBInitializer.RoomTypeConverter;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * Created by lucasamiaud on 28/02/2018.
 */

@Entity(tableName = "seasons",foreignKeys = @ForeignKey(entity = Serie.class,
        parentColumns = "id",
        childColumns = "serie_id",
        onDelete = CASCADE))
public class Season {

    @ColumnInfo(name = "air_date")
    @TypeConverters(RoomTypeConverter.class)
    private Date airDate;
    @ColumnInfo(name = "episode_count")
    private int episodeCount;
    @PrimaryKey
    private int id;
    @ColumnInfo(name = "poster_path")
    private String posterPath;
    @ColumnInfo(name = "season_number")
    private int seasonNumber;
    @TypeConverters(RoomTypeConverter.class)
    private ArrayList<Episode> episodes;
    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "overview")
    private String overview;
    @ColumnInfo(name = "serie_id")
    private int serieId;
    @Ignore
    private ArrayList<Artist> cast;
    @Ignore
    private ArrayList<Video> videos;

    public Season(){
        this.airDate = new Date();
        this.episodeCount = 0;
        this.id = 0;
        this.posterPath = "";
        this.seasonNumber = 0;
    }

    public Date getAirDate() {
        return airDate;
    }

    public void setAirDate(Date airDate) {
        this.airDate = airDate;
    }

    public int getEpisodeCount() {
        return episodeCount;
    }

    public void setEpisodeCount(int episodeCount) {
        this.episodeCount = episodeCount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public int getSeasonNumber() {
        return seasonNumber;
    }

    public void setSeasonNumber(int seasonNumber) {
        this.seasonNumber = seasonNumber;
    }

    public ArrayList<Episode> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(ArrayList<Episode> episodes) {
        this.episodes = episodes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public ArrayList<Artist> getCast(){
        return this.cast;
    }

    public void setCast(ArrayList<Artist> cast){
        this.cast = cast;
    }

    public int getSerieId() {
        return serieId;
    }

    public void setSerieId(int serieId) {
        this.serieId = serieId;
    }

    public ArrayList<Video> getVideos() {
        return videos;
    }

    public void setVideos(ArrayList<Video> videos) {
        this.videos = videos;
    }

    public Episode getFutureEpisode(){
        final long now = System.currentTimeMillis();
        Optional<Episode> episode = this.episodes.stream().filter(episode1 -> episode1.getAirDate() != null
                && episode1.getAirDate().getTime() >= now).findFirst();
        return episode.orElse(new Episode());
    }

    public List<Episode> getOldEpisodes(){
        return this.getEpisodes().stream().filter(s -> s.getAirDate() != null)
                .filter(s -> fr.eseo.dis.amiaudluc.spinoffapp.utils.DateUtils.isDayBefore(s.getAirDate()))
                .collect(Collectors.toList());
    }

    public List<Episode> getFutureEpisodes(){
        return this.getEpisodes().stream()
                .filter(s -> s.getAirDate() != null
                        && !fr.eseo.dis.amiaudluc.spinoffapp.utils.DateUtils.isDayBefore(s.getAirDate()))
                .collect(Collectors.toList());
    }

    public Video getRightVideo(){
        Optional<Video> video = this.videos.stream().filter(video1 -> video1.getSite().equals("YouTube")
                && video1.getType().equals("Trailer")).findFirst();
        return video.orElse(new Video());
    }
}
