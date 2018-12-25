package fr.eseo.dis.amiaudluc.spinoffapp.model;

/**
 * Created by lucasamiaud on 28/02/2018.
 */

public class ProductionCountry {

    private String iso_3166_1;
    private String name;

    public ProductionCountry(){
        this.iso_3166_1="";
        this.name="";
    }

    public String getName() {
        return name;
    }

    public String getIso_3166_1() {
        return iso_3166_1;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIso_3166_1(String iso_3166_1) {
        this.iso_3166_1 = iso_3166_1;
    }
}
