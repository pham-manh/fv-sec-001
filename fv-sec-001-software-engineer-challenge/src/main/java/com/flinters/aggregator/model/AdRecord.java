package com.flinters.aggregator.model;

/**
 * Immutable record representing a single parsed row from the input CSV.
 */
public record AdRecord(
        String campaignId,
        String date,
        long impressions,
        long clicks,
        double spend,
        long conversions
) {
}
