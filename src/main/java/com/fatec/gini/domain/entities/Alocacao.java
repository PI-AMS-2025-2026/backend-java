package com.fatec.gini.domain.entities;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@Table(name = "alocacao")
public class Alocacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_alocacao")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_turma", nullable = false)
    private Turma turma;

    @ManyToOne
    @JoinColumn(name = "id_disciplina", nullable = false)
    private Disciplina disciplina;

    @ManyToOne
    @JoinColumn(name = "id_sala", nullable = false)
    private Sala sala;

    @ManyToOne
    @JoinColumn(name = "id_professor", nullable = false)
    private Professor professor;

    private DiaSemana diaSemana;

    @ManyToOne
    @JoinColumn(name = "id_bloco_horario", nullable = false)
    private BlocoHorario blocoHorario;

    @ManyToOne
    @JoinColumn(name = "id_quadro_horario", nullable = false)
    private QuadroHorario quadroHorario;

    @CreationTimestamp 
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp 
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "alocacao")
    private List<HistoricoVersaoAlocacao> historicoAlteracoes;

    public Alocacao(Turma turma, Disciplina disciplina, Sala sala, Professor professor, DiaSemana diaSemana,
            BlocoHorario blocoHorario, QuadroHorario quadroHorario) {
        this.turma = turma;
        this.disciplina = disciplina;
        this.sala = sala;
        this.professor = professor;
        this.diaSemana = diaSemana;
        this.blocoHorario = blocoHorario;
        this.quadroHorario = quadroHorario;
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
        Alocacao other = (Alocacao) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Alocacao [id=" + id + ", turma=" + turma + ", disciplina=" + disciplina + ", sala=" + sala
                + ", professor=" + professor + ", diaSemana=" + diaSemana + ", blocoHorario=" + blocoHorario
                + ", quadroHorario="
                + quadroHorario + "]";
    }

}
