package com.comandante.http.server.resource;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

public class BillHttpGraph implements Serializable {

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

    @JsonProperty
    private String title;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BillHttpGraph that = (BillHttpGraph) o;

        if (height != that.height) return false;
        if (refreshRate != that.refreshRate) return false;
        if (width != that.width) return false;
        if (!graphUrl.equals(that.graphUrl)) return false;
        if (!timezone.equals(that.timezone)) return false;
        if (!title.equals(that.title)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = graphUrl.hashCode();
        result = 31 * result + width;
        result = 31 * result + height;
        result = 31 * result + refreshRate;
        result = 31 * result + timezone.hashCode();
        result = 31 * result + title.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "BillHttpGraph{" +
                "graphUrl='" + graphUrl + '\'' +
                ", width=" + width +
                ", height=" + height +
                ", refreshRate=" + refreshRate +
                ", timezone='" + timezone + '\'' +
                ", title='" + title + '\'' +
                '}';
    }

}
