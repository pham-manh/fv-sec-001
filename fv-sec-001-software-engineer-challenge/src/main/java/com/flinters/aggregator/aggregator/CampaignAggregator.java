package com.flinters.aggregator.aggregator;

import com.flinters.aggregator.model.AdRecord;
import com.flinters.aggregator.model.CampaignAggregate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Aggregates AdRecords by campaign_id. Produces CampaignAggregate with raw totals.
 * CTR and CPA are left as 0 and null; use MetricsCalculator to compute them.
 */
public class CampaignAggregator {

    public List<CampaignAggregate> aggregate(Stream<AdRecord> records) {
        Map<String, MutableTotals> totalsByCampaign = new HashMap<>();

        records.forEach(record -> totalsByCampaign
                .computeIfAbsent(record.campaignId(), k -> new MutableTotals(record.campaignId()))
                .add(record));

        List<CampaignAggregate> result = new ArrayList<>();
        for (MutableTotals t : totalsByCampaign.values()) {
            result.add(new CampaignAggregate(
                    t.campaignId,
                    t.impressions,
                    t.clicks,
                    t.spend,
                    t.conversions,
                    0.0,
                    null
            ));
        }
        return result;
    }

    private static class MutableTotals {
        final String campaignId;
        long impressions;
        long clicks;
        double spend;
        long conversions;

        MutableTotals(String campaignId) {
            this.campaignId = campaignId;
        }

        void add(AdRecord r) {
            impressions += r.impressions();
            clicks += r.clicks();
            spend += r.spend();
            conversions += r.conversions();
        }
    }
}
