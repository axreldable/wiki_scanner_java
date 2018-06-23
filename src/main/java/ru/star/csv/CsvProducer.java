package ru.star.csv;

import ru.star.model.CsvModel;

public interface CsvProducer {
    default void addCsv(CsvModel model) {
        try {
            CsvQueueHolder.articles.put(model);
        } catch (InterruptedException ignored) {}
    }
}
