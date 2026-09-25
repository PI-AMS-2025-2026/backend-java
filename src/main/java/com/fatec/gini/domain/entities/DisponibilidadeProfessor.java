package com.fatec.gini.domain.entities;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "disponibilidade_professor")
public class DisponibilidadeProfessor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_disponibilidade_professor")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_professor", nullable = false)
    private Professor professor;

    // CORREÇÃO: força o Hibernate a persistir o nome do enum (ex: "SEGUNDA") em vez do ordinal (0,1,2...),
    // evitando que os dados fiquem inconsistentes caso a ordem do enum DiaSemana mude no futuro
    @Enumerated(EnumType.STRING)
    private DiaSemana diaSemana;

    @ManyToOne
    @JoinColumn(name = "id_bloco_horario", nullable = false)
    private BlocoHorario blocoHorario;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public DisponibilidadeProfessor(Professor professor, DiaSemana diaSemana, BlocoHorario blocoHorario) {
        this.professor = professor;
        this.diaSemana = diaSemana;
        this.blocoHorario = blocoHorario;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DisponibilidadeProfessor other = (DisponibilidadeProfessor) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}