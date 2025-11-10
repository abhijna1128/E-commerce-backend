package com.ecommerce.service;

import com.ecommerce.entity.Role;
import com.ecommerce.entity.User;
import com.ecommerce.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }
        if (user.getRole() == null) user.setRole(Role.ROLE_CUSTOMER);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public boolean validateUser(String email, String rawPassword) {
        Optional<User> u = userRepository.findByEmail(email);
        return u.isPresent() && passwordEncoder.matches(rawPassword, u.get().getPassword());
    }

    // ✅ Add this method to fix the error
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User updateUser(Long id, User incoming) {
        User u = getUserById(id);
        if (incoming.getName() != null) u.setName(incoming.getName());
        if (incoming.getEmail() != null) u.setEmail(incoming.getEmail());
        // do NOT change password here; create a dedicated changePassword method if needed
        return userRepository.save(u);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
