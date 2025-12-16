package fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model;

import java.time.Instant;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "series")
public class SerieDatabase {
    @PrimaryKey
    @NonNull
    private Integer id;
    private String name;
    @ColumnInfo(name = "poster_path")
    private String posterPath;
    @ColumnInfo(name = "runtime")
    private int runtime;
    @ColumnInfo(name = "watched")
    private boolean watched;
    @ColumnInfo(name = "last_synchronisation_time")
    private Instant lastSynchronisationTime;

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

    public int getRuntime() {
        return runtime;
    }

    public boolean isWatched() {
        return watched;
    }

    public Instant getLastSynchronisationTime() {
        return lastSynchronisationTime;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(@NonNull Integer id) {
        this.id = id;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    public void setWatched(boolean watched) {
        this.watched = watched;
    }

    public void setLastSynchronisationTime(Instant lastSynchronisationTime) {
        this.lastSynchronisationTime = lastSynchronisationTime;
    }
}
