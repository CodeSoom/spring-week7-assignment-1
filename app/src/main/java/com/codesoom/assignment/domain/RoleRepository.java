package com.codesoom.assignment.domain;

import java.util.Optional;

public interface RoleRepository {

    Role save(Role role);

    Optional<Role> findByName(String name);
}
