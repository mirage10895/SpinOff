package fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(tableName = "movie")
public class MovieDatabase {
    @PrimaryKey
    @NonNull
    private Integer id;
    private String title;
    @ColumnInfo(name = "poster_path")
    private String posterPath;
    private Integer runtime;
    @ColumnInfo(name = "watched")
    private boolean watched;
    @ColumnInfo(name = "last_synchronisation_time")
    private Instant lastSynchronisationTime;

    public MovieDatabase() {
        this.watched = false;
    }
}
