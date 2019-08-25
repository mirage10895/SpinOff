package fr.eseo.dis.amiaudluc.spinoffapp.database.DAO;

import android.arch.persistence.room.ColumnInfo;

import java.util.Date;

public class CalendarBean {
    @ColumnInfo(name = "name")
    private String episodeName;
    @ColumnInfo(name = "air_date")
    private Date airDate;
    @ColumnInfo(name = "poster_path")
    private String seriePosterPath;
    @ColumnInfo(name = "watched")
    private Boolean isWatched;

    public String getEpisodeName() {
        return episodeName;
    }

    public void setEpisodeName(String episodeName) {
        this.episodeName = episodeName;
    }

    public Date getAirDate() {
        return airDate;
    }

    public void setAirDate(Date airDate) {
        this.airDate = airDate;
    }

    public String getSeriePosterPath() {
        return seriePosterPath;
    }

    public void setSeriePosterPath(String seriePosterPath) {
        this.seriePosterPath = seriePosterPath;
    }

    public Boolean getWatched() {
        return isWatched;
    }

    public void setWatched(Boolean watched) {
        isWatched = watched;
    }
}
