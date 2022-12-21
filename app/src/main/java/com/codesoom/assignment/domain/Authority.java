package com.codesoom.assignment.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "authority")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Authority {

    @Id
    @Column(name = "authority_name")
    private String authorityName;
}
