package com.fatec.horario.infrastructure.mappers;

import com.fatec.horario.domain.entities.Disciplina;
import com.fatec.horario.dto.Disciplina.DisciplinaRequest;
import com.fatec.horario.dto.Disciplina.DisciplinaResponse;

public class DisciplinaMapper {

    public static Disciplina toEntity(DisciplinaRequest request) {
        Disciplina disciplina = new Disciplina();
        
        disciplina.setNome(request.nome());
        disciplina.setCargaHoraria(request.cargaHoraria());
        disciplina.setTipoDisciplina(request.tipoDisciplina());
        disciplina.setPeriodo(request.periodo());
        disciplina.setModalidade(request.modalidade());
        disciplina.setCodDisciplina(request.codDisciplina());
        disciplina.setCor(request.cor());
        
        // Os objetos Curso e TipoSala são setados no Service 
        // usando os IDs (request.idCurso() e request.idTipoSala())
        
        return disciplina;
    }

    public static DisciplinaResponse toResponse(Disciplina disciplina) {
        return new DisciplinaResponse(
                disciplina.getId(),
                disciplina.getNome(),
                disciplina.getCargaHoraria(),
                disciplina.getTipoDisciplina(),
                disciplina.getPeriodo(),
                disciplina.getModalidade(),
                disciplina.getCodDisciplina(),
                disciplina.getCor(),
                // Verificação de nulidade para evitar NullPointerException caso o curso/sala não estejam carregados
                disciplina.getCurso() != null ? disciplina.getCurso().getNome() : null,
                disciplina.getTipoSala() != null ? disciplina.getTipoSala().getNome() : null
        );
    }
}