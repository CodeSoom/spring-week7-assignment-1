package com.codesoom.assignment.repository;

import com.codesoom.assignment.domain.Authority;
import com.codesoom.assignment.domain.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    List<User> findAll();

    User save(User user);

    boolean existsByEmail(String email);

    Optional<User> findById(Long id);

    Optional<User> findByIdAndDeletedIsFalse(Long id);

    Optional<User> findByEmail(String email);

    @Query(
            value = "select authority_name from user_authority where user_id = ?",
            nativeQuery = true
    )
    Set<Authority> findAuthorityNameById(long id);
}
