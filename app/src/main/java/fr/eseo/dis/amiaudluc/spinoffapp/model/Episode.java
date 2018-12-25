package fr.eseo.dis.amiaudluc.spinoffapp.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Date;

import fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.DBInitializer.RoomTypeConverter;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * Created by lucasamiaud on 08/03/2018.
 */

@Entity(tableName = "episodes",foreignKeys = @ForeignKey(entity = Season.class,
        parentColumns = "id",
        childColumns = "season_id",
        onDelete = CASCADE))
public class Episode {

    @TypeConverters(RoomTypeConverter.class)
    @ColumnInfo(name = "air_date")
    private Date airDate;
    @Ignore
    private ArrayList<Artist> crew;
    @ColumnInfo(name = "episode_number")
    private int episodeNumber;
    @Ignore
    private ArrayList<Artist> guestStars;
    @ColumnInfo(name = "overview")
    private String overview;
    @ColumnInfo(name = "name")
    private String name;
    @PrimaryKey
    private int id;
    @ColumnInfo(name = "production_code")
    @Nullable private String productionCode;
    @ColumnInfo(name = "season_number")
    private int seasonNumber;
    @ColumnInfo(name = "still_path")
    @Nullable private String stillPath;
    @ColumnInfo(name = "vote_avg")
    private double voteAvg;
    @ColumnInfo(name = "vote_count")
    private int voteCount;
    @ColumnInfo(name ="season_id")
    private int idSeason;

    public Episode(){

    }

    public Date getAirDate() {
        return airDate;
    }

    public void setAirDate(Date airDate) {
        this.airDate = airDate;
    }

    public ArrayList<Artist> getCrew() {
        return crew;
    }

    public void setCrew(ArrayList<Artist> crew) {
        this.crew = crew;
    }

    public int getEpisodeNumber() {
        return episodeNumber;
    }

    public void setEpisodeNumber(int episodeNumber) {
        this.episodeNumber = episodeNumber;
    }

    public ArrayList<Artist> getGuestStars() {
        return guestStars;
    }

    public void setGuestStars(ArrayList<Artist> guestStars) {
        this.guestStars = guestStars;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Nullable
    public String getProductionCode() {
        return productionCode;
    }

    public void setProductionCode(@Nullable String productionCode) {
        this.productionCode = productionCode;
    }

    public int getSeasonNumber() {
        return seasonNumber;
    }

    public void setSeasonNumber(int seasonNumber) {
        this.seasonNumber = seasonNumber;
    }

    @Nullable
    public String getStillPath() {
        return stillPath;
    }

    public void setStillPath(@Nullable String stillPath) {
        this.stillPath = stillPath;
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

    public int getIdSeason() {
        return idSeason;
    }

    public void setIdSeason(int idSeason) {
        this.idSeason = idSeason;
    }

    @Override
    public String toString() {

        return "S" + this.getSeasonNumber() +
                "E" + this.getEpisodeNumber() +
                ": " + this.getName();
    }
}
