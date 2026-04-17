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
    private int totalSeasons;
    private int totalSeries;
    private int watchlistCount;
    private String topGenre = "";
    private List<Map.Entry<String, Integer>> topGenres;
    private String topCombination = "";
    private String topDecade = "";
    private List<Map.Entry<String, Integer>> topDecades;
    private double meanRating;
    private double medianRating;
    private List<Map.Entry<String, Integer>> topActors;
    private List<Map.Entry<String, Integer>> topDirectors;
    private Map.Entry<String, Integer> topActorNetwork;
    private String topOrigin = "";
    private List<Map.Entry<String, Integer>> topOrigins;
    private String topLanguage = "";
    private List<Map.Entry<String, Integer>> topLanguages;
}
