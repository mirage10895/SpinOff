package fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.DBInitializer;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.eseo.dis.amiaudluc.spinoffapp.model.Episode;
import fr.eseo.dis.amiaudluc.spinoffapp.model.ProductionCompany;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Season;

/**
 * Created by lucasamiaud on 10/03/2018.
 */

public class RoomTypeConverter {

    private static Gson gson = new Gson();

    @TypeConverter
    public static ArrayList<Season> stringToSeasonList(String data) {
        if (data == null) {
            return new ArrayList<>();
        }
        Type listType = new TypeToken<ArrayList<Season>>() {}.getType();
        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String seasonToString(ArrayList<Season> someObjects) {
        return gson.toJson(someObjects);
    }

    @TypeConverter
    public static ArrayList<ProductionCompany> stringToProductionList(String data) {
        if (data == null) {
            return new ArrayList<>();
        }
        Type listType = new TypeToken<List<ProductionCompany>>() {}.getType();

        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String productionToString(ArrayList<ProductionCompany> someObjects) {
        return gson.toJson(someObjects);
    }

    @TypeConverter
    public static ArrayList<Episode> stringToEpisodeList(String data) {
        if (data == null) {
            return new ArrayList<>();
        }
        Type listType = new TypeToken<ArrayList<Episode>>() {}.getType();

        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String episodeToString(ArrayList<Episode> someObjects) {
        return gson.toJson(someObjects);
    }

    @TypeConverter
    public static Date toDate(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long toLong(Date value) {
        return value == null ? null : value.getTime();
    }

}
