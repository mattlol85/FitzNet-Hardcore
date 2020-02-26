package org.fitznet.doomdns.fitznethardcore;

public class Logger {

    private static final String PREFIX = "Fitz-Net Hardcore";

    public static void logStatistic(String message) {
        logWithExtra("Statistic", message);
    }

    public static void logDebug(String message) {
        logWithExtra("Debug", message);
    }

    public static void logError(String message) {
        logWithExtra("Error", message);
    }

    public static void logWarning(String message) {
        logWithExtra("Warning", message);
    }

    static void logInfo(String message) {
        logWithExtra("Info", message);
    }

    private static void logWithExtra(String extra, String message) {
        System.out.println(PREFIX + ": " + extra + " | " + message);
    }

    public static void log(String message) {
        System.out.println(PREFIX + " | " + message);
    }

}