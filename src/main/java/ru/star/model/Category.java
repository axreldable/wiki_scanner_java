package ru.star.model;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class Category {
    @SerializedName("pageid")
    private String pageId;
    private String ns;
    private String title;
    private String type;
}
