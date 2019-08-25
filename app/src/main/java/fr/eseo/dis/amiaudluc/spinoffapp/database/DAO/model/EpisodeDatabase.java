package fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = "episodes",
        foreignKeys = @ForeignKey(entity = SeasonDatabase.class,
                parentColumns = "id",
                childColumns = "season_id",
                onDelete = CASCADE),
        indices = {@Index("season_id")}
)
public class EpisodeDatabase {
    @PrimaryKey
    @NonNull
    private Integer id;
    private String name;
    @ColumnInfo(name = "air_date")
    private Date airDate;
    @ColumnInfo(name = "season_id")
    private Integer seasonId;
    private Boolean watched;

    public EpisodeDatabase () {
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

    public Date getAirDate() {
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

    public void setAirDate(Date airDate) {
        this.airDate = airDate;
    }

    public void setWatched(Boolean watched) {
        this.watched = watched;
    }
}
