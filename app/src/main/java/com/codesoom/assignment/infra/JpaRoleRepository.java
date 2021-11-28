package com.codesoom.assignment.infra;

import com.codesoom.assignment.domain.Role;
import com.codesoom.assignment.domain.RoleRepository;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface JpaRoleRepository extends RoleRepository, CrudRepository<Role, Long> {

    List<Role> findAllByUserId(Long userId);
}
