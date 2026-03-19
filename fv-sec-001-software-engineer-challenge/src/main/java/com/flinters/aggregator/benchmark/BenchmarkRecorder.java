package com.flinters.aggregator.benchmark;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.lang.management.MemoryUsage;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Records benchmark metrics during aggregation and writes results to console and benchmark.log.
 * Measurements are designed to have minimal impact on main program performance.
 */
public class BenchmarkRecorder {

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final String inputFileName;
    private final long inputFileSizeBytes;
    private final Path outputDir;

    private long startTimeNanos;
    private final AtomicLong totalRows = new AtomicLong(0);
    private final AtomicLong rowsSkipped = new AtomicLong(0);

    public BenchmarkRecorder(Path inputPath, Path outputDir) throws IOException {
        this.inputFileName = inputPath.getFileName().toString();
        this.inputFileSizeBytes = Files.size(inputPath);
        this.outputDir = outputDir;
    }

    public void start() {
        startTimeNanos = System.nanoTime();
    }

    public void recordRow() {
        totalRows.incrementAndGet();
    }

    public void recordSkippedRow() {
        rowsSkipped.incrementAndGet();
    }

    public void finish(int campaignsFound) throws IOException {
        long endTimeNanos = System.nanoTime();
        double processingTimeSec = (endTimeNanos - startTimeNanos) / 1_000_000_000.0;
        long peakMemoryBytes = getPeakHeapMemory();
        String jvmArgs = getJvmArgs();
        String date = LocalDateTime.now().format(DATE_FORMAT);

        String report = formatReport(
                inputFileName,
                formatFileSize(inputFileSizeBytes),
                totalRows.get(),
                rowsSkipped.get(),
                campaignsFound,
                processingTimeSec,
                peakMemoryBytes,
                jvmArgs,
                date
        );

        System.out.println(report);
        Path logPath = outputDir.resolve("benchmark.log");
        Files.writeString(logPath, report + System.lineSeparator(), StandardCharsets.UTF_8);
    }

    private static String formatReport(
            String inputFile,
            String fileSize,
            long totalRows,
            long rowsSkipped,
            int campaignsFound,
            double processingTimeSec,
            long peakMemoryBytes,
            String jvmArgs,
            String date
    ) {
        return """
                === Benchmark Results ===
                Input file: %s (%s)
                Total rows: %s
                Rows skipped: %s
                Campaigns found: %d
                Processing time: %.1fs
                Peak memory: %s
                JVM args: %s
                Date: %s
                """.formatted(
                inputFile,
                fileSize,
                formatNumber(totalRows),
                formatNumber(rowsSkipped),
                campaignsFound,
                processingTimeSec,
                formatMemory(peakMemoryBytes),
                jvmArgs.isEmpty() ? "(default)" : jvmArgs,
                date
        ).trim();
    }

    private static long getPeakHeapMemory() {
        long peak = 0;
        for (MemoryPoolMXBean pool : ManagementFactory.getMemoryPoolMXBeans()) {
            if (pool.getType() == MemoryType.HEAP) {
                MemoryUsage usage = pool.getPeakUsage();
                if (usage != null) {
                    peak += usage.getUsed();
                }
            }
        }
        return peak > 0 ? peak : Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    }

    private static String getJvmArgs() {
        return String.join(" ", ManagementFactory.getRuntimeMXBean().getInputArguments());
    }

    private static String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.2f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.2f MB", bytes / (1024.0 * 1024));
        return String.format("%.2f GB", bytes / (1024.0 * 1024 * 1024));
    }

    private static String formatMemory(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.0f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.0f MB", bytes / (1024.0 * 1024));
        return String.format("%.2f GB", bytes / (1024.0 * 1024 * 1024));
    }

    private static String formatNumber(long n) {
        return String.format("%,d", n);
    }
}
