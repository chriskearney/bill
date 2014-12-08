package com.comandante;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.internal.Maps;

import java.util.Map;

public class BillCommand {

    @Parameter(names = { "-width", "-w" }, description = "Graph display width.")
    private int width = Bill.DEFAULT_WIDTH;

    @Parameter(names = { "-height", "-h" }, description = "Graph display height.")
    private int height = Bill.DEFAULT_HEIGHT;

    @Parameter(names = { "-timezone", "-tz" }, description = "Graph display timezone.")
    private String timezone = null;

    @Parameter(names = "-debug", description = "Debug mode")
    private boolean debug = false;

    @Parameter(names = { "-reload", "-r" }, description = "Reload interval.")
    private int reloadInterval = Integer.MAX_VALUE;

    @Parameter(names = { "-graph", "-g" }, description = "Graphite graph details.")
    private String graphUrl;

    @Parameter(names = { "-title", "-t" }, description = "Graphite graph details.")
    private String title;

    @Parameter(names = { "-duration", "-d" }, description = "Graphite graph duration.")
    private String duration;

    public Map<String, String> getInjectPairs() {
        Map<String, String> stringStringMap = Maps.newHashMap();
        stringStringMap.put("width", Integer.toString(width));
        stringStringMap.put("height", Integer.toString(height));
        if (timezone != null) {
            stringStringMap.put("tz", timezone);
        }
        if (duration != null) {
            stringStringMap.put("from", duration);
        }
        return stringStringMap;
    }

    public String getGraphUrl() {
        return graphUrl;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public String getTimezone() {
        return timezone;
    }

    public boolean isDebug() {
        return debug;
    }

    public int getReloadInterval() {
        return reloadInterval;
    }

    public String getTitle() {
        return title;
    }

    public String getDuration() {
        return duration;
    }

}
