package me.nobokik.blazeclient.gui;

public interface Renderable {
    String getName();

    void render();

    default Theme getTheme() {
        return new Theme() {
        };
    }
}
