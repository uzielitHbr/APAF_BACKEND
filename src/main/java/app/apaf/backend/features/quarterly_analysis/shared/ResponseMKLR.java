package app.apaf.backend.features.quarterly_analysis.shared;

import java.util.List;

public record ResponseMKLR<M, K, D, R>(
        M meta,
        K kpis,
        List<D> data,
        R resumenTotal) {
}
