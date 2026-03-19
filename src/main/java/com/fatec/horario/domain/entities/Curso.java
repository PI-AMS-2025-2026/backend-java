package com.fatec.horario.domain.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "curso")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_curso;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String periodicidade; // Semestral ou Anual

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private Integer duracao;
}