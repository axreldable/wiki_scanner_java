package ru.star.printer.model;

import lombok.Builder;
import lombok.Getter;
import ru.star.http.WikiClient;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Model for Wiki printer params.
 */
@Builder
@Getter
public class WikiPrinterParamsModel {
    private WikiClient client;
    private AtomicInteger articleCounter;
    private int printingCount;
}
