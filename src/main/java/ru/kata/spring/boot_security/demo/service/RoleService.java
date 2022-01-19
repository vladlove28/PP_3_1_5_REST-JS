package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.model.Role;
import java.util.Collection;

public interface RoleService {
     Role getRole(String role);
    Collection<Role> findAll();
    Role getById(Long id);
    void saveRole(Role role);

}
