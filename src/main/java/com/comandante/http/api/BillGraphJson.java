package com.comandante.http.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class BillGraphJson {

    @NotNull
    @JsonProperty
    private String graphUrl;

    @JsonProperty
    private int width;

    @JsonProperty
    private int height;

    @Size(min = 30)
    @JsonProperty
    private int refreshRate;

    @NotNull
    @JsonProperty
    private String timezone;

    public String getGraphUrl() {
        return graphUrl;
    }

    public void setGraphUrl(String graphUrl) {
        this.graphUrl = graphUrl;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getRefreshRate() {
        return refreshRate;
    }

    public void setRefreshRate(int refreshRate) {
        this.refreshRate = refreshRate;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }
}
