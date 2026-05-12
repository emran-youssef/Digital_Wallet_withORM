package service;

import com.sun.nio.sctp.IllegalReceiveException;
import entities.User;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import repositories.UserRepository;

import java.time.LocalDateTime;

@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User register(EntityManager em,
                         String username,
                         String email,
                         String password) {

        validate(username, email, password);

        if (userRepository.existsByUsername(em, username))
            throw new IllegalArgumentException("Username not available");

        if (userRepository.existsByEmail(em, email))
            throw new IllegalArgumentException("Email already exists");

        User user = User.builder()
                .username(username)
                .email(email)
                .password(password)
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(em, user);

        return user;
    }

    public boolean login(EntityManager em, String username, String password) {

        User user = userRepository.findByUsername(em, username);

        if (user == null)
            return false;

        return user.getPassword().equals(password);
    }

    public User findByUsername(EntityManager em, String username) {

        User user = userRepository.findByUsername(em, username);

        if (user == null)
            throw new IllegalArgumentException("User not found: " + username);

        return user;
    }

    public User findById(EntityManager em, Long id) {

        if (id == null)
            throw new IllegalArgumentException("Id cannot be null");

        User user = userRepository.findById(em, id);

        if (user == null)
            throw new IllegalArgumentException("User not found: " + id);

        return user;
    }

    private void validate(String username, String email, String password) {
        if (username == null || username.isBlank())
            throw new IllegalArgumentException("Username is required");

        if (email == null || email.isBlank())
            throw new IllegalArgumentException("Email is required");

        if (password == null || password.isBlank())
            throw new IllegalArgumentException("Password is required");
    }
}
