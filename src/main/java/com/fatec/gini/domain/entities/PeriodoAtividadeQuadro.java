package com.fatec.gini.domain.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fatec.gini.domain.models.Status;
import com.fatec.gini.domain.models.TipoPeridoAtividadeQuadro;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "periodo_atividade_quadro")
public class PeriodoAtividadeQuadro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_periodo_atividade_quadro")
    private Long id;

    private Integer ano;
    private Integer periodo;

    @Column(name = "data_inicio")
    private LocalDate dataInicio;

    @Column(name = "data_fim")
    private LocalDate dataFim;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(name = "tipo_periodo_atividade_quadro")
    private TipoPeridoAtividadeQuadro tipoPeridoAtividadeQuadro;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "periodoAtividadeQuadro")
    private List<QuadroHorario> quadroHorarios;

    public PeriodoAtividadeQuadro(Integer ano, Integer periodo, LocalDate dataInicio, LocalDate dataFim,
            Status status) {
        this.ano = ano;
        this.periodo = periodo;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.status = status;
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
        PeriodoAtividadeQuadro other = (PeriodoAtividadeQuadro) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}
