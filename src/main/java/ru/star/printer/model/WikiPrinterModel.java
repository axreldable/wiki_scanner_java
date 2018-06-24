package ru.star.printer.model;

import lombok.Builder;
import lombok.Getter;
import ru.star.parser.json.WikiParser;

/**
 * Model for configuring Wiki printer.
 */
@Builder
@Getter
public class WikiPrinterModel {
    private WikiPrinterParamsModel params;
    private DirNameModel dirName;
    private ExecutorModel executor;
    private WikiParser parser;
}
