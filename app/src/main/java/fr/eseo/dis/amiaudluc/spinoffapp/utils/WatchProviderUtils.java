package fr.eseo.dis.amiaudluc.spinoffapp.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.util.List;
import java.util.stream.Collectors;

import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Media;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.WatchProvider;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.adapter.WatchProviderAdapterData;

public class WatchProviderUtils {
    private static final String STREMIO_LOGO_URL = "https://cdn.jsdelivr.net/gh/homarr-labs/dashboard-icons/webp/stremio.webp";
    private static final String STREMIO_DEEP_LINK = "stremio:///detail/%s/%s";
    private static final String STREMIO_MOVIE_LINK = "movie";
    private static final String STREMIO_SERIE_LINK = "series";

    private WatchProviderUtils() {
        // utility class
    }

    public static List<WatchProviderAdapterData> formatWatchProviders(
            WatchProvider.WatchProviderApiResponse watchProviderApiResponse,
            String mediaType,
            String imdbMediaId
    ) {
        WatchProvider.WatchProviders watchProviders = watchProviderApiResponse.results().get("FR");
        WatchProviderAdapterData stremioProviderAdapterData = stremioProvider(mediaType, imdbMediaId);
        if (watchProviders == null) {
            if (imdbMediaId == null) {
                return List.of();
            }
            return List.of(stremioProviderAdapterData);
        }
        // 1796 = Netflix with ads
        // 2100 = Amazon with ads
        if (watchProviders.flatrate() == null) {
            return List.of(stremioProviderAdapterData);
        }
        List<WatchProviderAdapterData> flatRates = watchProviders.flatrate()
                .stream()
                .filter(w -> !w.providerName().contains("Ads"))
                .map(w -> new WatchProviderAdapterData(
                        w.providerId(),
                        w.providerName(),
                        w.logoPath(),
                        watchProviders.link(),
                        null
                ))
                .collect(Collectors.toList());

        if (flatRates.size() >= 2) {
            return flatRates.stream()
                    .limit(2)
                    .collect(Collectors.toList());
        }
        flatRates.add(stremioProviderAdapterData);
        return flatRates;
    }

    public static void openProvider(Context context, WatchProviderAdapterData provider) {
        if (provider.externalUrl() == null) return;

        LogUtils.d("WATCH", "Opening provider " + provider.externalUrl());
        try {
            Uri uri = Uri.parse(provider.externalUrl());
            context.startActivity(new Intent(Intent.ACTION_VIEW, uri));
        } catch (Exception e) {
            LogUtils.e("WATCH", "Error while opening provider", e);
            String fallback = provider.fallbackPackageName();
            if (fallback != null) {
                launchPlayStore(context, fallback);
            }
        }
    }

    private static void launchPlayStore(Context context, String packageName) {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
        }
    }

    public static WatchProviderAdapterData stremioProvider(
            String mediaType,
            String tmdbMediaId
    ) {
        String stremioMediaType = mediaType.equals(Media.MOVIE) ? STREMIO_MOVIE_LINK : STREMIO_SERIE_LINK;
        return new WatchProviderAdapterData(
                -1,
                "Stremio",
                STREMIO_LOGO_URL,
                String.format(STREMIO_DEEP_LINK, stremioMediaType, tmdbMediaId),
                "com.stremio.one"
        );
    }
}
