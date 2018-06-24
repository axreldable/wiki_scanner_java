package ru.star.config;

import lombok.Getter;
import org.apache.log4j.Logger;
import ru.star.config.exception.WrongConfigException;
import ru.star.config.model.ConfigModel;
import ru.star.parser.json.WikiParser;
import ru.star.utils.FileUtils;

import java.io.File;

/**
 * Class for working with config.
 */
public class ConfigWorker {
    private final static Logger logger = Logger.getLogger(ConfigWorker.class);

    @Getter
    private ConfigModel config;

    /**
     * Creates and validates the config.
     *
     * @param configPath - path to the config
     * @throws WrongConfigException throws when the config is wrong
     */
    public ConfigWorker(String configPath, WikiParser parser) throws WrongConfigException {
        String configJson = FileUtils.readFromFile(configPath);
        this.config = parser.parse(configJson, ConfigModel.class);
        validate();
    }

    /**
     * Method for validate the configuration.
     *
     * @throws WrongConfigException throws when the config is wrong
     */
    private void validate() throws WrongConfigException {
        checkCondition(config.getStartCategories().length > 0, "At least one starting category is required");
        checkCondition(config.getCrawlingThreadsCount() > 0, "At least one crawling thread is required");
        checkCondition(config.getQueryDelay() >= 0, "queryDelay should not be negative");
        checkCondition(config.getPrintingCount() > 0, "At least one article for printing is required");

        checkCondition(config.getCrawlingResultsPath().length() != 0, "crawlingResultsPath shouldn't be empty");
        checkCondition(config.getResultCsvName().length() != 0, "getResultCsvName shouldn't be empty");
    }

    /**
     * Creates directories for crawling results if need it.
     */
    public void createCrawlingResultPath() {
        String crawlingResultsPath = config.getCrawlingResultsPath();
        if (!(new File(crawlingResultsPath).exists())) {
            logger.info("crawlingResultsPath doesn't exist");
            FileUtils.createDirs(crawlingResultsPath);
            logger.info(crawlingResultsPath + " created");
        }
    }

    /**
     * Creates directories for results csv file if need it.
     */
    public void createResultFilePath() {
        File resultCsvPathFile = new File(config.getResultCsvName()).getParentFile();
        if (!resultCsvPathFile.exists()) {
            String resultCsvAbsPath = resultCsvPathFile.getAbsolutePath();
            logger.info("path to " + resultCsvAbsPath + " doesn't exist");
            FileUtils.createDirs(resultCsvAbsPath);
            logger.info(resultCsvAbsPath + " created");
        }
    }

    /**
     * Checks the condition and throws {@link WrongConfigException} if the condition down't true.
     *
     * @param condition - the condition for check
     * @param message   - exception message
     * @throws WrongConfigException throws when the condition doesn't true
     */
    private void checkCondition(boolean condition, String message) throws WrongConfigException {
        if (!condition) {
            throw new WrongConfigException(message);
        }
    }
}
