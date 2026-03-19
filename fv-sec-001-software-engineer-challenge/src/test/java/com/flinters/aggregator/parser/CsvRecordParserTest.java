package com.flinters.aggregator.parser;

import com.flinters.aggregator.model.AdRecord;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class CsvRecordParserTest {

    private static final String HEADER = "campaign_id,date,impressions,clicks,spend,conversions";

    private final CsvRecordParser parser = new CsvRecordParser();

    private CSVRecord parseRow(String header, String dataRow) throws Exception {
        try (CSVParser p = new CSVParser(new StringReader(header + "\n" + dataRow),
                CSVFormat.DEFAULT.withFirstRecordAsHeader())) {
            return p.iterator().next();
        }
    }

    @Test
    void parsesValidRow() throws Exception {
        CSVRecord record = parseRow(HEADER, "CMP001,2025-01-01,1000,50,25.50,5");
        Optional<AdRecord> result = parser.parse(record);

        assertThat(result).isPresent();
        assertThat(result.get().campaignId()).isEqualTo("CMP001");
        assertThat(result.get().date()).isEqualTo("2025-01-01");
        assertThat(result.get().impressions()).isEqualTo(1000);
        assertThat(result.get().clicks()).isEqualTo(50);
        assertThat(result.get().spend()).isEqualTo(25.50);
        assertThat(result.get().conversions()).isEqualTo(5);
    }

    @Test
    void returnsEmptyForMalformedNumbers() throws Exception {
        CSVRecord record = parseRow(HEADER, "CMP001,2025-01-01,abc,50,25.50,5");
        Optional<AdRecord> result = parser.parse(record);

        assertThat(result).isEmpty();
    }

    @Test
    void returnsEmptyForNegativeValues() throws Exception {
        CSVRecord record = parseRow(HEADER, "CMP001,2025-01-01,-100,50,25.50,5");
        Optional<AdRecord> result = parser.parse(record);

        assertThat(result).isEmpty();
    }

    @Test
    void acceptsEmptyDate() throws Exception {
        CSVRecord record = parseRow(HEADER, "CMP001,,1000,50,25.50,5");
        Optional<AdRecord> result = parser.parse(record);

        assertThat(result).isPresent();
        assertThat(result.get().date()).isEmpty();
    }

    @Test
    void returnsEmptyForNullRecord() {
        Optional<AdRecord> result = parser.parse(null);
        assertThat(result).isEmpty();
    }

    @Test
    void parsesRowWithExtraColumns() throws Exception {
        String header = "campaign_id,date,impressions,clicks,spend,conversions,extra";
        try (CSVParser p = new CSVParser(new StringReader(header + "\nCMP001,2025-01-01,1000,50,25.50,5,ignored"),
                CSVFormat.DEFAULT.withFirstRecordAsHeader())) {
            CSVRecord record = p.iterator().next();
            Optional<AdRecord> result = parser.parse(record);
            assertThat(result).isPresent();
            assertThat(result.get().campaignId()).isEqualTo("CMP001");
        }
    }
}
