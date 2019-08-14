package fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "series")
public class SerieDatabase {
    @PrimaryKey
    @NonNull
    private Integer id;
    private String name;
    private String posterPath;
    private Integer seasonsNumber;

    public SerieDatabase() {
        this.id = -1;
    }

    @NonNull
    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public Integer getSeasonsNumber() {
        return seasonsNumber;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(@NonNull Integer id) {
        this.id = id;
    }

    public void setSeasonsNumber(Integer seasonsNumber) {
        this.seasonsNumber = seasonsNumber;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }
}
