package fr.eseo.dis.amiaudluc.spinoffapp.model;

/**
 * Created by lucasamiaud on 28/02/2018.
 */

public class Realisator extends Artist{

    private int id;
    private String name;
    private int gender;
    private String profilePath;

    public Realisator() {
        super();
        this.gender = 0;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }
}
