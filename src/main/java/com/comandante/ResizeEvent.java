package com.comandante;


public class ResizeEvent {
    private final String id;
    private final int width;
    private final int height;

    public ResizeEvent(int height, int width, String id) {
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

    public static ResizeEventBuilder newBuilder() {
        return new ResizeEventBuilder();
    }

    public static class ResizeEventBuilder {
        private int height;
        private int width;
        private String id;

        public ResizeEventBuilder setHeight(int height) {
            this.height = height;
            return this;
        }

        public ResizeEventBuilder setWidth(int width) {
            this.width = width;
            return this;
        }

        public ResizeEventBuilder setId(String id) {
            this.id = id;
            return this;
        }

        public ResizeEvent build() {
            return new ResizeEvent(height, width, id);
        }

    }
}