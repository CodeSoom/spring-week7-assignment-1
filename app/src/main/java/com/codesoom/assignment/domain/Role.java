package com.codesoom.assignment.domain;

import com.codesoom.assignment.enums.RoleEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.A;

import javax.persistence.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "role_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private RoleEnum roleEnum;

    public Role(RoleEnum roleEnum) {
        this.roleEnum = roleEnum;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
