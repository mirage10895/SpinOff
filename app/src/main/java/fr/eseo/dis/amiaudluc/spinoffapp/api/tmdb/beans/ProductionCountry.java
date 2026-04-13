package fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by lucasamiaud on 28/02/2018.
 */

@Getter
@Setter
@NoArgsConstructor
public class ProductionCountry {
    @SerializedName("iso_3166_1")
    private String iso31661;
    private String name;
}
