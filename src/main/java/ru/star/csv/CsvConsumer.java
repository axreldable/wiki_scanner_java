package ru.star.csv;

import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class CsvConsumer implements Runnable {
    private final static Logger logger = Logger.getLogger(CsvConsumer.class);

    private BufferedWriter writer;
    private String fileName;


    public CsvConsumer(String fileName) {
        this.fileName = fileName;
        try {
            writer = new BufferedWriter(new FileWriter(fileName));
        } catch (IOException e) {
            logger.error("Couldn't write to file - " + fileName, e);
        }
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
            // prints to end if doesn't make all articles
            while (CsvQueueHolder.articles.size() != 0) {
                try {
                    writer.write(CsvQueueHolder.articles.take().toCsvRow() + separator);
                } catch (IOException e1) {
                    logger.error("Couldn't write to file - " + fileName, e1);
                } catch (InterruptedException ignored) {}
            }
            logger.info("Done print CSVs");
        } catch (IOException e) {
            logger.error("Couldn't write to file - " + fileName, e);
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                logger.error("Couldn't close file - " + fileName, e);
            }
        }
    }
}
