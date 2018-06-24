package ru.star.csv;

import ru.star.printer.model.CsvModel;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Queue holder for csv models. Uses Linked queue because we only add and remove models.
 */
class CsvQueueHolder {
    private CsvQueueHolder() {
        throw new UnsupportedOperationException("Utility classes are not supposed to be instantiated");
    }

    static BlockingQueue<CsvModel> articles = new LinkedBlockingDeque<>();
}
