package ru.star.csv;

import ru.star.printer.model.CsvModel;

/**
 * Interface for csv producer.
 */
public interface CsvProducer {

    /**
     * Method for add csv in the queue. Blocks the thread if the queue is full.
     *
     * @param model - csv model for writing
     */
    default void addCsv(CsvModel model) {
        try {
            CsvQueueHolder.articles.put(model);
        } catch (InterruptedException ignored) {}
    }
}
