package ru.nikles.lab1.user;

public record NotificationSettings(
        boolean emailNotifications,
        boolean pushNotifications,
        boolean orderStatusNotifications
) {

    public static NotificationSettings disabled() {
        return new NotificationSettings(false, false, false);
    }
}
