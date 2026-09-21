package ru.nikles.lab1.user;

import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final RiakUserRepository userRepository;

    public UserService(RiakUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User create(User user) {
        return userRepository.save(user);
    }

    public User findById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }
}
