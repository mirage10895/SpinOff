package fr.eseo.dis.amiaudluc.spinoffapp.utils;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StatUtils {
    private StatUtils() {
        // unused
    }

    /**
     * Rework stats so that only values above the threshold are kept.
     * The threshold is computed dynamically based on the number of entries.
     * Values below the threshold are put in an "others" entry
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
}
