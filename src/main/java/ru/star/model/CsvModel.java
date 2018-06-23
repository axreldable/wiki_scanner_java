package ru.star.model;

import lombok.Builder;
import lombok.ToString;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Builder
@ToString
public class CsvModel {
    private String fileId;
    private String articleName;
    private String url;
    private String category;
    private int level;
    private int articleSize;

    public String toCsvRow() {
        return Stream.of(fileId, articleName, url, category, String.valueOf(level), String.valueOf(articleSize))
                .map(value -> value.replaceAll("\"", "\"\""))
                .collect(Collectors.joining(","));
    }
}
