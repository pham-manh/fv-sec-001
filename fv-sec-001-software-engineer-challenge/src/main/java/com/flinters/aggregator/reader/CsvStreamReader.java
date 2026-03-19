package com.flinters.aggregator.reader;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Streams CSV records from a file without loading the entire file into memory.
 */
public class CsvStreamReader {

    /**
     * Returns a stream of CSV records. The stream must be closed by the caller (e.g. try-with-resources).
     * Skips the header row via withFirstRecordAsHeader().
     */
    public Stream<CSVRecord> read(Path path) throws IOException {
        BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
        CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader());

        return StreamSupport.stream(parser.spliterator(), false)
                .onClose(() -> {
                    try {
                        parser.close();
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
    }
}
