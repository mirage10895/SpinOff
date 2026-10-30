package fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.time.Instant;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

/**
 * Release-tracking state of a serie kept in the library, decoupled from the library tables.
 * <p>
 * {@link SerieDatabase} answers "what the user owns"; this entity answers "what the app has
 * already seen and notified about". It stores the TMDB snapshot of the last synchronisation so
 * the release detector can compare it with the fresh API payload: the last known status, the
 * last seen episode and season counts. It also keeps the next announced episode, which is the
 * future calendar's primary source, and the last notification time used as a throttle.
 */
@Getter
@Setter
@Entity(
        tableName = "serie_notification",
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
        }
)
public class SerieNotificationDatabase {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "serie_id")
    private Integer serieId;
    @ColumnInfo(name = "enabled", defaultValue = "1")
    private boolean enabled = true;
    @ColumnInfo(name = "last_known_status")
    private String lastKnownStatus;
    @ColumnInfo(name = "last_known_season_count")
    private Integer lastKnownSeasonCount;
    @ColumnInfo(name = "last_seen_season")
    private Integer lastSeenSeason;
    @ColumnInfo(name = "last_seen_episode")
    private Integer lastSeenEpisode;
    @ColumnInfo(name = "last_seen_air_date")
    private LocalDate lastSeenAirDate;
    @ColumnInfo(name = "notified_at")
    private Instant notifiedAt;
    @ColumnInfo(name = "next_episode_season")
    private Integer nextEpisodeSeason;
    @ColumnInfo(name = "next_episode_number")
    private Integer nextEpisodeNumber;
    @ColumnInfo(name = "next_episode_air_date")
    private LocalDate nextEpisodeAirDate;
    @ColumnInfo(name = "next_episode_name")
    private String nextEpisodeName;
}
