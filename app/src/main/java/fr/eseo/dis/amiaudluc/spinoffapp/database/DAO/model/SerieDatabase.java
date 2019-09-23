package fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;

@Entity(tableName = "series")
public class SerieDatabase {
    @PrimaryKey
    @NonNull
    private Integer id;
    private String name;
    @ColumnInfo(name = "poster_path")
    private String posterPath;
    @ColumnInfo(name = "seasons_number")
    private Integer seasonsNumber;
    @ColumnInfo(name = "average_episode_run_time")
    private Double averageEpisodeRunTime;
    @ColumnInfo(name = "number_of_episodes")
    private Integer numberOfEpisodes;

    public SerieDatabase() {
        this.id = -1;
    }

    @NonNull
    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public Integer getSeasonsNumber() {
        return seasonsNumber;
    }

    public Double getAverageEpisodeRunTime() {
        return averageEpisodeRunTime;
    }

    public Integer getNumberOfEpisodes() {
        return numberOfEpisodes;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(@NonNull Integer id) {
        this.id = id;
    }

    public void setSeasonsNumber(Integer seasonsNumber) {
        this.seasonsNumber = seasonsNumber;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public void setAverageEpisodeRunTime(Double averageEpisodeRunTime) {
        this.averageEpisodeRunTime = averageEpisodeRunTime;
    }

    public void setNumberOfEpisodes(Integer numberOfEpisodes) {
        this.numberOfEpisodes = numberOfEpisodes;
    }

    public Double getTotalRunTime() {
        return this.averageEpisodeRunTime * this.numberOfEpisodes;
    }
}
