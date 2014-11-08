package com.ramis.keepchat;

import de.robv.android.xposed.XposedBridge;

public class Utils {

    /**
     * Restrict instantiation of this class, it only contains static methods.
     */
    private Utils() { }

    /**
     * Write debug information to the Xposed Log if enabled in the settings or forced by the parameter
     * @param message The message you want to log
     * @param prefix Whether it should be prefixed by the log-tag
     * @param forced Whether to force log and thus overrides the debug setting
     */
    public static void log(String message, boolean prefix, boolean forced) {
        if (KeepChat.DEBUGGING || forced) {
            if (prefix) {
                message = KeepChat.LOG_TAG + message;
            }
            XposedBridge.log(message);
        }
    }

    /**
     * Write debug information to the Xposed Log if enabled in the settings
     * @param message The message you want to log
     * @param prefix Whether it should be prefixed by the log-tag
     */
    public static void log(String message, boolean prefix) {
        log(message, prefix, false);
    }

    /**
     * Write debug information to the Xposed Log if enabled in the settings
     * This method always prefixes the message by the log-tag
     * @param message The message you want to log
     */
    public static void log(String message) {
        log(message, true);
    }

    /**
     * Write a throwable to the Xposed Log, even when debugging is disabled.
     * @param throwable The throwable to log
     */
    public static void log(Throwable throwable) {
        XposedBridge.log(throwable);
    }

    /**
     * Write a throwable with a message to the Xposed Log, even when debugging is disabled.
     * @param message The message to log
     * @param throwable The throwable to log after the message
     */
    public static void log(String message, Throwable throwable) {
        log(message, true, true);
        log(throwable);
    }
}
