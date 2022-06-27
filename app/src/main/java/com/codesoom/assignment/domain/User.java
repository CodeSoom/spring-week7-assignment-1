package com.codesoom.assignment.domain;

import com.codesoom.assignment.enums.RoleEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private Long id;

    private String email;

    private String name;

    private String password;

    @OneToMany(mappedBy = "user_id")
    @Builder.Default
    private List<Role> roles = new ArrayList<>();

    @Builder.Default
    private boolean deleted = false;

    public void changeWith(User source) {
        name = source.name;
        password = source.password;
    }

    //TODO 이렇게 하는 것이 괜찮을지..?
    public void addUserRole() {
        role = Role.builder().roleEnum(RoleEnum.USER).build();
        roles.add(role);
        role.setUser(this);
    }

    public void destroy() {
        deleted = true;
    }

    public boolean authenticate(String password) {
        return !deleted && password.equals(this.password);
    }
}
