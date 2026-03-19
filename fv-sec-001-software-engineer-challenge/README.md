# FV-SEC001 — Ad Performance Aggregator

A Java CLI application that processes a large CSV file (~1GB) containing advertising performance data. It aggregates records by `campaign_id`, computes CTR and CPA metrics, and outputs the top 10 campaigns by highest CTR and lowest CPA to CSV files.

---

## 1. Introduction

**FV-SEC001 — Ad Performance Aggregator** reads a CSV file with columns `campaign_id`, `date`, `impressions`, `clicks`, `spend`, and `conversions`. It aggregates totals per campaign, computes CTR (clicks/impressions) and CPA (spend/conversions), then writes `top10_ctr.csv` and `top10_cpa.csv` to the specified output directory.

---

## 2. Prerequisites

- **Java 17** or later
- **Gradle 8.x** (wrapper included: `gradlew` / `gradlew.bat`)

---

## 3. Setup & Build

```bash
git clone <repo-url>
cd fv-sec-001-software-engineer-challenge
```

Build the fat JAR:

```bash
./gradlew shadowJar
```

On Windows:

```bash
gradlew.bat shadowJar
```

The JAR is produced at `build/libs/aggregator-1.0.0.jar`.

---

## 4. How to Run

```bash
java -jar build/libs/aggregator-1.0.0.jar --input ad_data.csv --output results/
```

### Arguments

| Argument | Short | Description |
| --- | --- | --- |
| `--input` | `-i` | Path to input CSV file (required) |
| `--output` | `-o` | Path to output directory for result files (required) |
| `--benchmark` | `-b` | Enable benchmark logging (writes `benchmark.log` with performance metrics) |

### Output Files

- `results/top10_ctr.csv` — Top 10 campaigns by highest CTR
- `results/top10_cpa.csv` — Top 10 campaigns by lowest CPA (excludes zero conversions)
- `results/benchmark.log` — Performance metrics (only when `--benchmark` is used)

---

## 5. Libraries Used

| Library | Version | Purpose |
| --- | --- | --- |
| Apache Commons CSV | 1.10.0 | Streaming CSV parsing; avoids loading the full file into memory |
| Picocli | 4.7.5 | CLI argument parsing with validation and help generation |
| SLF4J | 2.0.9 | Logging facade |
| Logback | 1.4.14 | Logging implementation |
| JUnit 5 | 5.10.2 | Unit and integration tests |
| AssertJ | 3.24.2 | Fluent assertions in tests |
| Mockito | 5.8.0 | Test mocking |

---

## 6. Project Structure

```text
src/main/java/com/flinters/aggregator/
├── AggregatorApp.java          # Entry point, orchestrates pipeline
├── cli/
│   └── CliConfig.java          # CLI options (Picocli), validation
├── reader/
│   └── CsvStreamReader.java    # Streams CSV records from file (BufferedReader + Commons CSV)
├── parser/
│   └── CsvRecordParser.java    # Parses CSVRecord → AdRecord, skips malformed rows
├── aggregator/
│   └── CampaignAggregator.java # Aggregates AdRecords by campaign_id
├── calculator/
│   └── MetricsCalculator.java  # Computes CTR and CPA for aggregates
├── reporter/
│   └── Top10Reporter.java      # Writes top10_ctr.csv and top10_cpa.csv
├── benchmark/
│   └── BenchmarkRecorder.java  # Records processing time, peak memory, writes benchmark.log
└── model/
    ├── AdRecord.java           # Immutable record for a parsed CSV row
    └── CampaignAggregate.java  # Immutable aggregate with totals + CTR + CPA
```

---

## 7. Processing Time & Peak Memory

| Metric | Value |
| --- | --- |
| Input file size | ~1 GB (994.97 MB) |
| Total rows | 26,843,544 |
| Processing time | 31.9 s |
| Peak memory | 154 MB |
| JVM args | (default) |

*Measured with `--benchmark` on a ~1GB dataset. Run with `--benchmark` to record metrics for your environment.*

---

## 8. Running Tests

```bash
./gradlew test
```

On Windows:

```bash
gradlew.bat test
```

### Test Classes

| Test Class | Description |
| --- | --- |
| `AggregatorAppTest` | CLI validation and help |
| `AggregatorAppIntegrationTest` | End-to-end flow, empty CSV, benchmark on/off |
| `CampaignAggregatorTest` | Aggregation logic, multiple campaigns |
| `MetricsCalculatorTest` | CTR/CPA calculation, division-by-zero handling |
| `CsvRecordParserTest` | Parsing, malformed rows, negative values |
| `CsvStreamReaderTest` | CSV streaming with headers |
| `Top10ReporterTest` | Output format, sorting, filtering |
| `CliConfigTest` | Input/output path validation |
| `BenchmarkRecorderTest` | Benchmark report format |

---

## 9. Design Decisions

### Streaming

The file is never loaded into memory. `CsvStreamReader` uses `BufferedReader` and Apache Commons CSV `CSVParser` to stream records line-by-line. Records are processed in a pipeline: parse → filter malformed → aggregate.

### Memory

Only the aggregation map (one entry per unique `campaign_id`) is kept in memory. For a typical dataset with ~50 campaigns, memory usage stays low (~154 MB peak for ~1GB input).

### Error Handling

- **Malformed rows:** `CsvRecordParser` returns `Optional.empty()` for missing columns, invalid numbers, or negative values; rows are skipped and counted in `benchmark.log`.
- **Division by zero:** CTR = 0 when impressions = 0; CPA = null when conversions = 0. Campaigns with zero conversions are excluded from `top10_cpa.csv`.
- **Missing input/output:** `CliConfig` validates that the input file exists and the output path is a directory (creates it if missing).

### Float Precision

`spend` is stored as `double`. Output uses `%.2f` for spend and CPA, and `%.4f` for CTR.

---

## 10. Docker

### Build

```bash
docker build -t ad-aggregator:latest .
```

### Run

Mount input and output directories:

```bash
docker run --rm \
  -v /path/to/data:/data \
  -v /path/to/results:/output \
  ad-aggregator:latest --input /data/ad_data.csv --output /output
```

With benchmark logging:

```bash
docker run --rm \
  -v /path/to/data:/data \
  -v /path/to/results:/output \
  ad-aggregator:latest --input /data/ad_data.csv --output /output --benchmark
```

Example (Windows PowerShell):

```powershell
docker run --rm `
  -v ${PWD}/data:/data `
  -v ${PWD}/docker-output:/output `
  ad-aggregator:latest --input /data/ad_data.csv --output /output --benchmark
```

The image uses Eclipse Temurin 17 JRE and runs as non-root user `appuser`.
