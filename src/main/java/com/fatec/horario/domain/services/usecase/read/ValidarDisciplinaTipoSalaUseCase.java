package com.fatec.horario.domain.services.usecase.read;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fatec.horario.domain.entities.Disciplina;
import com.fatec.horario.domain.entities.Sala;
import com.fatec.horario.web.exception.BusinessException; 

@Service 
public class ValidarDisciplinaTipoSalaUseCase {

    @Transactional(readOnly = true)
    public void validarCompatibilidadeDisciplinaSala(Disciplina disciplina, Sala sala) {
        
        // verifica se disciplina e sala não são nulas, se forem ai retorna uma exception
        if (disciplina == null || sala == null) {
            throw new BusinessException("Disciplina e Sala devem ser fornecidas para validação.");
        }

        // aqui é para verificar se o tipo sala foi preenchido na alocação, caso nao for, ai o sistema da essa mensagem
        if (disciplina.getTipoSala() == null || sala.getTipoSala() == null) {
            throw new BusinessException("Tipo de sala não definido para a disciplina ou para a sala.");
        }

        // se os ids forem incompativeis ai ele barra e mostra a mensagem e nao deixa o usuario seguir para a alocação
        boolean tipoSalaInvalido = !disciplina.getTipoSala().getId()
                .equals(sala.getTipoSala().getId());

        if (tipoSalaInvalido) {
            throw new BusinessException("A sala informada é incompatível com o tipo de sala exigido pela disciplina.");
        }
    }
}