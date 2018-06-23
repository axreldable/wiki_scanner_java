package ru.star.model;

import lombok.Getter;

@Getter
public class PageCategory {
    private Category category;
    private int pageNumber;

    public PageCategory(Category category, int pageNumber) {
        this.category = category;
        this.pageNumber = pageNumber;
    }
}
