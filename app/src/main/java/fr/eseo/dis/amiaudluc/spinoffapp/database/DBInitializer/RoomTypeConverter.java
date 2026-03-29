package fr.eseo.dis.amiaudluc.spinoffapp.database.DBInitializer;

import androidx.room.TypeConverter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

/**
 * Created by lucasamiaud on 10/03/2018.
 */

public class RoomTypeConverter {

    @TypeConverter
    public static LocalDate timestampToLocalDate(Long timestamp) {
        if (timestamp == null) {
            return null;
        }
        return Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    @TypeConverter
    public static Long localDateToTimestamp(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    @TypeConverter
    public static Instant timestampToInstant(Long timestamp) {
        if (timestamp == null) {
            return null;
        }
        return Instant.ofEpochMilli(timestamp);
    }

    @TypeConverter
    public static Long instantToTimestamp(Instant date) {
        if (date == null) {
            return null;
        }
        return date.toEpochMilli();
    }

}
