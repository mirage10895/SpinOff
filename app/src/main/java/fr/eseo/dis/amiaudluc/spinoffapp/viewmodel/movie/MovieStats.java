package fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.movie;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MovieStats {
    private int totalRuntime;
    private int totalMovies;
    private int watchlistCount;
    private String topGenre = "";
    private List<Map.Entry<String, Integer>> topGenres;
    private String topCombination = "";
    private String topYear;
    private List<Map.Entry<String, Integer>> topYears;
}
