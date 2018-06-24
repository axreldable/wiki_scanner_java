package ru.star.printer.model;

import lombok.Builder;
import lombok.Getter;
import ru.star.parser.json.WikiParser;

import java.util.List;

/**
 * Model for configuring pages printer.
 */
@Builder
@Getter
public class PagesPrinterModel {
    private List<PageCategoryModel> pages;
    private String dirName;
    private String categoryId;
    private WikiPrinterParamsModel wikiPrinterParams;
    private WikiParser parser;
}
