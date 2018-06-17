package ru.star.model;

import lombok.Builder;
import lombok.ToString;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Builder
@ToString
public class CsvModel {
    String fileId;
    String articleName;
    String url;
    String category;
    int level;
    int articleSize;

    public String toCsvRow() {
        return Stream.of(fileId, articleName, url, category, String.valueOf(level), String.valueOf(articleSize))
                .map(value -> value.replaceAll("\"", "\"\""))
                .collect(Collectors.joining(","));
    }
}
