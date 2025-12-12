package fr.eseo.dis.amiaudluc.spinoffapp.api.beans;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DateBounds {
    private LocalDate maximum;
    private LocalDate minimum;
}
