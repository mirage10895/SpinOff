package fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(
        tableName = "serie_season",
        foreignKeys = {
                @ForeignKey(
                        entity = SerieDatabase.class,
                        parentColumns = "id",
                        childColumns = "serie_id",
                        onDelete = ForeignKey.CASCADE
                )
        },
        indices = {
                @Index("serie_id")
        },
        primaryKeys = {
                "serie_id", "season_number"
        }
)
public class SeasonDatabase {
    @NonNull
    @ColumnInfo(name = "serie_id")
    private Integer serieId;
    @NonNull
    @ColumnInfo(name = "season_number")
    private Integer seasonNumber;
    @ColumnInfo(name = "episode_count")
    private Integer episodeCount;
    @ColumnInfo(name = "watched")
    private boolean watched;
}
