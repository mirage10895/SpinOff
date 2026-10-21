package fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model;

import androidx.room.ColumnInfo;

public class SeasonCount {
    @ColumnInfo(name = "season_number")
    public int seasonNumber;
    
    @ColumnInfo(name = "watched_count")
    public int watchedCount;
}
