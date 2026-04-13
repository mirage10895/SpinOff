package fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.discovery.beans;

import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Media;

public record DiscoveryMedia<T extends Media>(
        T media,
        boolean isInLibrary,
        boolean isWatched
) {
}
