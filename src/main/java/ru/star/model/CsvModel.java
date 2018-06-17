package ru.star.model;

import lombok.Builder;
import lombok.ToString;

@Builder
@ToString
public class CsvModel {
    String fileId;
    String articleName;
    String url;
    String category;
    int level;
    int articleSize;
}
