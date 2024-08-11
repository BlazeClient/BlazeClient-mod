package me.nobokik.blazeclient.api.helpers;

public class MathHelper {

    /**
     * Rounds a number to a specified decimal
     *
     * @param value    The number which should be rounded
     * @param decimals To how many decimals the number should be rounded
     */

    public static double round(double value, int decimals) {
        double scale = Math.pow(10, decimals);
        return Math.round(value * scale) / scale;
    }

    /**
     * Checks if your mouse is inside a given rectangle
     *
     * @param x      Left X coordinate of the rectangle
     * @param y      Top Y coordinate of the rectangle
     * @param w      Width of the rectangle
     * @param h      Height of the rectangle
     * @param mouseX The current X position of the mouse
     * @param mouseY The current Y position of the mouse
     */

    public static boolean withinBox(int x, int y, int w, int h, int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h;
    }

    public static boolean withinBoundsRange(float var, float var2, float range) {
        return var >= var2 - range && var <= var2 + range;
    }
}