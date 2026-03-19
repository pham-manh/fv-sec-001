package com.flinters.aggregator.cli;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CliConfigTest {

    @Test
    void parseInputAndOutputArgs() {
        CliConfig config = new CliConfig();
        new picocli.CommandLine(config).execute("--input", "data.csv", "--output", "out/");

        assertThat(config.getInputPath().toString()).endsWith("data.csv");
        assertThat(config.getOutputPath().toString()).endsWith("out");
    }

    @Test
    void helpOptionWorks() {
        int exitCode = new picocli.CommandLine(new CliConfig()).execute("--help");
        assertThat(exitCode).isEqualTo(0);
    }

    @Test
    void throwsWhenInputFileDoesNotExist(@TempDir Path tempDir) throws Exception {
        Path missingFile = tempDir.resolve("nonexistent.csv");
        Path outputDir = tempDir.resolve("out");
        Files.createDirectories(outputDir);

        assertThatThrownBy(() -> {
            CliConfig config = new CliConfig();
            new picocli.CommandLine(config).parseArgs("--input", missingFile.toString(), "--output", outputDir.toString());
            config.call();
        }).isInstanceOf(picocli.CommandLine.ExecutionException.class);
    }

    @Test
    void createsOutputDirectoryIfNotExists(@TempDir Path tempDir) throws Exception {
        Path inputFile = tempDir.resolve("input.csv");
        Files.writeString(inputFile, "campaign_id,date,impressions,clicks,spend,conversions\nCMP001,2025-01-01,100,10,5.0,1");
        Path outputDir = tempDir.resolve("new-output-dir");
        assertThat(outputDir).doesNotExist();

        CliConfig config = new CliConfig();
        new picocli.CommandLine(config).parseArgs("--input", inputFile.toString(), "--output", outputDir.toString());
        config.call();

        assertThat(outputDir).exists();
        assertThat(outputDir).isDirectory();
    }
}
