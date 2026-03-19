package com.flinters.aggregator.aggregator;

import com.flinters.aggregator.model.AdRecord;
import com.flinters.aggregator.model.CampaignAggregate;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class CampaignAggregatorTest {

    private final CampaignAggregator aggregator = new CampaignAggregator();

    @Test
    void aggregatesSingleRecord() {
        AdRecord record = new AdRecord("CMP001", "2025-01-01", 1000, 50, 25.50, 5);
        List<CampaignAggregate> result = aggregator.aggregate(Stream.of(record));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).campaignId()).isEqualTo("CMP001");
        assertThat(result.get(0).totalImpressions()).isEqualTo(1000);
        assertThat(result.get(0).totalClicks()).isEqualTo(50);
        assertThat(result.get(0).totalSpend()).isEqualTo(25.50);
        assertThat(result.get(0).totalConversions()).isEqualTo(5);
    }

    @Test
    void aggregatesMultipleRowsSameCampaign() {
        AdRecord r1 = new AdRecord("CMP001", "2025-01-01", 1000, 50, 25.0, 5);
        AdRecord r2 = new AdRecord("CMP001", "2025-01-02", 2000, 100, 50.0, 10);
        List<CampaignAggregate> result = aggregator.aggregate(Stream.of(r1, r2));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).totalImpressions()).isEqualTo(3000);
        assertThat(result.get(0).totalClicks()).isEqualTo(150);
        assertThat(result.get(0).totalSpend()).isEqualTo(75.0);
        assertThat(result.get(0).totalConversions()).isEqualTo(15);
    }

    @Test
    void aggregatesMultipleCampaigns() {
        AdRecord r1 = new AdRecord("CMP001", "2025-01-01", 1000, 50, 25.0, 5);
        AdRecord r2 = new AdRecord("CMP002", "2025-01-01", 500, 25, 12.5, 2);
        AdRecord r3 = new AdRecord("CMP001", "2025-01-02", 500, 25, 12.5, 2);
        List<CampaignAggregate> result = aggregator.aggregate(Stream.of(r1, r2, r3));

        assertThat(result).hasSize(2);

        CampaignAggregate cmp001 = result.stream().filter(a -> a.campaignId().equals("CMP001")).findFirst().orElseThrow();
        assertThat(cmp001.totalImpressions()).isEqualTo(1500);
        assertThat(cmp001.totalClicks()).isEqualTo(75);
        assertThat(cmp001.totalSpend()).isEqualTo(37.5);
        assertThat(cmp001.totalConversions()).isEqualTo(7);

        CampaignAggregate cmp002 = result.stream().filter(a -> a.campaignId().equals("CMP002")).findFirst().orElseThrow();
        assertThat(cmp002.totalImpressions()).isEqualTo(500);
        assertThat(cmp002.totalClicks()).isEqualTo(25);
        assertThat(cmp002.totalSpend()).isEqualTo(12.5);
        assertThat(cmp002.totalConversions()).isEqualTo(2);
    }

    @Test
    void emptyStreamReturnsEmptyList() {
        List<CampaignAggregate> result = aggregator.aggregate(Stream.empty());
        assertThat(result).isEmpty();
    }
}
