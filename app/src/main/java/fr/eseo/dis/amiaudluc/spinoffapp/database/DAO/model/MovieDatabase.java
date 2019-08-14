package fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "movies")
public class MovieDatabase {
    @PrimaryKey
    @NonNull
    private Integer id;
    private String title;
    private String posterPath;
    private boolean watched;

    public MovieDatabase() {
        this.id = -1;
    }

    @NonNull
    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public boolean isWatched() {
        return watched;
    }

    public void setId(@NonNull Integer id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public void setWatched(boolean watched) {
        this.watched = watched;
    }
}
