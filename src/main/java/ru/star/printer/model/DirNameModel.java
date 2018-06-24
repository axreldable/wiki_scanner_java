package ru.star.printer.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class DirNameModel {
    private String preventDirs;
    private String categoryId;
    private String category;
}
