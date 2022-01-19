package ru.kata.spring.boot_security.demo.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserService extends UserDetailsService {

    Optional<User> findUserById(Long id);

    User findByUsername(String username);

    Collection<User> findAll();

    void addUser(User user);

    void updateUser(User newUser);

    void deleteUser(String username);

    void deleteUserId(Long id);
}
