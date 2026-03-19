package com.flinters.aggregator.reporter;

import com.flinters.aggregator.model.CampaignAggregate;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

/**
 * Writes top10_ctr.csv and top10_cpa.csv to the output directory.
 */
public class Top10Reporter {

    private static final String[] HEADERS = {
            "campaign_id", "total_impressions", "total_clicks", "total_spend",
            "total_conversions", "CTR", "CPA"
    };

    private static final String TOP10_CTR = "top10_ctr.csv";
    private static final String TOP10_CPA = "top10_cpa.csv";

    public void write(Path outputDir, List<CampaignAggregate> aggregates) throws IOException {
        List<CampaignAggregate> byCtr = aggregates.stream()
                .sorted(Comparator.comparingDouble(CampaignAggregate::ctr).reversed())
                .limit(10)
                .toList();

        List<CampaignAggregate> byCpa = aggregates.stream()
                .filter(a -> a.totalConversions() > 0)
                .filter(a -> a.cpa() != null)
                .sorted(Comparator.comparingDouble(CampaignAggregate::cpa))
                .limit(10)
                .toList();

        writeCsv(outputDir.resolve(TOP10_CTR), byCtr);
        writeCsv(outputDir.resolve(TOP10_CPA), byCpa);
    }

    private void writeCsv(Path path, List<CampaignAggregate> rows) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
             CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(HEADERS))) {

            for (CampaignAggregate a : rows) {
                String ctrFormatted = String.format("%.4f", a.ctr());
                String cpaFormatted = a.cpa() == null ? "" : String.format("%.2f", a.cpa());
                printer.printRecord(
                        a.campaignId(),
                        a.totalImpressions(),
                        a.totalClicks(),
                        String.format("%.2f", a.totalSpend()),
                        a.totalConversions(),
                        ctrFormatted,
                        cpaFormatted
                );
            }
        }
    }
}
