package com.codesoom.assignment.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository {

  List<Role> findAllByUserId(Long userId);

  Role save(Role any);
}
