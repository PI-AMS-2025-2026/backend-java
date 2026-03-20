package com.fatec.horario.domain.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "dia_semana")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DiaSemana {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String nome;


}