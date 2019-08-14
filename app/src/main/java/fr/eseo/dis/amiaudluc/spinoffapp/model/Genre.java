package fr.eseo.dis.amiaudluc.spinoffapp.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by lucasamiaud on 28/02/2018.
 */

@Getter
@Setter
public class Genre {

    private int id;
    private String name;

    public Genre(){
        this.id = 0;
        this.name = "";
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
