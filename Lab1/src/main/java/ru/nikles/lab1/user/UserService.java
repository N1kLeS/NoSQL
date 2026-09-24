package ru.nikles.lab1.user;

import org.springframework.stereotype.Service;

import ru.nikles.lab1.profile.RiakProfileCacheRepository;

@Service
public class UserService {

    private final RiakUserRepository userRepository;
    private final RiakProfileCacheRepository profileCacheRepository;

    public UserService(RiakUserRepository userRepository,
                       RiakProfileCacheRepository profileCacheRepository) {
        this.userRepository = userRepository;
        this.profileCacheRepository = profileCacheRepository;
    }

    public User create(User user) {
        User savedUser = userRepository.save(user);
        profileCacheRepository.delete(user.id());
        return savedUser;
    }

    public User findById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }
}
