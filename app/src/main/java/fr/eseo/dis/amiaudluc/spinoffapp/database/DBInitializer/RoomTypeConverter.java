package fr.eseo.dis.amiaudluc.spinoffapp.database.DBInitializer;

import androidx.room.TypeConverter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoField;

/**
 * Created by lucasamiaud on 10/03/2018.
 */

public class RoomTypeConverter {

    @TypeConverter
    public static LocalDate toDate(Long timestamp) {
        LocalDate ldt;
        if (timestamp == null) {
            return null;
        } else {
            ldt = Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDate();
        }
        return ldt;
    }

    @TypeConverter
    public static Long toTimestamp(LocalDate date) {
        if (date == null) {
            return null;
        } else {
            return date.getLong(ChronoField.DAY_OF_MONTH);
        }
    }

}
