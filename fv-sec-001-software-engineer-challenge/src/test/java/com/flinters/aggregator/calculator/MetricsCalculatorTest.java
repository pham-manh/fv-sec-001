package com.flinters.aggregator.calculator;

import com.flinters.aggregator.model.CampaignAggregate;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MetricsCalculatorTest {

    private final MetricsCalculator calculator = new MetricsCalculator();

    @Test
    void computesCtrCorrectly() {
        CampaignAggregate input = new CampaignAggregate("CMP001", 1000, 50, 100.0, 10, 0.0, null);
        CampaignAggregate result = calculator.computeMetrics(input);

        assertThat(result.ctr()).isEqualTo(0.05);
    }

    @Test
    void ctrIsZeroWhenImpressionsZero() {
        CampaignAggregate input = new CampaignAggregate("CMP001", 0, 0, 0.0, 0, 0.0, null);
        CampaignAggregate result = calculator.computeMetrics(input);

        assertThat(result.ctr()).isEqualTo(0.0);
    }

    @Test
    void computesCpaCorrectly() {
        CampaignAggregate input = new CampaignAggregate("CMP001", 1000, 50, 100.0, 10, 0.0, null);
        CampaignAggregate result = calculator.computeMetrics(input);

        assertThat(result.cpa()).isEqualTo(10.0);
    }

    @Test
    void cpaIsNullWhenConversionsZero() {
        CampaignAggregate input = new CampaignAggregate("CMP001", 1000, 50, 100.0, 0, 0.0, null);
        CampaignAggregate result = calculator.computeMetrics(input);

        assertThat(result.cpa()).isNull();
    }

    @Test
    void withMetricsProcessesAllAggregates() {
        CampaignAggregate a1 = new CampaignAggregate("CMP001", 100, 5, 50.0, 5, 0.0, null);
        CampaignAggregate a2 = new CampaignAggregate("CMP002", 0, 0, 0.0, 0, 0.0, null);
        List<CampaignAggregate> result = calculator.withMetrics(List.of(a1, a2));

        assertThat(result).hasSize(2);
        assertThat(result.get(0).ctr()).isEqualTo(0.05);
        assertThat(result.get(0).cpa()).isEqualTo(10.0);
        assertThat(result.get(1).ctr()).isEqualTo(0.0);
        assertThat(result.get(1).cpa()).isNull();
    }
}
