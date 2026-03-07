package game.util;

public class Time {
    public static float sTimeStarted = System.nanoTime();

    public static float getTime() {
        return (float)((System.nanoTime() - sTimeStarted) * 1E-9);
    }
}
