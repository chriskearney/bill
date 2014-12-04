package com.comandante.http.api;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class BillGraphJson {

    @NotNull
    private String graphUrl;

    private int width;

    private int height;

    @Size(min = 30)
    private int refreshRate;

    @NotNull
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
