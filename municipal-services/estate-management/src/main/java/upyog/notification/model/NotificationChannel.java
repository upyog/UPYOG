package upyog.notification.model;

public enum NotificationChannel {
    SMS,
    EVENT,
    EMAIL;

    public static NotificationChannel from(String value) {
        if (value == null) {
            return null;
        }
        return NotificationChannel.valueOf(value.trim().toUpperCase());
    }
}
