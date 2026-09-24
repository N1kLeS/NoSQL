package ru.nikles.lab1.profile;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.nikles.lab1.user.User;

@RestController
@RequestMapping("/api/profiles")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getProfile(@PathVariable String userId) {
        return response(profileService.getProfile(userId));
    }

    @PostMapping("/{userId}/refresh")
    public ResponseEntity<User> refreshProfile(@PathVariable String userId) {
        return response(profileService.refreshProfile(userId));
    }

    private ResponseEntity<User> response(ProfileResult result) {
        return ResponseEntity.ok()
                .header("X-Cache", result.cacheStatus().name())
                .body(result.profile());
    }
}
