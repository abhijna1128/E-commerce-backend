package com.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Data; // optional if using Lombok

@Data // generates getters/setters/toString automatically
@Entity
@Table(name = "users") // optional, good practice
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role; // Admin or Customer
}
