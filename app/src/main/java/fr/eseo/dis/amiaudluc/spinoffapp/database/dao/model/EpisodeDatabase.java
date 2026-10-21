package fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import lombok.Getter;
import lombok.Setter;

/**
 * Link table storing only the episodes that have been watched.
 * A row present means the episode is watched; there is no row for unwatched episodes.
 */
@Getter
@Setter
@Entity(
        tableName = "serie_episode",
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
                "serie_id", "season_number", "episode_number"
        }
)
public class EpisodeDatabase {
    @NonNull
    @ColumnInfo(name = "serie_id")
    private Integer serieId;
    @NonNull
    @ColumnInfo(name = "season_number")
    private Integer seasonNumber;
    @NonNull
    @ColumnInfo(name = "episode_number")
    private Integer episodeNumber;
}
