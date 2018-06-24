package ru.star.printer.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class WikiPrinterModel {
    private WikiPrinterParamsModel params;
    private DirNameModel dirName;
    private ExecutorModel executor;
}
