package ru.star.config.model;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

/**
 * The config params.
 */
@Getter
public class ConfigModel {
    @SerializedName("start_categories")
    private String[] startCategories;
    @SerializedName("crawling_threads_count")
    private int crawlingThreadsCount;
    @SerializedName("query_delay")
    private int queryDelay;
    @SerializedName("crawling_results_path")
    private String crawlingResultsPath;
    @SerializedName("result_csv_name")
    private String resultCsvName;
    @SerializedName("print_count")
    private int printingCount;
}
