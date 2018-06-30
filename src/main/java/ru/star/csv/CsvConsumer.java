package ru.star.csv;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.Writer;

/**
 * Add csv rows in result file in separate thread.
 */
public class CsvConsumer implements Runnable {
    private final static Logger logger = Logger.getLogger(CsvConsumer.class);

    private Writer writer;

    /**
     * Csv consumer initialisation.
     *
     * @param writer - writer
     */
    public CsvConsumer(Writer writer) {
        this.writer = writer;
    }

    @Override
    @SuppressWarnings("InfiniteLoopStatement")
    public void run() {
        String separator = System.getProperty("line.separator");
        try {
            while (true) {
                writer.write(CsvQueueHolder.articles.take().toCsvRow() + separator);
            }
        } catch (InterruptedException e) {
            // prints to end if doesn't done with articles
            while (CsvQueueHolder.articles.size() != 0) {
                try {
                    writer.write(CsvQueueHolder.articles.take().toCsvRow() + separator);
                } catch (IOException e1) {
                    logger.error("Fail to write", e1);
                } catch (InterruptedException ignored) {
                }
            }
            logger.info("Done print CSVs");
        } catch (IOException e) {
            logger.error("Fail to write", e);
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                logger.error("Couldn't close writer", e);
            }
        }
    }
}
