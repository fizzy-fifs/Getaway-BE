package com.example.holidayplanner.user;

import com.example.holidayplanner.user.privilege.Privilege;
import com.example.holidayplanner.user.privilege.PrivilegeRepository;
import com.example.holidayplanner.user.role.Role;
import com.example.holidayplanner.user.role.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private final RoleRepository roleRepository;

    @Autowired
    private final PrivilegeRepository privilegeRepository;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    public SetupDataLoader(RoleRepository roleRepository, PrivilegeRepository privilegeRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.privilegeRepository = privilegeRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }


    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {

        if (isAlreadySetup()) { return; }

        //Create read and write privileges
        var readPrivilege = createPrivilegeIfNotFound("READ_PRIVILEGE");
        var writePrivilege = createPrivilegeIfNotFound("WRITE_PRIVILEGE");

        //Create admin and user privileges
        List<Privilege> adminPrivileges = Arrays.asList(readPrivilege, writePrivilege);
        List<Privilege> userPrivileges = Collections.singletonList(readPrivilege);

        //Create admin and user roles
        createRoleIfNotFound("ROLE_ADMIN", adminPrivileges);
        createRoleIfNotFound("ROLE_USER", userPrivileges);

        Role adminRole = roleRepository.findByName("ROLE_ADMIN");

        //Create and save an initial admin
        User user = new User();
        user.setFirstName("Admin");
        user.setEmail("admin@mail.com");
        user.setPassword(passwordEncoder.encode("Admin123"));
        user.setRoles(Collections.singletonList(adminRole));
        user.setEnabled(true);
        userRepository.save(user);
    }



    @Transactional
    Role createRoleIfNotFound(String name, Collection<Privilege> privileges) {

        //Check for the existence of a role in the db
        Role role = roleRepository.findByName(name);

        //Create one if none exists
        if(role == null) {
            role = new Role(name);
            role.setPrivileges(privileges);
            roleRepository.save(role);
        }

        return role;
    }

    @Transactional
    Privilege createPrivilegeIfNotFound(String name) {

        //Check for the existence of a privilege in the db
        Privilege privilege = privilegeRepository.findByName(name);

        //Create one if none exists
        if(privilege == null) {
            privilege = new Privilege(name);
            privilegeRepository.save(privilege);
        }

        return privilege;
    }

    private boolean isAlreadySetup() {
        Role adminRole = roleRepository.findByName("ROLE_ADMIN");
        Role userRole = roleRepository.findByName("ROLE_USER");
        return adminRole != null || userRole != null;
    }
}
