package fr.eseo.dis.amiaudluc.spinoffapp.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by lucasamiaud on 28/02/2018.
 */

@Getter
@Setter
public class ProductionCompany {
    private String name;
    private int id;

    public ProductionCompany(){
        this.name = "";
        this.id = 0;
    }
}
