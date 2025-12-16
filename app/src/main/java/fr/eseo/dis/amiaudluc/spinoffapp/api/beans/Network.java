package fr.eseo.dis.amiaudluc.spinoffapp.api.beans;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by lucasamiaud on 28/02/2018.
 */

@Getter
@Setter
@NoArgsConstructor
public class Network {
    private String name;
    private int id;
    private String logoPath;
    private Language originCountry;
}
