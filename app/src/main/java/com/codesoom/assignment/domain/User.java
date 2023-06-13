/**
 * 복호화가 불가능한 암호화 : 123  => 6
 * Hash => data -> n-bit
 * Hash Table (key ->(string) -> integer(32-bits)) => Hash 충돌
 * 123 -> 6
 */

package com.codesoom.assignment.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue
    private Long id;

    @Builder.Default
    private String email = "";

    @Builder.Default
    private String name = "";

    @Builder.Default
    private String password = "";

    @Enumerated(value = EnumType.STRING)
    private UserType userType;
    @Builder.Default
    private boolean deleted = false;

    public User() {
        this.email = "";
        this.name = "";
        this.password = "";
    }

    public void changeWith(User source) {
        name = source.name;
    }

    public void destroy() {
        deleted = true;
    }

    public boolean authenticate(String password,
                                PasswordEncoder passwordEncoder) {
        return !deleted && passwordEncoder.matches(password, this.password);
    }

    public void changePassword(String password,
                               PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(password);
    }
}
