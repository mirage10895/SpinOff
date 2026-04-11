package fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.serie;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SerieStats {
    private long totalMinutes;
    private int totalEpisodes;
    private int totalSeries;
    private int watchlistCount;
    private String topGenre = "";
    private List<Map.Entry<String, Integer>> top3Genres;
    private String topCombination = "";
    private int topYear;
    private List<Map.Entry<Integer, Integer>> top3Years;
}
