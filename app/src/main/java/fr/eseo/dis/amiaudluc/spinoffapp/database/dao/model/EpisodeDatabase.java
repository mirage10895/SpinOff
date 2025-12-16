package fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

import java.time.LocalDate;

@Entity(tableName = "episodes",
        indices = {@Index("season_id")}
)
public class EpisodeDatabase {
    @PrimaryKey
    @NonNull
    private Integer id;
    private String name;
    @ColumnInfo(name = "air_date")
    private LocalDate airDate;
    @ColumnInfo(name = "season_id")
    private Integer seasonId;
    private boolean watched;

    public EpisodeDatabase() {
        this.id = -1;
        this.watched = false;
    }

    @NonNull
    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDate getAirDate() {
        return airDate;
    }

    public Integer getSeasonId() {
        return seasonId;
    }

    public Boolean isWatched() {
        return this.watched;
    }

    public void setId(@NonNull Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSeasonId(Integer seasonId) {
        this.seasonId = seasonId;
    }

    public void setAirDate(LocalDate airDate) {
        this.airDate = airDate;
    }

    public void setWatched(boolean watched) {
        this.watched = watched;
    }
}
