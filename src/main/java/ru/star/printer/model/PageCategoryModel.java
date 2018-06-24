package ru.star.printer.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PageCategoryModel {
    private CategoryModel category;
    private int pageNumber;
}
