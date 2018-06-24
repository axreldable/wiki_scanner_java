package ru.star.printer.model;

import lombok.Builder;
import lombok.ToString;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Model for printing in csv file.
 */
@Builder
@ToString
public class CsvModel {
    private String fileId;
    private String articleName;
    private String url;
    private String category;
    private int level;
    private int articleSize;

    /**
     * Convert the model to csv string.
     *
     * @return csv string
     */
    public String toCsvRow() {
        return Stream.of(fileId, articleName, url, category, String.valueOf(level), String.valueOf(articleSize))
                .map(value -> value.replaceAll("\"", "\"\""))
                .collect(Collectors.joining(","));
    }
}
