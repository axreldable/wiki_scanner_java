package ru.star.printer.model;

import lombok.Builder;
import lombok.Getter;

/**
 * Contains parameters for map directory name.
 */
@Builder
@Getter
public class DirNameModel {
    private String preventDirs;
    private String categoryId;
    private String category;
}
