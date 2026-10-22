package fr.eseo.dis.amiaudluc.spinoffapp.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.time.LocalDate;
import java.util.List;

import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.SerieNotificationDatabase;

/**
 * Persistence of the release-tracking state of the series kept in the library.
 * <p>
 * Synchronous reads and writes are meant for the background synchronisation, observable reads
 * for the screens that will surface upcoming releases.
 */
@Dao
public interface SerieNotificationDAO {

    @Query("select * from serie_notification")
    LiveData<List<SerieNotificationDatabase>> getAll();

    @Query("select * from serie_notification where serie_id = :serieId")
    LiveData<SerieNotificationDatabase> get(int serieId);

    @Query("select * from serie_notification where serie_id = :serieId")
    SerieNotificationDatabase getSync(int serieId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsert(SerieNotificationDatabase notification);

    @Query("update serie_notification set enabled = :enabled where serie_id = :serieId")
    void setEnabled(int serieId, boolean enabled);

    /**
     * Upcoming and recent releases of enabled series, ordered by air date: the primary source of
     * the release calendar.
     */
    @Query("select * from serie_notification "
            + "where enabled = 1 and next_episode_air_date between :from and :to "
            + "order by next_episode_air_date")
    LiveData<List<SerieNotificationDatabase>> findReleasesBetween(LocalDate from, LocalDate to);

    @Query("delete from serie_notification where serie_id = :serieId")
    void deleteBySerie(int serieId);

    /**
     * Drop the tracking state of the series that left the library so no orphan row survives a
     * backup import or a manual deletion.
     */
    @Query("delete from serie_notification where serie_id not in (select id from serie)")
    void deleteOrphans();

    @Query("delete from serie_notification where next_episode_air_date is not null "
            + "and next_episode_air_date < :threshold")
    void clearReleasesOlderThan(LocalDate threshold);
}
