package com.sahdeepsingh.Bop.notifications;

//Copied from KMP

/**
 * Sticks a message outside of the application UI, both
 * on the "notification area" and the "notification drawer".
 * <p>
 * Simple class that wraps the Android API.
 * <p>
 * You should inherit it and add your custom needs.
 */
class NotificationSimple {

    /**
     * Counter to assure each created Notification gets
     * an unique ID at runtime.
     */
    private static int LAST_NOTIFICATION_ID = 1;
    /**
     * Unique identifier for the current Notification.
     * <p>
     * When sending a new Notification, if it has the
     * same ID number it'll only get updated, not
     * created from scratch.
     */
    int NOTIFICATION_ID;

    NotificationSimple() {
        NOTIFICATION_ID = LAST_NOTIFICATION_ID;
        LAST_NOTIFICATION_ID++;
    }
}