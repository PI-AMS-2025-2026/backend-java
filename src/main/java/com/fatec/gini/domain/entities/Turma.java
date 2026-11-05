
package com.fatec.gini.domain.entities;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fatec.gini.domain.models.Status;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(
    name = "turma",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_turma_curso_ano_periodo",
            columnNames = {"id_curso", "ano", "periodo"}
        )
    }
)
public class Turma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_turma")
    private Long id;

    @Column(nullable = false)
    private String codigo;

    @Column(nullable = false)
    private Integer periodo;

    @Column(nullable = false)
    private Integer ano;

    @Column(name = "numero_alunos", nullable = false)
    private Integer numeroAlunos;

    @ManyToOne
    @JoinColumn(name = "id_curso", nullable = false)
    private Curso curso;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.ATIVO;

    @OneToMany(mappedBy = "turma")
    private List<Alocacao> alocacoes;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Turma(
            String codigo,
            Integer periodo,
            Integer ano,
            Integer numeroAlunos) {

        this.codigo = codigo;
        this.periodo = periodo;
        this.ano = ano;
        this.numeroAlunos = numeroAlunos;
        this.status = Status.ATIVO;
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
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Turma other = (Turma) obj;

        if (id == null) {
            return other.id == null;
        }

        return id.equals(other.id);
    }
}
