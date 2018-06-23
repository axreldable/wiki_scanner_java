package ru.star.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PageCategory {
    private Category category;
    private int pageNumber;
}
