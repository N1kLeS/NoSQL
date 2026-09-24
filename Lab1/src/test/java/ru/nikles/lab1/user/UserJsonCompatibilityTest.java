package ru.nikles.lab1.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserJsonCompatibilityTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void readsOldUserWithoutNotificationSettings() throws Exception {
        String oldUserJson = """
                {
                  "id": "old-user",
                  "name": "Старый пользователь",
                  "email": "old@example.com"
                }
                """;

        User user = objectMapper.readValue(oldUserJson, User.class);

        assertThat(user.notificationSettings()).isEqualTo(NotificationSettings.disabled());
    }

    @Test
    void savesAndReadsUserWithNotificationSettings() throws Exception {
        User source = new User(
                "new-user",
                "Новый пользователь",
                "new@example.com",
                new NotificationSettings(true, true, false)
        );

        String json = objectMapper.writeValueAsString(source);
        User restored = objectMapper.readValue(json, User.class);

        assertThat(restored).isEqualTo(source);
        assertThat(json).contains("\"emailNotifications\":true");
        assertThat(json).contains("\"pushNotifications\":true");
        assertThat(json).contains("\"orderStatusNotifications\":false");
    }
}
