package fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiListResponse<T> {
    private Integer page;
    private List<T> results;
    private Integer totalPages;
    private Integer totalResults;
}
