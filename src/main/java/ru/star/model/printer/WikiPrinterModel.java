package ru.star.model.printer;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class WikiPrinterModel {
    private WikiPrinterParams params;
    private PrintModel model;
    private ExecutorModel executorModel;
}
