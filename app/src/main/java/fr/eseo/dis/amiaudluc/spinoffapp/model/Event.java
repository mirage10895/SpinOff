package fr.eseo.dis.amiaudluc.spinoffapp.model;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by lucasamiaud on 16/03/2018.
 */

@Getter
@Setter
public class Event {
    private LocalDate date;
    private String posterPath;
    private String name;
    private Boolean watched;

    public Event(LocalDate date, String posterPath, String name, Boolean watched){
        this.date = date;
        this.posterPath = posterPath;
        this.name = name;
        this.watched = watched;
    }
}
