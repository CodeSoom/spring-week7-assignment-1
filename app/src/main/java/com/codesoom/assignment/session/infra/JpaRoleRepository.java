package com.codesoom.assignment.session.infra;

import com.codesoom.assignment.session.domain.Role;
import com.codesoom.assignment.session.domain.RoleRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface JpaRoleRepository
        extends RoleRepository, CrudRepository<Role, Long> {
    List<Role> findAllByUserId(Long userId);
    Role save(Role role);
}
