package tcc.impl.security.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import tcc.impl.security.entities.Role;
import tcc.impl.security.repository.RoleRepository;

import java.util.List;

@RestController
public class RoleController {

    private final RoleRepository roleRepository;

    public RoleController(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }


    @GetMapping("/roles")
    @PreAuthorize("hasAuthority('super_admin')")
    public ResponseEntity<List<Role>> listRoles() {
        var roles = roleRepository.findAll();
        return ResponseEntity.ok(roles);
    }
}
