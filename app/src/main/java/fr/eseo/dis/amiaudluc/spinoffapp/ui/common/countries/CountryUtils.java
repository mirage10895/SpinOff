package fr.eseo.dis.amiaudluc.spinoffapp.ui.common.countries;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;

public class CountryUtils {
    public static List<Country> countries() {
        return Arrays.stream(Locale.getISOCountries())
                .map(code -> new Country(code, displayCountry(code)))
                .sorted(Comparator.comparing(Country::name))
                .collect(Collectors.toList());
    }

    public static String displayCountry(String isoCode) {
        return new Locale("", isoCode).getDisplayCountry();
    }

    public record Country(String code, String name) {
        @NonNull
        @Override
        public String toString() {
            return name;
        }
    }
}
