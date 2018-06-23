package ru.star.csv;

import ru.star.printer.model.CsvModel;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

class CsvQueueHolder {
    private CsvQueueHolder() {
        throw new UnsupportedOperationException("Utility classes are not supposed to be instantiated");
    }

    static BlockingQueue<CsvModel> articles = new LinkedBlockingDeque<>();
}
