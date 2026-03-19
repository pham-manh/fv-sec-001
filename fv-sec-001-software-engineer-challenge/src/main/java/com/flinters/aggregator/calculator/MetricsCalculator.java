package com.flinters.aggregator.calculator;

import com.flinters.aggregator.model.CampaignAggregate;

import java.util.List;

/**
 * Computes CTR and CPA for campaign aggregates.
 * CTR = total_clicks / total_impressions (0 if impressions = 0)
 * CPA = total_spend / total_conversions (null if conversions = 0)
 */
public class MetricsCalculator {

    public List<CampaignAggregate> withMetrics(List<CampaignAggregate> aggregates) {
        return aggregates.stream()
                .map(this::computeMetrics)
                .toList();
    }

    public CampaignAggregate computeMetrics(CampaignAggregate a) {
        double ctr = a.totalImpressions() == 0
                ? 0.0
                : (double) a.totalClicks() / a.totalImpressions();

        Double cpa = a.totalConversions() == 0
                ? null
                : a.totalSpend() / a.totalConversions();

        return a.withMetrics(ctr, cpa);
    }
}
