package com.flinters.aggregator;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AggregatorAppTest {

    @Test
    void mainWithHelpExitsSuccessfully() {
        int exitCode = new picocli.CommandLine(new com.flinters.aggregator.cli.CliConfig())
                .execute("--help");
        assertThat(exitCode).isEqualTo(0);
    }
}
