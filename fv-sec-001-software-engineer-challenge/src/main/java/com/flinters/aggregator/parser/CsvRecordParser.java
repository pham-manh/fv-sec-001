package com.flinters.aggregator.parser;

import com.flinters.aggregator.model.AdRecord;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Parses CSVRecord to AdRecord. Returns empty for malformed rows.
 */
public class CsvRecordParser {

    private static final Logger log = LoggerFactory.getLogger(CsvRecordParser.class);

    private static final String CAMPAIGN_ID = "campaign_id";
    private static final String DATE = "date";
    private static final String IMPRESSIONS = "impressions";
    private static final String CLICKS = "clicks";
    private static final String SPEND = "spend";
    private static final String CONVERSIONS = "conversions";

    /**
     * Parses a CSV record to AdRecord. Skips malformed rows and returns empty.
     * Extra columns are ignored. Negative values are rejected.
     */
    public Optional<AdRecord> parse(CSVRecord record) {
        if (record == null) {
            return Optional.empty();
        }
        try {
            if (!record.isMapped(CAMPAIGN_ID) || !record.isMapped(IMPRESSIONS)
                    || !record.isMapped(CLICKS) || !record.isMapped(SPEND)
                    || !record.isMapped(CONVERSIONS)) {
                log.debug("Skipping row {}: missing required columns", record.getRecordNumber());
                return Optional.empty();
            }

            String campaignId = record.get(CAMPAIGN_ID).trim();
            if (campaignId.isEmpty()) {
                log.debug("Skipping row {}: empty campaign_id", record.getRecordNumber());
                return Optional.empty();
            }

            String date = record.isMapped(DATE) ? record.get(DATE).trim() : "";

            long impressions = parseLong(record.get(IMPRESSIONS));
            long clicks = parseLong(record.get(CLICKS));
            double spend = parseDouble(record.get(SPEND));
            long conversions = parseLong(record.get(CONVERSIONS));

            if (impressions < 0 || clicks < 0 || spend < 0 || conversions < 0) {
                log.debug("Skipping row {}: negative value in numeric field", record.getRecordNumber());
                return Optional.empty();
            }

            return Optional.of(new AdRecord(campaignId, date, impressions, clicks, spend, conversions));
        } catch (Exception e) {
            log.warn("Skipping malformed row {}: {}", record.getRecordNumber(), e.getMessage());
            return Optional.empty();
        }
    }

    private static long parseLong(String value) {
        if (value == null || value.isBlank()) {
            return 0;
        }
        return Long.parseLong(value.trim());
    }

    private static double parseDouble(String value) {
        if (value == null || value.isBlank()) {
            return 0.0;
        }
        return Double.parseDouble(value.trim());
    }
}
