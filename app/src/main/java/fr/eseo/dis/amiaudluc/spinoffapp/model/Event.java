package fr.eseo.dis.amiaudluc.spinoffapp.model;

import java.util.Date;

/**
 * Created by lucasamiaud on 16/03/2018.
 */

public class Event {

    private Date date;
    private Episode episode;

    public Event(Date date,Episode episode){
        this.date = date;
        this.episode = episode;
    }

    public Date getDate() {
        return date;
    }

    public Episode getEpisode() {
        return episode;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setEpisode (Episode episode) {
        this.episode = episode;
    }
}
