package fr.eseo.dis.amiaudluc.spinoffapp.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by lucasamiaud on 28/02/2018.
 */

@Getter
@Setter
public class ProductionCountry {

    private String iso_3166_1;
    private String name;

    public ProductionCountry(){
        this.iso_3166_1 = "";
        this.name = "";
    }
}
