---
name: java-clean-modular
description: Java clean code and modular structure for the aggregator project - packages, classes, method extraction, Gradle dependencies
---

# Java Clean & Modular Skill

Use when writing or refactoring Java code in this project.

## Activation

- Implementing aggregator logic, CSV reader/writer, metrics calculator
- Splitting large methods or classes
- Adding new packages or classes
- Adding Gradle dependencies

## Rules to Follow

Apply `.cursor/rules/`:

- **java-clean-code.mdc** — explicit naming, single responsibility, no magic numbers
- **java-modular-structure.mdc** — packages, classes, when to extract
- **gradle-dependencies.mdc** — allowed dependencies, how to add them

## Package Structure

```
com.flinters.aggregator/
├── AggregatorApp.java       # CLI entrypoint
├── reader/                  # CSV streaming
├── model/                   # CampaignAggregate, etc.
├── aggregator/              # Aggregation logic
├── calculator/              # CTR, CPA
├── writer/                  # CSV output
└── cli/                     # Argument parsing
```

## Permissions

- Create new packages under `com.flinters.aggregator.*`
- Create new classes to split methods
- Add Gradle dependencies (commons-csv, picocli, slf4j, assertj, etc.)
