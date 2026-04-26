package fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.discovery.beans;

import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.DiscoverFilters;

public record DiscoveryFilter(DiscoveryType type, DiscoverFilters filters) {
    public static DiscoveryFilter defaultFilter() {
        return new DiscoveryFilter(DiscoveryType.POPULAR, DiscoverFilters.builder().build());
    }
}
