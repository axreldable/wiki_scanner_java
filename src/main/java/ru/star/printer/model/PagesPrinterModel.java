package ru.star.printer.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class PagesPrinterModel {
    private List<PageCategoryModel> pages;
    private String dirName;
    private String categoryId;
    private WikiPrinterParamsModel wikiPrinterParams;
}
