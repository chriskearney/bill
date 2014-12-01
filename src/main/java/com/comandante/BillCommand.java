package com.comandante;

import com.beust.jcommander.Parameter;

public class BillCommand {

    @Parameter(names = { "-width", "-w" }, description = "Graph display width.")
    private int width;

    @Parameter(names = { "-height", "-h" }, description = "Graph display height.")
    private int height;

    @Parameter(names = { "-timezone", "-tz" }, description = "Graph display timezone.")
    private String timezone = null;

    @Parameter(names = "-debug", description = "Debug mode")
    private boolean debug = false;

    @Parameter(names = { "-reload", "-r" }, description = "Reload interval.")
    private int reloadInterval = 0;

    @Parameter(names = { "-graph", "-g" }, description = "Graphite graph details.")
    private String graphUrl;

    @Parameter(names = "-graphiteHost", description =  "Hostname of graphiteserver")
    private String graphiteHost;

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

    public String getGraphiteHost() {
        return graphiteHost;
    }
}
