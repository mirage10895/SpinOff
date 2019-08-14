package fr.eseo.dis.amiaudluc.spinoffapp.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by lucasamiaud on 28/02/2018.
 */

@Getter
@Setter
public class Network {
    private String name;
    private int id;
    private String logoPath;
    private Language originCountry;

    public Network(){
        this.name = "";
        this.id = 0;
        this.logoPath = "";
        this.originCountry = Language.DEFAULT;
    }
}
