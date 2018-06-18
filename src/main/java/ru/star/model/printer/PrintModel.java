package ru.star.model.printer;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PrintModel {
    private String preventDirs;
    private String categoryId;
    private String category;
}
