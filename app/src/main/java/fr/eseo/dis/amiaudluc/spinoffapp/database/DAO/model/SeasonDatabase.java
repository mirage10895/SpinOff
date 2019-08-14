package fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = "seasons",
        foreignKeys = @ForeignKey(entity = SerieDatabase.class,
                parentColumns = "id",
                childColumns = "serie_id",
                onDelete = CASCADE),
        indices = {@Index("serie_id")}
)
public class SeasonDatabase {
    @PrimaryKey
    @NonNull
    private Integer id;
    private String name;
    @ColumnInfo(name = "season_number")
    private Integer seasonNumber;
    @ColumnInfo(name = "poster_path")
    private String posterPath;
    @ColumnInfo(name = "serie_id")
    private Integer serieId;

    public SeasonDatabase() {
        this.id = 0;
        this.posterPath = "";
        this.seasonNumber = 0;
    }

    @NonNull
    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getSeasonNumber() {
        return seasonNumber;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public Integer getSerieId() {
        return serieId;
    }

    public void setId(@NonNull Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSeasonNumber(Integer seasonNumber) {
        this.seasonNumber = seasonNumber;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public void setSerieId(Integer serieId) {
        this.serieId = serieId;
    }
}
