package com.fatec.gini.domain.entities;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "historico_alteracao")
public class HistoricoAlteracao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historico_alteracao")
    private Long id;

    @Column(name = "data_alteracao", nullable = false)
    private LocalDate dataAlteracao;

    @Column(nullable = false)
    private String justificativa;

    @Column(name = "campo_alterado", nullable = false)
    private String campoAlterado;

    @Column(name = "valor_antigo")
    private String valorAntigo;

    @Column(name = "valor_novo", nullable = false)
    private String valorNovo;

    @ManyToOne
    @JoinColumn(name = "id_alocacao", nullable = false)
    private Alocacao alocacao;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    public HistoricoAlteracao(LocalDate dataAlteracao, String justificativa, String campoAlterado, String valorAntigo,
            String valorNovo) {
        this.dataAlteracao = dataAlteracao;
        this.justificativa = justificativa;
        this.campoAlterado = campoAlterado;
        this.valorAntigo = valorAntigo;
        this.valorNovo = valorNovo;
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
        HistoricoAlteracao other = (HistoricoAlteracao) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}
