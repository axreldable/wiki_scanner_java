package ru.star.model;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

@Getter
class WikiModel {
    @SerializedName("pageid")
    private String pageId;
    private String ns;
    private String title;
}
