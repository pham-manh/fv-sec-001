package com.flinters.aggregator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AggregatorAppIntegrationTest {

    @Test
    void endToEndProducesCorrectOutputFiles(@TempDir Path tempDir) throws Exception {
        Path inputFile = tempDir.resolve("input.csv");
        Files.writeString(inputFile, """
                campaign_id,date,impressions,clicks,spend,conversions
                CMP042,2025-01-01,62500,3125,6250.25,312
                CMP042,2025-01-02,62500,3125,6250.25,313
                CMP015,2025-01-01,170000,7650,15300.12,765
                CMP015,2025-01-02,170000,7650,15300.13,765
                CMP008,2025-01-01,445000,17800,35600.37,1780
                CMP008,2025-01-02,445000,17800,35600.38,1780
                CMP007,2025-01-01,225000,6750,6750.00,675
                CMP007,2025-01-02,225000,6750,6750.00,675
                """);

        Path outputDir = tempDir.resolve("results");
        Files.createDirectories(outputDir);

        AggregatorApp.run(inputFile, outputDir, true);

        Path ctrFile = outputDir.resolve("top10_ctr.csv");
        Path cpaFile = outputDir.resolve("top10_cpa.csv");

        assertThat(ctrFile).exists();
        assertThat(cpaFile).exists();

        List<String> ctrLines = Files.readAllLines(ctrFile);
        assertThat(ctrLines.get(0)).contains("campaign_id", "total_impressions", "CTR", "CPA");
        assertThat(ctrLines).hasSizeGreaterThan(1);

        List<String> cpaLines = Files.readAllLines(cpaFile);
        assertThat(cpaLines.get(0)).contains("campaign_id", "total_impressions", "CTR", "CPA");
        assertThat(cpaLines).hasSizeGreaterThan(1);

        // CMP042 has CTR 0.05 (3125+3125)/(62500+62500) = 6250/125000 = 0.05
        // CMP015 has CTR 0.045 (7650+7650)/(170000+170000) = 15300/340000 = 0.045
        // CMP008 has CTR 0.04
        // So top CTR should be CMP042 first
        assertThat(ctrLines.get(1)).startsWith("CMP042");

        // CMP007 has CPA 10.0 (6750+6750)/(675+675) = 13500/1350 = 10
        // Others have higher CPA
        assertThat(cpaLines.get(1)).startsWith("CMP007");

        // Benchmark logging
        Path benchmarkLog = outputDir.resolve("benchmark.log");
        assertThat(benchmarkLog).exists();
        String benchmarkContent = Files.readString(benchmarkLog);
        assertThat(benchmarkContent).contains("=== Benchmark Results ===");
        assertThat(benchmarkContent).contains("Input file: input.csv");
        assertThat(benchmarkContent).contains("Total rows:");
        assertThat(benchmarkContent).contains("Rows skipped:");
        assertThat(benchmarkContent).contains("Campaigns found: 4");
        assertThat(benchmarkContent).contains("Processing time:");
        assertThat(benchmarkContent).contains("Peak memory:");
        assertThat(benchmarkContent).contains("JVM args:");
        assertThat(benchmarkContent).contains("Date:");
    }

    @Test
    void emptyCsvProducesEmptyOutputFiles(@TempDir Path tempDir) throws Exception {
        Path inputFile = tempDir.resolve("empty.csv");
        Files.writeString(inputFile, "campaign_id,date,impressions,clicks,spend,conversions\n");

        Path outputDir = tempDir.resolve("results");
        Files.createDirectories(outputDir);

        AggregatorApp.run(inputFile, outputDir, true);

        List<String> ctrLines = Files.readAllLines(outputDir.resolve("top10_ctr.csv"));
        assertThat(ctrLines).hasSize(1); // header only
        List<String> cpaLines = Files.readAllLines(outputDir.resolve("top10_cpa.csv"));
        assertThat(cpaLines).hasSize(1); // header only

        assertThat(outputDir.resolve("benchmark.log")).exists();
    }

    @Test
    void benchmarkDisabledDoesNotCreateBenchmarkLog(@TempDir Path tempDir) throws Exception {
        Path inputFile = tempDir.resolve("input.csv");
        Files.writeString(inputFile, """
                campaign_id,date,impressions,clicks,spend,conversions
                CMP001,2025-01-01,1000,50,100.00,10
                """);

        Path outputDir = tempDir.resolve("results");
        Files.createDirectories(outputDir);

        AggregatorApp.run(inputFile, outputDir, false);

        assertThat(outputDir.resolve("top10_ctr.csv")).exists();
        assertThat(outputDir.resolve("top10_cpa.csv")).exists();
        assertThat(outputDir.resolve("benchmark.log")).doesNotExist();
    }
}
