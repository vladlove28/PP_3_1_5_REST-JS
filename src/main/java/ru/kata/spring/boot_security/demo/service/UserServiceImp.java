package ru.kata.spring.boot_security.demo.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.UserRepository;


import javax.validation.Valid;
import java.util.Collection;
import java.util.Optional;
@Validated
@Service
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;

    private final RoleService roleService;

    private final BCryptPasswordEncoder passwordEncoder;

    public UserServiceImp(UserRepository userRepository, RoleService roleService, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
        return new User(user.getUsername(),
                user.getPassword(),
                user.getAuthorities());
    }

    @Transactional(readOnly = true)
    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
//        .orElseThrow(() -> new UsernameNotFoundException(username));
    }

    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
    }

    @Transactional(readOnly = true)
    public Collection<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional
    public void addUser(User user) {
        userRepository.findByUsername(user.getUsername()).ifPresentOrElse((r) -> {
        }, () -> {
            makeUserIfNot(user);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
        });
    }

    @Transactional
    public void updateUser(@Valid User newUser) {
        User oldUser = userRepository.findById(newUser.getId()).orElseThrow(RuntimeException::new);
        oldUser.setUsername(newUser.getUsername());
        oldUser.setAge(newUser.getAge());
        oldUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        oldUser.setRoles(newUser.getRoles());
        makeUserIfNot(oldUser);
    }

    @Transactional
    public void deleteUser(String username) {
        userRepository.findByUsername(username).ifPresent((this::flushUser));
    }

    @Transactional
    @Override
    public void deleteUserId(Long id) {
        userRepository.findById(id).ifPresent((this::flushUser));
    }

    private void flushUser(User user) {
        userRepository.delete(user);
    }

    private void makeUserIfNot(User oldUser) {
        if (!oldUser.getStringRoles().contains(roleService.getRole("ROLE_USER").toString())) {
            oldUser.addRole(roleService.getRole("ROLE_USER"));
        }
    }
}
