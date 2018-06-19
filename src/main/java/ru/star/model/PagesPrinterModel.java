package ru.star.model;

import lombok.Builder;
import lombok.Getter;
import ru.star.model.printer.WikiPrinterParams;

import java.util.List;

@Builder
@Getter
public class PagesPrinterModel {
    private List<PageCategory> pages;
    private String dirName;
    private String categoryId;
    private WikiPrinterParams wikiPrinterParams;
}
