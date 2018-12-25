package fr.eseo.dis.amiaudluc.spinoffapp.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;

/**
 * Created by lucasamiaud on 28/02/2018.
 */

@Entity
public class ProductionCompany {

    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "id")
    private int id;

    public ProductionCompany(){
        this.name="";
        this.id=0;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }
}
