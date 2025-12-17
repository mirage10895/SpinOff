package fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model;

import androidx.room.ColumnInfo;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CalendarBean {
    @ColumnInfo(name = "name")
    private String episodeName;
    @ColumnInfo(name = "air_date")
    private LocalDate airDate;
    @ColumnInfo(name = "poster_path")
    private String seriePosterPath;
    @ColumnInfo(name = "watched")
    private boolean watched;
}
