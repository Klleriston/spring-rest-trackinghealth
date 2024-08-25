package com.fiap.backend.services;

import com.fiap.backend.exceptions.ResourceNotFoundException;
import com.fiap.backend.models.User;
import com.fiap.backend.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private HealthProfileService healthProfileService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public User createUser(String nome, String email, String password) {
        String encodedPasswordString = passwordEncoder.encode(password);
        User user = new User(nome, email, encodedPasswordString);
        User savedUser = userRepository.save(user);
        healthProfileService.createHealthProfile(0.0, 0.0, "Inactive", false, savedUser.getId());

        return savedUser;
    }

    public User updatedUser(UUID id, String name, String email, String password) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found \n" + id));

        User updatedUser = new User(existingUser.getId(), name, email, password);

        return userRepository.save(updatedUser);
    }

    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        userRepository.delete(user);
    }

}
