package ru.star.model;

import lombok.Getter;

@Getter
public class PageCategory {
    Category category;
    int pageNumber;

    public PageCategory(Category category, int pageNumber) {
        this.category = category;
        this.pageNumber = pageNumber;
    }
}
