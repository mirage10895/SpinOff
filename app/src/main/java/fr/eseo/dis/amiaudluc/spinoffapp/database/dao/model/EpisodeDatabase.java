package fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(
        tableName = "serie_episode",
        foreignKeys = {
                @ForeignKey(
                        entity = fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.MovieDatabase.class,
                        parentColumns = "id",
                        childColumns = "serie_id"
                )
        },
        primaryKeys = {
                "serie_id", "season_number", "number"
        }
)
public class EpisodeDatabase {
    @NonNull
    @ColumnInfo(name = "serie_id")
    private Integer serieId;
    @NonNull
    @ColumnInfo(name = "season_number")
    private Integer seasonNumber;
    @ColumnInfo(name = "number")
    private boolean number;
    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "air_date")
    private LocalDate airDate;
}
