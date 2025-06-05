package com.example.userservice.config;

import com.example.userservice.entity.Permission;
import com.example.userservice.entity.Role;
import com.example.userservice.entity.RoleName;
import com.example.userservice.entity.User;
import com.example.userservice.entity.UserStatus;
import com.example.userservice.repository.PermissionRepository;
import com.example.userservice.repository.RoleRepository;
import com.example.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        try {
            initializePermissions();
            initializeRoles();
            initializeAdminUser();
            System.out.println("✅ Data initialization completed successfully!");
        } catch (Exception e) {
            System.err.println("❌ Error during data initialization: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initializePermissions() {
        String[] permissions = {
                "USER_CREATE", "USER_READ", "USER_UPDATE", "USER_DELETE",
                "CLIENT_CREATE", "CLIENT_READ", "CLIENT_UPDATE", "CLIENT_DELETE",
                "PET_CREATE", "PET_READ", "PET_UPDATE", "PET_DELETE",
                "APPOINTMENT_CREATE", "APPOINTMENT_READ", "APPOINTMENT_UPDATE", "APPOINTMENT_DELETE",
                "MEDICAL_CREATE", "MEDICAL_READ", "MEDICAL_UPDATE", "MEDICAL_DELETE",
                "INVENTORY_CREATE", "INVENTORY_READ", "INVENTORY_UPDATE", "INVENTORY_DELETE",
                "BILLING_CREATE", "BILLING_READ", "BILLING_UPDATE", "BILLING_DELETE",
                "REPORT_READ", "REPORT_GENERATE"
        };

        for (String permissionName : permissions) {
            if (!permissionRepository.existsByName(permissionName)) {
                Permission permission = new Permission(permissionName, "Auto-generated permission");
                permissionRepository.save(permission);
                System.out.println("✅ Created permission: " + permissionName);
            }
        }
    }

    private void initializeRoles() {
        for (RoleName roleName : RoleName.values()) {
            if (!roleRepository.existsByName(roleName)) {
                Role role = new Role(roleName, roleName.getDescription());
                roleRepository.save(role);
                System.out.println("✅ Created role: " + roleName);
            }
        }
    }

    private void initializeAdminUser() {
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@veterinary.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFirstName("Super");
            admin.setLastName("Admin");
            admin.setStatus(UserStatus.ACTIVE);

            Role adminRole = roleRepository.findByName(RoleName.ADMIN)
                    .orElseThrow(() -> new RuntimeException("Admin role not found"));

            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);
            admin.setRoles(roles);

            userRepository.save(admin);
            System.out.println("✅ Created admin user: admin/admin123");
        }
    }
}