package com.flinters.aggregator.reporter;

import com.flinters.aggregator.model.CampaignAggregate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class Top10ReporterTest {

    private final Top10Reporter reporter = new Top10Reporter();

    @Test
    void writesTop10CtrSortedDescending(@TempDir Path outputDir) throws Exception {
        CampaignAggregate low = new CampaignAggregate("CMP_LOW", 1000, 10, 50.0, 5, 0.01, 10.0);
        CampaignAggregate high = new CampaignAggregate("CMP_HIGH", 1000, 50, 50.0, 5, 0.05, 10.0);
        CampaignAggregate mid = new CampaignAggregate("CMP_MID", 1000, 30, 50.0, 5, 0.03, 10.0);

        reporter.write(outputDir, List.of(low, high, mid));

        Path ctrFile = outputDir.resolve("top10_ctr.csv");
        assertThat(ctrFile).exists();
        List<String> lines = Files.readAllLines(ctrFile);
        assertThat(lines).hasSize(4); // header + 3 rows
        assertThat(lines.get(0)).contains("campaign_id", "CTR", "CPA");
        assertThat(lines.get(1)).startsWith("CMP_HIGH"); // highest CTR first
        assertThat(lines.get(2)).startsWith("CMP_MID");
        assertThat(lines.get(3)).startsWith("CMP_LOW");
    }

    @Test
    void writesTop10CpaSortedAscendingAndExcludesZeroConversions(@TempDir Path outputDir) throws Exception {
        CampaignAggregate zeroConv = new CampaignAggregate("CMP_ZERO", 1000, 50, 100.0, 0, 0.05, null);
        CampaignAggregate highCpa = new CampaignAggregate("CMP_HIGH", 1000, 50, 100.0, 5, 0.05, 20.0);
        CampaignAggregate lowCpa = new CampaignAggregate("CMP_LOW", 1000, 50, 50.0, 5, 0.05, 10.0);

        reporter.write(outputDir, List.of(zeroConv, highCpa, lowCpa));

        Path cpaFile = outputDir.resolve("top10_cpa.csv");
        assertThat(cpaFile).exists();
        List<String> lines = Files.readAllLines(cpaFile);
        assertThat(lines).hasSize(3); // header + 2 rows (zero conversions excluded)
        assertThat(lines.get(1)).startsWith("CMP_LOW"); // lowest CPA first
        assertThat(lines.get(2)).startsWith("CMP_HIGH");
        assertThat(lines.stream().noneMatch(l -> l.contains("CMP_ZERO"))).isTrue();
    }

    @Test
    void limitsTo10RowsEach(@TempDir Path outputDir) throws Exception {
        List<CampaignAggregate> many = new java.util.ArrayList<>();
        for (int i = 0; i < 15; i++) {
            many.add(new CampaignAggregate("CMP" + i, 1000, 50 + i, 50.0, 5, 0.05 + i * 0.001, 10.0));
        }

        reporter.write(outputDir, many);

        List<String> ctrLines = Files.readAllLines(outputDir.resolve("top10_ctr.csv"));
        assertThat(ctrLines).hasSize(11); // header + 10
        List<String> cpaLines = Files.readAllLines(outputDir.resolve("top10_cpa.csv"));
        assertThat(cpaLines).hasSize(11); // header + 10
    }

    @Test
    void formatsCtrWith4DecimalsAndCpaWith2Decimals(@TempDir Path outputDir) throws Exception {
        CampaignAggregate a = new CampaignAggregate("CMP001", 1000, 33, 66.666, 3, 0.033, 22.222);

        reporter.write(outputDir, List.of(a));

        String ctrLine = Files.readAllLines(outputDir.resolve("top10_ctr.csv")).get(1);
        assertThat(ctrLine).contains("0.0330"); // 4 decimals
        assertThat(ctrLine).contains("22.22");  // 2 decimals for CPA
    }
}
