package com.flinters.aggregator;

import com.flinters.aggregator.aggregator.CampaignAggregator;
import com.flinters.aggregator.benchmark.BenchmarkRecorder;
import com.flinters.aggregator.calculator.MetricsCalculator;
import com.flinters.aggregator.cli.CliConfig;
import com.flinters.aggregator.parser.CsvRecordParser;
import com.flinters.aggregator.reader.CsvStreamReader;
import com.flinters.aggregator.reporter.Top10Reporter;
import picocli.CommandLine;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class AggregatorApp {

    public static void main(String[] args) {
        CommandLine cmd = new CommandLine(new CliConfig());
        cmd.setExecutionExceptionHandler((ex, commandLine, parseResult) -> {
            String message = ex.getMessage();
            if (message == null || message.isBlank()) {
                message = ex.getClass().getSimpleName() + ": " + ex.getCause();
            }
            commandLine.getErr().println("Error: " + message);
            return commandLine.getCommandSpec().exitCodeOnExecutionException();
        });
        int exitCode = cmd.execute(args);
        if (exitCode != 0) {
            System.exit(exitCode);
        }
    }

    /**
     * Runs the aggregation pipeline. Called after CLI validation.
     *
     * @param benchmarkEnabled when true, records performance metrics and writes benchmark.log
     */
    public static void run(Path inputPath, Path outputPath, boolean benchmarkEnabled) throws Exception {
        CsvStreamReader reader = new CsvStreamReader();
        CsvRecordParser parser = new CsvRecordParser();
        CampaignAggregator aggregator = new CampaignAggregator();
        MetricsCalculator calculator = new MetricsCalculator();
        Top10Reporter reporter = new Top10Reporter();
        BenchmarkRecorder benchmark = benchmarkEnabled ? new BenchmarkRecorder(inputPath, outputPath) : null;

        if (benchmark != null) benchmark.start();
        try (Stream<org.apache.commons.csv.CSVRecord> csvStream = reader.read(inputPath)) {
            Stream<com.flinters.aggregator.model.AdRecord> recordStream = csvStream
                    .map(r -> {
                        if (benchmark != null) benchmark.recordRow();
                        return r;
                    })
                    .map(parser::parse)
                    .filter(opt -> {
                        if (benchmark != null && opt.isEmpty()) benchmark.recordSkippedRow();
                        return opt.isPresent();
                    })
                    .map(Optional::get);

            List<com.flinters.aggregator.model.CampaignAggregate> aggregates =
                    aggregator.aggregate(recordStream);
            List<com.flinters.aggregator.model.CampaignAggregate> withMetrics =
                    calculator.withMetrics(aggregates);

            reporter.write(outputPath, withMetrics);
            if (benchmark != null) benchmark.finish(withMetrics.size());
        }
    }
}
