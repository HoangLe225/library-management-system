package com.spring.project.library.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "name")
    private String name;
    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true)
//    @EqualsAndHashCode.Exclude
//    @ToString.Exclude
    private Set<UserRole> userRoles;
}
