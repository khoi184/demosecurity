package com.thuanthanhtech.demosecurity.config;

import java.util.HashSet;
import java.util.Set;

import com.thuanthanhtech.demosecurity.entity.Role;
import com.thuanthanhtech.demosecurity.entity.User;
import com.thuanthanhtech.demosecurity.repository.RoleRepository;
import com.thuanthanhtech.demosecurity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@RequiredArgsConstructor
public class DataSeeder implements ApplicationListener<ContextRefreshedEvent> {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent arg0) {
        Role role = new Role();
        if (roleRepository.findByName("ROLE_ADMIN") == null) {
            role.setName("ROLE_ADMIN");
            roleRepository.save(role);
        }else if (roleRepository.findByName("ROLE_USER") == null) {
            role.setName("ROLE_USER");
            roleRepository.save(role);
        }

        if (userRepository.findByEmail("admin@gmail.com") == null) {
            Set<Role> roles = new HashSet<>();
            Role roleAdmin = roleRepository.findByName("ROLE_ADMIN");
            roles.add(roleAdmin);

            User admin = new User();

            admin.setEmail("admin@gmail.com");
            admin.setPassword(passwordEncoder().encode("admin"));
            admin.setUsername("admin");
            admin.setRole(roles);
            userRepository.save(admin);
            log.info(admin.getUsername() +" user is added. " + admin.getUsername() + " has Admin Role.");
        }

        if (userRepository.findByEmail("khoinguyen.shini.2@gmail.com") == null) {
            Set<Role> roles = new HashSet<>();
            Role roleUser = roleRepository.findByName("ROLE_USER");
            roles.add(roleUser);

            User user = new User();

            user.setEmail("khoinguyen.shini.2@gmail.com");
            user.setPassword(passwordEncoder().encode("123456"));
            user.setUsername("khoinguyen");
            user.setRole(roles);
            userRepository.save(user);
            log.info(user.getUsername() +" user is added. " + user.getUsername() + " has Admin Role.");
        }
    }
}