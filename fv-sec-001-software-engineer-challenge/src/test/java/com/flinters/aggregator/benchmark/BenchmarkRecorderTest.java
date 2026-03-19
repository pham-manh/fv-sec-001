package com.flinters.aggregator.benchmark;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class BenchmarkRecorderTest {

    @Test
    void recordsMetricsAndWritesBenchmarkLog(@TempDir Path tempDir) throws Exception {
        Path inputFile = tempDir.resolve("data.csv");
        Files.writeString(inputFile, "campaign_id,date,impressions,clicks,spend,conversions\n");

        Path outputDir = tempDir.resolve("out");
        Files.createDirectories(outputDir);

        BenchmarkRecorder recorder = new BenchmarkRecorder(inputFile, outputDir);
        recorder.start();
        recorder.recordRow();
        recorder.recordRow();
        recorder.recordSkippedRow();
        recorder.finish(1);

        Path benchmarkLog = outputDir.resolve("benchmark.log");
        assertThat(benchmarkLog).exists();
        String content = Files.readString(benchmarkLog);
        assertThat(content).contains("=== Benchmark Results ===");
        assertThat(content).contains("Input file: data.csv");
        assertThat(content).contains("Total rows: 2");
        assertThat(content).contains("Rows skipped: 1");
        assertThat(content).contains("Campaigns found: 1");
        assertThat(content).contains("Processing time:");
        assertThat(content).contains("Peak memory:");
        assertThat(content).contains("JVM args:");
        assertThat(content).contains("Date:");
    }
}
