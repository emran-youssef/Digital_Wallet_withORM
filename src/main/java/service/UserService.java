package service;

import com.sun.nio.sctp.IllegalReceiveException;
import entities.User;
import lombok.AllArgsConstructor;
import repositories.UserRepository;

import java.time.LocalDateTime;

@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User register(String username, String email, String password){
        if(username == null || username.isBlank()){
            throw new IllegalArgumentException("Username is required!");
        }
        if(email == null || email.isBlank()){
            throw new IllegalArgumentException("Email is required!");
        }
        if(password == null || password.isBlank()){
            throw new IllegalArgumentException("Password is required!");
        }

        if(userRepository.existsByUsername(username)){
            throw new IllegalArgumentException("user name not available.");
        }
        if(userRepository.existsByEmail(email)){
            throw new IllegalArgumentException("Email already exist.");
        }

        var user = User.builder()
                .username(username).email(email)
                .password(password).createdAt(LocalDateTime.now()).build();

       userRepository.saveUser(user);
       return user;

    }

    public boolean login(String username, String password){
        var user = userRepository.findByUsername(username);
        if(user == null){
            return false;
        }
        return user.getPassword().equals(password);
    }

    public User findByUsername(String username){
        var user = userRepository.findByUsername(username);
        if(user == null){
            throw new IllegalArgumentException("User not found!: "+ username);
        }

        return user;
    }

    public User findById(Long id){
        var user = userRepository.findById(id);
        if(id == null){
            throw new IllegalArgumentException("User not found: "+ id);
        }

        return user;
    }

}
