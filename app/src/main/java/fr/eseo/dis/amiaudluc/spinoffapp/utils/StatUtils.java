package fr.eseo.dis.amiaudluc.spinoffapp.utils;

import android.util.Pair;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class StatUtils {
    private StatUtils() {
        // unused
    }

    /**
     * Rework stats so that only values above the threshold are kept.
     * The threshold is computed dynamically based on the number of entries.
     * Values below the threshold are put in an "others" entry
     *
     * @param stats the computed stats
     */
    public static List<Map.Entry<String, Integer>> topNWithOther(
            Map<String, Integer> stats,
            int maxN
    ) {
        List<Map.Entry<String, Integer>> sorted = stats.entrySet()
                .stream()
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .collect(Collectors.toList());

        int total = sorted.stream().mapToInt(Map.Entry::getValue).sum();

        List<Map.Entry<String, Integer>> result = new ArrayList<>();
        int otherSum = 0;

        for (int i = 0; i < sorted.size(); i++) {
            double percentage = (double) sorted.get(i).getValue() / total;

            if (i < maxN && percentage >= 0.03) { // keep if ≥ 3%
                result.add(sorted.get(i));
            } else {
                otherSum += sorted.get(i).getValue();
            }
        }

        if (otherSum > 0) {
            result.add(new AbstractMap.SimpleEntry<>("Other", otherSum));
        }

        return result;
    }

    public static double computeMedian(List<Double> ratings) {
        return ratings.size() % 2 == 0
                ? (ratings.get(ratings.size() / 2) + ratings.get(ratings.size() / 2 - 1)) / 2
                : ratings.get(ratings.size() / 2);
    }

    public static Set<String> extractTrimmedValues(String commaSeparatedString) {
        if (commaSeparatedString == null || commaSeparatedString.isEmpty()) {
            return Set.of();
        }
        String[] values = commaSeparatedString.split(",");
        return Set.of(
                Optional.ofNullable(values[0])
                        .map(String::trim)
                        .orElse("")
        );
    }

    public static List<Pair<String, String>> extractPairs(Set<String> values) {
        if (values == null || values.isEmpty()) {
            return List.of();
        }
        List<Pair<String, String>> result = new ArrayList<>();
        values.forEach(first -> values.forEach(second -> {
            if (!first.equals(second)) {
                result.add(new Pair<>(first, second));
            }
        }));
        return result;
    }

    public static void fillMapCount(Map<String, Integer> previousValue, Set<String> values) {
        if (values == null || values.isEmpty()) {
            return;
        }
        for (String value : values) {
            String trim = value.trim();
            int orDefault = Optional.ofNullable(previousValue.getOrDefault(trim, 0)).orElse(0);
            previousValue.put(trim, orDefault + 1);
        }
    }
}
