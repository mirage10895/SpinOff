package fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.DBOptimizer;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import java.util.List;

import fr.eseo.dis.amiaudluc.spinoffapp.model.Season;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Serie;

/**
 * Created by lucasamiaud on 11/04/2018.
 */

public class SerieWithSeasons {

    @Embedded
    public Serie serie;

    @Relation(parentColumn = "id",
            entityColumn = "serie_id")
    public List<Season> seasonList;

}
