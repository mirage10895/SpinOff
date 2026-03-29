package fr.eseo.dis.amiaudluc.spinoffapp.api.beans;

import java.util.List;
import java.util.Map;

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
    ) {}

    public record WatchProviderApiResponse(
            int id,
            Map<String, WatchProviders> results
    ) {}
}
