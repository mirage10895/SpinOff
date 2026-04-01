package fr.eseo.dis.amiaudluc.spinoffapp.api.beans;

import java.util.List;

import fr.eseo.dis.amiaudluc.spinoffapp.api.beans.DateBounds;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiListResponse<T> {
    private Integer page;
    private List<T> results;
    private DateBounds dates;
    private Integer totalPages;
    private Integer totalResults;
}
