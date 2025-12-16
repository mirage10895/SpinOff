package fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model;

import java.time.Instant;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "movies")
public class MovieDatabase {
    @PrimaryKey
    @NonNull
    private Integer id;
    private String title;
    @ColumnInfo(name = "poster_path")
    private String posterPath;
    private Integer runtime;
    private boolean watched;
    @ColumnInfo(name = "last_synchronisation_time")
    private Instant lastSynchronisationTime;

    public MovieDatabase() {
        this.watched = false;
    }

    @NonNull
    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public boolean isWatched() {
        return watched;
    }

    public Integer getRuntime() {
        return runtime;
    }

    public Instant getLastSynchronisationTime() {
        return lastSynchronisationTime;
    }

    public void setId(@NonNull Integer id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public void setWatched(boolean watched) {
        this.watched = watched;
    }

    public void setRuntime(Integer runtime) {
        this.runtime = runtime;
    }

    public void setLastSynchronisationTime(Instant lastSynchronisationTime) {
        this.lastSynchronisationTime = lastSynchronisationTime;
    }
}
