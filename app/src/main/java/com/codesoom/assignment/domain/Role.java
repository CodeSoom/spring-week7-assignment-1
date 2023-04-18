package com.codesoom.assignment.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.checkerframework.common.value.qual.ArrayLen;

@Entity
@NoArgsConstructor
public class Role {

  @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long userId;

  @Getter
  private String name;


  public Role(Long userId, String name) {
    this.userId = userId;
    this.name = name;
  }
  public Role(String name) {
    this(null,name);
  }

}