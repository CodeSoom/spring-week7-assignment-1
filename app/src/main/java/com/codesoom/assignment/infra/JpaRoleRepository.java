package com.codesoom.assignment.infra;

import com.codesoom.assignment.domain.Role;
import com.codesoom.assignment.domain.RoleRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface JpaRoleRepository
        extends RoleRepository, CrudRepository<Role, Long> {

    Role save(Role role);

    Optional<Role> findByName(String name);

}
