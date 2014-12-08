package com.comandante.graph;


public class BillResizeEvent {
    private final String id;
    private final int width;
    private final int height;

    public BillResizeEvent(int height, int width, String id) {
        this.height = height;
        this.width = width;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public static BillResizeEventBuilder newBuilder() {
        return new BillResizeEventBuilder();
    }

    public static class BillResizeEventBuilder {
        private int height;
        private int width;
        private String id;

        public BillResizeEventBuilder setHeight(int height) {
            this.height = height;
            return this;
        }

        public BillResizeEventBuilder setWidth(int width) {
            this.width = width;
            return this;
        }

        public BillResizeEventBuilder setId(String id) {
            this.id = id;
            return this;
        }

        public BillResizeEvent build() {
            return new BillResizeEvent(height, width, id);
        }

    }
}