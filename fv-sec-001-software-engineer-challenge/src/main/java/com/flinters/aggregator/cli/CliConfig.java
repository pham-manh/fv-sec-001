package com.flinters.aggregator.cli;

import com.flinters.aggregator.AggregatorApp;
import picocli.CommandLine;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "aggregator",
        description = "Ad Performance Aggregator - processes CSV and outputs top 10 by CTR and CPA",
        mixinStandardHelpOptions = true
)
public class CliConfig implements Callable<Integer> {

    @CommandLine.Option(
            names = {"--input", "-i"},
            description = "Path to input CSV file",
            required = true
    )
    private Path inputPath;

    @CommandLine.Option(
            names = {"--output", "-o"},
            description = "Path to output directory for result files",
            required = true
    )
    private Path outputPath;

    @CommandLine.Option(
            names = {"--benchmark", "-b"},
            description = "Enable benchmark logging (writes benchmark.log with performance metrics)",
            defaultValue = "false"
    )
    private boolean benchmarkEnabled;

    public Path getInputPath() {
        return inputPath.toAbsolutePath().normalize();
    }

    public Path getOutputPath() {
        return outputPath.toAbsolutePath().normalize();
    }

    @Override
    public Integer call() throws Exception {
        validateInputFile();
        ensureOutputDirectory();
        AggregatorApp.run(getInputPath(), getOutputPath(), benchmarkEnabled);
        return CommandLine.ExitCode.OK;
    }

    private void validateInputFile() {
        if (!Files.exists(inputPath)) {
            throw new CommandLine.ExecutionException(
                    new CommandLine(this),
                    "Input file does not exist: " + inputPath
            );
        }
        if (!Files.isRegularFile(inputPath)) {
            throw new CommandLine.ExecutionException(
                    new CommandLine(this),
                    "Input path is not a regular file: " + inputPath
            );
        }
    }

    private void ensureOutputDirectory() throws java.io.IOException {
        if (!Files.exists(outputPath)) {
            Files.createDirectories(outputPath);
        }
        if (!Files.isDirectory(outputPath)) {
            throw new CommandLine.ExecutionException(
                    new CommandLine(this),
                    "Output path is not a directory: " + outputPath
            );
        }
    }
}
