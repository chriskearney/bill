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

    @JsonProperty
    private String graphDuration;

    public String getGraphDuration() {
        return graphDuration;
    }

    public void setGraphDuration(String graphDuration) {
        this.graphDuration = graphDuration;
    }

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
        if (graphDuration != null ? !graphDuration.equals(that.graphDuration) : that.graphDuration != null)
            return false;
        if (graphUrl != null ? !graphUrl.equals(that.graphUrl) : that.graphUrl != null) return false;
        if (timezone != null ? !timezone.equals(that.timezone) : that.timezone != null) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = graphUrl != null ? graphUrl.hashCode() : 0;
        result = 31 * result + width;
        result = 31 * result + height;
        result = 31 * result + refreshRate;
        result = 31 * result + (timezone != null ? timezone.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (graphDuration != null ? graphDuration.hashCode() : 0);
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
                ", graphDuration='" + graphDuration + '\'' +
                '}';
    }
}
