package com.flinters.aggregator.reader;

import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class CsvStreamReaderTest {

    private final CsvStreamReader reader = new CsvStreamReader();

    private Path resourcePath(String name) {
        return Paths.get(URI.create(getClass().getResource("/fixtures/" + name).toString()));
    }

    @Test
    void readsFixtureFileAndReturnsCorrectRowCount() throws Exception {
        Path path = resourcePath("sample.csv");

        try (Stream<CSVRecord> stream = reader.read(path)) {
            long count = stream.count();
            assertThat(count).isEqualTo(4);
        }
    }

    @Test
    void emptyFileWithHeaderOnlyReturnsZeroDataRows() throws Exception {
        Path path = resourcePath("empty.csv");

        try (Stream<CSVRecord> stream = reader.read(path)) {
            long count = stream.count();
            assertThat(count).isEqualTo(0);
        }
    }
}
