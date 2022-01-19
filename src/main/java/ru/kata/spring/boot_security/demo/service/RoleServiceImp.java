package ru.kata.spring.boot_security.demo.service;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;

import java.util.Collection;

@Service
public class RoleServiceImp implements RoleService {
    private final RoleRepository roleRepository;

    public RoleServiceImp(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Transactional(readOnly = true)
    public Role getRole(String role) {
        return roleRepository.findByRole(role)
                .orElseThrow(IllegalArgumentException::new);
    }

    @Transactional
    public void saveRole(Role role) {
        roleRepository.save(role);
    }

    @Transactional
    public Collection<Role> findAll() {
        return roleRepository.findAll();
    }

    @Transactional
    public Role getById(Long id) {
        return roleRepository.findById(id).orElse(null);
    }
}
