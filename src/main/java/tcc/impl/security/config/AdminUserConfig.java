package tcc.impl.security.config;

import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import tcc.impl.security.entities.Role;
import tcc.impl.security.entities.User;
import tcc.impl.security.repository.RoleRepository;
import tcc.impl.security.repository.UserRepository;

import java.util.Set;

@Configuration
public class AdminUserConfig implements CommandLineRunner {

    private RoleRepository roleRepository;
    private UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder;

    public AdminUserConfig(RoleRepository roleRepository, UserRepository userRepository,
                           BCryptPasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        var roleAdmin = roleRepository.findByName(Role.Values.ADMIN.name());
        var userAdmin = userRepository.findByUsername("admin");
        userAdmin.ifPresentOrElse(
                user -> {
                    System.out.println("admin already exists");
                },
                () -> {
                    var user = new User();
                    user.setUsername("admin");
                    user.setPassword(passwordEncoder.encode("123"));
                    user.setRoles(Set.of(roleAdmin));
                    userRepository.save(user);
                }
        );

        var roleSuperAdmin = roleRepository.findByName(Role.Values.SUPER_ADMIN.name());
        var userSuperAdmin = userRepository.findByUsername("super_admin");
        userSuperAdmin.ifPresentOrElse(
                user -> {
                    System.out.println("super admin already exists");
                },
                () -> {
                    var user = new User();
                    user.setUsername("super_admin");
                    user.setPassword(passwordEncoder.encode("123"));
                    user.setRoles(Set.of(roleSuperAdmin,roleAdmin));
                    userRepository.save(user);
                }
        );
    }
}
