package com.codesoom.assignment.domain;

import java.util.List;

public interface RoleRepository {
    List<Role> findAllByEmail(String email);

    Role save(Role role);
}
