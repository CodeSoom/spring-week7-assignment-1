package com.codesoom.assignment.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role,Long> {
  Role save(Role role);

  Optional<Role> findByName(String user);
}
