package ru.star.printer.model;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.ToString;

/**
 * Represents Wiki category.
 */
@ToString
@Getter
public class CategoryModel {
    private String type;
    @SerializedName("pageid")
    private String pageId;
    private String ns;
    private String title;
}
