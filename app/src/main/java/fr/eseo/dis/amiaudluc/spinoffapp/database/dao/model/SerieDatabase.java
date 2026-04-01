package fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.Instant;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(tableName = "serie")
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
    private String genres;
    @ColumnInfo(name = "season_count")
    private Integer seasonCount;
    @ColumnInfo(name = "episode_count")
    private Integer episodeCount;
    @ColumnInfo(name = "first_air_date")
    private LocalDate firstAirDate;
}
