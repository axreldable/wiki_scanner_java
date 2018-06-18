package ru.star.model.printer;

import lombok.Builder;
import lombok.Getter;
import ru.star.http.WikiClient;

import java.util.concurrent.atomic.AtomicInteger;

@Builder
@Getter
public class WikiPrinterParams {
    private WikiClient client;
    private AtomicInteger articleCounter;
    private int printingCount;
}
