# Introduction

This project uses skills downloaded from [skill.sh](https://skill.sh) to configure rules and skills for the AI-assisted workflow. The prompts below may appear to be structured for editing or documentation purposes, but they are in fact the **exact prompts** passed to the AI agent in the IDE to implement the code. ChatGPT is used only to format and organize the prompt structure before feeding them to the agent.

---

# Prompt 1: Create Context Files

## Task

Read the file `fv-sec-001-software-engineer-challenge/README.md`, analyze all project requirements, then create a set of context files in the folder `fv-sec-001-software-engineer-challenge/.context/` including:

1. `project-overview.md` — Project summary, objectives, scope, out-of-scope
2. `tech-requirements.md` — Tech stack, CLI interface, CSV schema, performance/memory requirements, deliverables
3. `business-logic.md` — Process flow, CTR/CPA formulas, edge cases (division by zero, malformed rows), output format
4. `tasks-breakdown.md` — Breakdown into 8-10 tasks, each task includes: description, input/output, acceptance criteria, complexity, dependency
5. `agent-instructions.md` — Instructions for 3 Agent type: Coder (recommended patterns, common errors), Reviewer (checklist), Tester (specific test cases with sample test data and expected output)
6. `glossary.md` — Explanation of terms: CTR, CPA, impressions, conversions, streaming, etc.

## Requirements
- Each file must be self-contained and understandable when read individually.
- If the README is unclear, write "Needs clarification".
- Use clear Markdown with specific examples.
- Calculate expected values ​​for sample test data from the README.



# Prompt 2: Clean Code & Production Quality

## Task

Follow clean code principles and write production-quality code.

## Requirements
- Ensure the code is **clean, readable, and maintainable**
- Follow **SOLID principles** and standard Java conventions
- Use meaningful naming for variables, methods, and classes
- Keep methods **small and focused** (single responsibility)
- Avoid code duplication

## Refactoring & Structure
- You are allowed to:
  - Create new **packages** and **classes** if needed
  - Split large or complex methods into smaller ones
  - Extract reusable logic into separate services, utilities, or helpers
- Maintain a clear and scalable project structure

## Dependencies
- You are allowed to:
  - Add necessary **Gradle dependencies** to support the implementation
  - Ensure dependencies are minimal, relevant, and commonly accepted

## Best Practices
- Add appropriate **exception handling**
- Use **logging** where necessary
- Write code that is easy to test (prefer dependency injection)
- Follow standard practices for **Spring Boot** (if applicable)

## Output Expectations
- Provide clean, well-structured code
- Clearly show any new files, classes, or packages created
- Include Gradle updates if dependencies are added
- Add brief explanations only if necessary


# Prompt 3: Implementation Plan

## Task

I need you to create a detailed implementation plan for a Java CLI application called "Ad Performance Aggregator" (challenge code: FV-SEC001).

## Project Context

This is a recruitment challenge. The project already has a skeleton at:
`working/recruitment/fv-sec-001-software-engineer-challenge/`

Language: **Java** (Maven project, already scaffolded)

---

## Problem Statement

Build a CLI app that reads a large CSV file (~1GB) of advertising records and outputs two aggregated result files.

### Input CSV Schema

| Column | Type | Description |
|---|---|---|
| campaign_id | string | Campaign ID |
| date | string | YYYY-MM-DD |
| impressions | integer | Ad impressions |
| clicks | integer | Number of clicks |
| spend | float | Advertising cost (USD) |
| conversions | integer | Number of conversions |

### Required Computations (aggregated per campaign_id)

- `total_impressions`, `total_clicks`, `total_spend`, `total_conversions`
- `CTR` = total_clicks / total_impressions
- `CPA` = total_spend / total_conversions (null/excluded if conversions = 0)

---

## Expected Outputs

### File 1: `top10_ctr.csv` — Top 10 campaigns by highest CTR

Columns: `campaign_id, total_impressions, total_clicks, total_spend, total_conversions, CTR, CPA`

### File 2: `top10_cpa.csv` — Top 10 campaigns by lowest CPA

Columns: same as above. Exclude campaigns with zero conversions.

---

## CLI Interface

java -jar aggregator.jar --input ad_data.csv --output results/


---

## Technical Constraints

- **Memory-efficient**: Must handle ~1GB file without loading it all into memory (use streaming/buffered reading)
- **Performance**: Should complete in reasonable time
- **Error handling**: Handle missing files, malformed/incomplete rows, division by zero
- **Clean code**: Meaningful names, no dead code, consistent style

---

## Test Requirements

Write unit and integration tests covering:

1. **Aggregation logic** — correct sum of impressions, clicks, spend, conversions per campaign
2. **CTR calculation** — correct formula, precision
3. **CPA calculation** — correct formula, null/excluded when conversions = 0
4. **Top 10 sorting** — CTR descending, CPA ascending
5. **CSV parsing** — malformed rows skipped gracefully, correct column mapping
6. **Edge cases**:
   - Campaign with 0 impressions (CTR = 0 or guard against division by zero)
   - Campaign with 0 conversions (excluded from CPA ranking)
   - Single-row CSV
   - Empty CSV (header only)
   - Missing columns / extra columns
7. **Output format** — CSV files have correct headers, correct number of rows (≤10), correct precision

Use **JUnit 5** + **Mockito** if needed. Tests should be runnable via `mvn test`.

---

## Deliverables Plan

Please produce a step-by-step implementation plan that includes:

1. **Package/class structure** (e.g. `model`, `parser`, `aggregator`, `reporter`, `cli`)
2. **Data flow** from CSV read → aggregate → sort → write output
3. **Key classes and their responsibilities**
4. **Memory optimization strategy** (streaming approach)
5. **Test structure** — which test class covers which behavior
6. **README.md content** to include: setup, how to run, libraries used, performance notes

Do NOT write code yet — produce the plan only. I will review and approve before implementation starts.



# Prompt 4: Docker Setup

## Task

Set up a Dockerfile for the ad-aggregator project (whole project) with the following requirements:

## Dockerfile
- Base image: use an appropriate lightweight base (e.g. eclipse-temurin:17-jre-alpine or language-specific slim variant)
- Multi-stage build: build stage with JDK, run stage with JRE only to keep the final image minimal
- The container runs the main application (aggregator) that processes CSV and outputs top 10 by CTR/CPA
- Mount points: /data for input CSV files, /output for result files (top10_ctr.csv, top10_cpa.csv, benchmark.log)
- Run as non-root user when possible
- Include a .dockerignore file to exclude unnecessary files (e.g. .git, .gradle, build, tests, IDE configs)

## Benchmark Logging (integrated into the main app)
- Benchmark logging is a feature of the application, enabled via the --benchmark (or -b) CLI flag
- When enabled, the app records performance metrics and writes to benchmark.log in the output directory
- The benchmark.log report includes: input file name and size, total rows processed, rows skipped, campaigns found, processing time, peak memory, JVM args, timestamp
- Benchmark measurements must not affect main program performance when disabled
- The application exits cleanly (exit code 0 on success, non-zero on failure)

## Sample Docker Run Command

Provide a sample showing how to:
- Mount the input directory to /data
- Mount the output directory to /output
- Pass --input and --output paths
- Optionally pass --benchmark to enable benchmark logging



# Prompt 5: Implement Benchmark Logging

## Task

Read the source code in `fv-sec-001-software-engineer-challenge/` and add the **benchmark logging** functionality to your program.

## What is Benchmark Logging?

When the program finishes running, it automatically **prints to the console** and **writes to the `benchmark.log` file the following actual performance parameters:

```
=== Benchmark Results ===
Input file: ad_data.csv (1.02 GB)
Total rows: 15,000,000
Rows skipped: 23
Campaigns found: 500
Processing time: 12.5s
Peak memory: 178 MB
JVM args: -Xmx512m
Date: 2025-xx-xx HH:mm:ss
```

## Implementation Requirements

1. **Measure processing time** — starts from the moment the file is read, ends when the output is written
2. **Measure peak memory** — use `Runtime.getRuntime().totalMemory() - freeMemory()` or Use `MemoryMXBean` to get peak heap usage
3. **Count data** — total rows processed, rows skipped (malformed), number of campaigns found
4. **Get file size** — input file size
5. **Print to console** after execution
6. **Write to file** `benchmark.log` in output directory

## Note

- Benchmark measurements MUST NOT affect the main program performance
- Read the current code, find the right place to insert measurement logic — do not break the existing architecture
- Keep code clean, separate the benchmark part into a separate class/method if necessary


# Prompt 6: Generate README.md for Completed Project

## Role

You are the Technical Writer Agent. Read the entire source code, build configuration, tests, Dockerfile, and benchmark logs of the project, then write an accurate and professional `README.md` file.

## Context

- Project path: `fv-sec-001-software-engineer-challenge/`
- Language: **Java**
- Application type: **CLI** — processes a ~1GB CSV advertising file, aggregates by campaign_id, outputs top 10 CTR and top 10 CPA
- Run command: `java -jar build/libs/aggregator-1.0.0.jar --input ad_data.csv --output results/`

## Before Writing — MUST Read

1. `build.gradle` or `pom.xml` → Java version, dependencies, build command
2. `src/main/java/...` → package name, class, code architecture
3. `src/test/java/...` → test class, test cases
4. `Dockerfile` (if available)
5. `benchmark.log` or output console → processing time, peak memory
6. Output files: `top10_ctr.csv`, `top10_cpa.csv`

## README Requirements

The requirements for the README must include:

- Setup instructions

- How to run the program
- Libraries used

- Processing time for the 1GB file

- Peak memory usage (if measured)

## README Structure

### 1. Introduction
- Project name: `FV-SEC001 — Ad Performance Aggregator`
- Description (2-3 sentences): what the program does, what the input/output is

### 2. Prerequisites
- Java version (read from build config)

- Build tool + version (Gradle/Maven)

### 3. Setup & Build
```
git clone <repo-url>
cd fv-sec-001-software-engineer-challenge
```
- Build command: read from the actual build config, write the correct command

### 4. How to Run
```
java -jar build/libs/aggregator-1.0.0.jar --input ad_data.csv --output results/
```
- Explanation of arguments: `--input`, `--output`
- Output files: `results/top10_ctr.csv`, `results/top10_cpa.csv`

### 5. Libraries Used
- Read from build.gradle/pom.xml
- Table: Library | Version | Purpose
- State the reason for selecting each library

### 6. Project Structure
- Directory tree, briefly explain the role of each main class/package

### 7. Processing Time & Peak Memory
- Read from `benchmark.log` or actual console output
- If there is no data yet → add a placeholder note "Will be updated after benchmarking"
- Table format:

| Metric | Value |

|-----------------|-------|

| Input file size | ~1GB |

| Processing time | ... |

| Peak memory | ... |

### 8. Running Tests
- Commands to run tests (read from build config)

- List the main test classes and provide a brief description

### 9. Design Decisions
- Read the code and explain:

- Streaming strategy for a 1GB file

- Memory optimization

- Error handling (malformed rows, division by zero)

- Float precision for spend

### 10. Docker (if you have a Dockerfile)

- Commands to build the image
- Commands to run the container with volume mounts for input/output

## Rules

1. **Read the actual code** — class names, packages, libraries, and versions must be 100% accurate
2. **Do not make things up** — if you can't find it, write N/A or skip it
3. **Write in English**

4. **Standard Markdown** — displays beautifully on GitHub
5. **Concise and professional** — avoid lengthy writing, get straight to the point
6. Do not add unnecessary sections beyond the requirements