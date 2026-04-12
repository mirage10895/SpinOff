package fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.adapter.WatchProviderAdapterData;

public record WatchProvider(
        String logoPath,
        int providerId,
        String providerName,
        String displayPriority
) {
    public record WatchProviders(
            String link,
            List<WatchProvider> buy,
            List<WatchProvider> flatrate,
            List<WatchProvider> rent
    ) {
    }

    public record WatchProviderApiResponse(
            int id,
            Map<String, WatchProviders> results
    ) {
    }

    public static List<WatchProviderAdapterData> filterOutWatchProviders(WatchProviderApiResponse watchProviderApiResponse) {
        WatchProviders watchProviders = watchProviderApiResponse.results().get("FR");
        if (watchProviders == null) {
            return List.of();
        }
        // 1796 = Netflix with ads
        // 2100 = Amazon with ads
        if (watchProviders.flatrate() == null) {
            return List.of();
        }
        return watchProviders.flatrate()
                .stream()
                .filter(w -> !w.providerName.contains("Ads"))
                .map(w -> new WatchProviderAdapterData(
                        w.providerId,
                        w.providerName,
                        w.logoPath,
                        watchProviders.link()
                ))
                .collect(Collectors.toList());
    }
}
