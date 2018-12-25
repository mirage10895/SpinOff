package fr.eseo.dis.amiaudluc.spinoffapp.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by lucasamiaud on 28/02/2018.
 */

@Entity
public class Network {

    @ColumnInfo(name = "name")
    private String name;
    @PrimaryKey
    private int id;
    @ColumnInfo(name = "logo_path")
    private String logoPath;
    @Ignore
    private Language originCountry;

    public Network(){
        this.name = "";
        this.id = 0;
        this.logoPath = "";
        this.originCountry = Language.unknown;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogoPath() {
        return logoPath;
    }

    public void setLogoPath(String logoPath) {
        this.logoPath = logoPath;
    }

    public Language getOriginCountry() {
        return originCountry;
    }

    public void setOriginCountry(Language originCountry) {
        this.originCountry = originCountry;
    }
}
