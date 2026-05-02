package fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.discovery.strategy;

import androidx.annotation.StringRes;

public record RuntimeFilter(
        @StringRes int labelResId,
        Integer min,
        Integer max
) {}
