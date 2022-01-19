package ru.kata.spring.boot_security.demo.configs;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.UserRepository;
import ru.kata.spring.boot_security.demo.service.RoleService;
import javax.annotation.PostConstruct;
import java.util.Set;

@Component
public class Init {
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final BCryptPasswordEncoder passwordEncoder;

    public Init(UserRepository userRepository, RoleService roleService, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void initUsers() {
        roleService.saveRole(new Role("ROLE_USER"));
        roleService.saveRole(new Role("ROLE_ADMIN"));

        Role user = roleService.getRole("ROLE_USER");
        Role admin = roleService.getRole("ROLE_ADMIN");

        userRepository.save(new User("admin", passwordEncoder.encode("112233"), 19, Set.of(user, admin)));
        userRepository.save(new User("User1", passwordEncoder.encode("123"), 19, Set.of(user, admin)));
        userRepository.save(new User("User2", passwordEncoder.encode("123"), 8, Set.of(user)));
    }

}
