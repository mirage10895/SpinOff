package fr.eseo.dis.amiaudluc.spinoffapp.model;

/**
 * Created by lucasamiaud on 28/02/2018.
 */

public class Genre {

    private int id;
    private String name;

    public Genre(){
        this.id = 0;
        this.name = "";
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
