package ru.klokov.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "emitter_owner")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class EmitterOwner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "owner_name", nullable = false, unique = true)
    private String name;

}