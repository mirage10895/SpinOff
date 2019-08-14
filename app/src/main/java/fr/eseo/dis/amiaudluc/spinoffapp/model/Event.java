package fr.eseo.dis.amiaudluc.spinoffapp.model;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by lucasamiaud on 16/03/2018.
 */

@Getter
@Setter
public class Event {
    private Date date;
    private Episode episode;

    public Event(Date date, Episode episode){
        this.date = date;
        this.episode = episode;
    }
}
