package com.flinters.aggregator.model;

/**
 * Immutable aggregate for a campaign: totals plus computed CTR and CPA.
 * CPA is null when conversions = 0.
 */
public record CampaignAggregate(
        String campaignId,
        long totalImpressions,
        long totalClicks,
        double totalSpend,
        long totalConversions,
        double ctr,
        Double cpa
) {
    /**
     * Creates a copy with updated CTR and CPA metrics.
     */
    public CampaignAggregate withMetrics(double ctr, Double cpa) {
        return new CampaignAggregate(
                campaignId,
                totalImpressions,
                totalClicks,
                totalSpend,
                totalConversions,
                ctr,
                cpa
        );
    }
}
