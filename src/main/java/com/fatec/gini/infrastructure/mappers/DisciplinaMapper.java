```java id="z7t9pw"
package com.fatec.gini.infrastructure.mappers;

import com.fatec.gini.domain.entities.Disciplina;
import com.fatec.gini.dto.disciplina.DisciplinaRequest;
import com.fatec.gini.dto.disciplina.DisciplinaResponse;

public class DisciplinaMapper {

    public static Disciplina toEntity(DisciplinaRequest request) {

        if (request == null) {
            return null;
        }

        Disciplina disciplina = new Disciplina();

        disciplina.setNome(request.nome());
        disciplina.setCargaHoraria(request.cargaHoraria());
        disciplina.setTipoDisciplina(request.tipoDisciplina());
        disciplina.setPeriodo(request.periodo());
        disciplina.setModalidade(request.modalidade());
        disciplina.setCodDisciplina(request.codDisciplina());
        disciplina.setCor(request.cor());

        return disciplina;
    }

    public static DisciplinaResponse toResponse(Disciplina disciplina) {

        if (disciplina == null) {
            return null;
        }

        return new DisciplinaResponse(
                disciplina.getId(),
                disciplina.getNome(),
                disciplina.getCargaHoraria(),
                disciplina.getTipoDisciplina(),
                disciplina.getPeriodo(),
                disciplina.getModalidade(),
                disciplina.getCodDisciplina(),
                disciplina.getCor(),
                disciplina.getCurso() != null
                        ? CursoMapper.toResponse(disciplina.getCurso())
                        : null,
                disciplina.getTipoSala() != null
                        ? TipoSalaMapper.toResponse(disciplina.getTipoSala())
                        : null,
                disciplina.getCreatedAt(),
                disciplina.getUpdatedAt());
    }
}
```
