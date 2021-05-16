// 암호화 : 평문 -> 암호 (1 2 3) => (4 5 6)
// 복호화 : 암호 -> 평문 (4 5 6) => (1 2 3)
// 복호화가 불가능한 암호화 123 => 6
// Hash => data -> n-bit
// Hash Table (key(string) -> integer(32-bits)) => Hash 충돌
// 123 -> 6
// 321 -> 6
// 따라서 Hash 충돌이 적은 알고리즘을 만드는것이 중요하다.
package com.codesoom.assignment.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Builder
@NoArgsConstructor
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

    @Builder.Default
    private boolean deleted = false;

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
