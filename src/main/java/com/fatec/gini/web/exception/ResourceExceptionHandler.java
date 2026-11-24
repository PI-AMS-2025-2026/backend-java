package com.fatec.gini.web.exception;

import java.time.Instant;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class ResourceExceptionHandler {

    @ExceptionHandler(AlocacaoLoteException.class)
    public ResponseEntity<? extends StandardError> alocacaoLoteException(
            AlocacaoLoteException exception, HttpServletRequest request) {
        var response = switch (exception.getCause()) {
            case BusinessException cause -> businessException(cause, request);
            case ParameterException cause -> parameterException(cause, request);
            case EntityNotFoundException cause -> entityNotFoundException(cause, request);
            case ConstraintViolationException cause -> {
                var error = criarErroValidacao(request);
                cause.getConstraintViolations().forEach(violation ->
                        adicionarErroCampo(error, violation.getPropertyPath().toString(), violation.getMessage()));
                yield ResponseEntity.unprocessableContent().body(error);
            }
            default -> unexpectedException(exception, request);
        };
        if (response.getBody() != null) {
            response.getBody().setAlocacaoIndice(exception.getIndice());
        }
        return response;
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<StandardError> entityNotFoundException(
            EntityNotFoundException exception,
            HttpServletRequest request) {

        HttpStatus status = HttpStatus.NOT_FOUND;

        StandardError error = new StandardError();
        error.setTimeStamp(Instant.now());
        error.setStatus(status.value());
        error.setError("Recurso não encontrado");
        error.setMessage(exception.getMessage());
        error.setPath(request.getRequestURI());

        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(SalaComAlocacoesException.class)
    public ResponseEntity<StandardError> salaComAlocacoesException(
            SalaComAlocacoesException exception, HttpServletRequest request) {
        StandardError error = new StandardError();
        error.setTimeStamp(Instant.now());
        error.setStatus(HttpStatus.CONFLICT.value());
        error.setError("Sala vinculada a alocações");
        error.setMessage(exception.getMessage());
        error.setPath(request.getRequestURI());
        error.setCode("SALA_COM_ALOCACOES");
        error.setLinkedCount(exception.getQuantidade());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(BlocoHorarioComVinculosException.class)
    public ResponseEntity<StandardError> blocoHorarioComVinculosException(
            BlocoHorarioComVinculosException exception, HttpServletRequest request) {
        StandardError error = new StandardError();
        error.setTimeStamp(Instant.now());
        error.setStatus(HttpStatus.CONFLICT.value());
        error.setError("Bloco de horário possui vínculos");
        error.setMessage(exception.getMessage());
        error.setPath(request.getRequestURI());
        error.setCode("BLOCO_HORARIO_COM_VINCULOS");
        error.setAllocationCount(exception.getAlocacoes());
        error.setAvailabilityCount(exception.getDisponibilidades());
        error.setLinkedCount(exception.getAlocacoes() + exception.getDisponibilidades());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<StandardError> authenticationException(
            AuthenticationException exception,
            HttpServletRequest request) {

        HttpStatus status = HttpStatus.UNAUTHORIZED;

        StandardError error = new StandardError();
        error.setTimeStamp(Instant.now());
        error.setStatus(status.value());
        error.setError("Credenciais inválidas");
        error.setMessage("E-mail ou senha inválidos");
        error.setPath(request.getRequestURI());

        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationError> validationException(
            MethodArgumentNotValidException exception,
            HttpServletRequest request) {

        ValidationError error = criarErroValidacao(request);
        exception.getBindingResult().getFieldErrors()
                .forEach(e -> adicionarErroCampo(error, e.getField(), e.getDefaultMessage()));
        return ResponseEntity.unprocessableContent().body(error);
    }

    private ValidationError criarErroValidacao(HttpServletRequest request) {
        ValidationError error = new ValidationError();
        error.setTimeStamp(Instant.now());
        error.setStatus(HttpStatus.UNPROCESSABLE_CONTENT.value());
        error.setError("Erro de validação");
        error.setMessage("Erro de validação nos campos enviados");
        error.setPath(request.getRequestURI());
        return error;
    }

    private void adicionarErroCampo(ValidationError error, String field, String message) {
        error.addError(message);
        error.addFieldError(field, message);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<StandardError> dataIntegrityViolationException(
            DataIntegrityViolationException exception,
            HttpServletRequest request) {

        String causa = exception.getMostSpecificCause().getMessage();
        if (causa != null && causa.contains("uk_periodo_atividade_quadro_ano_periodo_tipo")) {
            HttpStatus status = HttpStatus.CONFLICT;
            StandardError error = new StandardError();
            error.setTimeStamp(Instant.now());
            error.setStatus(status.value());
            error.setError("Erro de regra de negócio");
            error.setMessage("Já existe um período atividade quadro para o ano, período e tipo informados.");
            error.setPath(request.getRequestURI());
            return ResponseEntity.status(status).body(error);
        }

        HttpStatus status = HttpStatus.CONFLICT;

        StandardError error = new StandardError();
        error.setTimeStamp(Instant.now());
        error.setStatus(status.value());
        error.setError("Operação não permitida");
        error.setMessage(
                "A operação não pode ser concluída porque existem registros relacionados. Revise os vínculos e tente novamente.");
        error.setPath(request.getRequestURI());

        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(ParameterException.class)
    public ResponseEntity<StandardError> parameterException(
            ParameterException exception,
            HttpServletRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST;

        StandardError error = new StandardError();
        error.setTimeStamp(Instant.now());
        error.setStatus(status.value());
        error.setError("Erro de parâmetro");
        error.setMessage(exception.getMessage());
        error.setPath(request.getRequestURI());

        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<StandardError> businessException(
            BusinessException exception,
            HttpServletRequest request) {

        HttpStatus status = HttpStatus.CONFLICT;

        StandardError error = new StandardError();
        error.setTimeStamp(Instant.now());
        error.setStatus(status.value());
        error.setError("Erro de regra de negócio");
        error.setMessage(exception.getMessage());
        error.setPath(request.getRequestURI());

        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<StandardError> databaseException(
            DatabaseException exception,
            HttpServletRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST;

        StandardError error = new StandardError();
        error.setTimeStamp(Instant.now());
        error.setStatus(status.value());
        error.setError("Erro de banco de dados");
        error.setMessage(exception.getMessage());
        error.setPath(request.getRequestURI());

        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(RefreshTokenException.class)
    public ResponseEntity<StandardError> refreshTokenException(
            RefreshTokenException exception,
            HttpServletRequest request) {

        HttpStatus status = HttpStatus.UNAUTHORIZED;

        StandardError error = new StandardError();
        error.setTimeStamp(Instant.now());
        error.setStatus(status.value());
        error.setError("Refresh token inválido");
        error.setMessage(exception.getMessage());
        error.setPath(request.getRequestURI());

        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<StandardError> illegalArgumentException(
            IllegalArgumentException exception,
            HttpServletRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST;

        StandardError error = new StandardError();
        error.setTimeStamp(Instant.now());
        error.setStatus(status.value());
        error.setError("Parâmetro inválido");
        error.setMessage(exception.getMessage());
        error.setPath(request.getRequestURI());

        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<StandardError> httpMessageNotReadableException(
            HttpMessageNotReadableException exception,
            HttpServletRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST;
        Throwable cause = exception.getMostSpecificCause();
        String message = (cause != null && cause.getMessage() != null && !cause.getMessage().isBlank())
                ? cause.getMessage()
                : "Corpo da requisição inválido";

        StandardError error = new StandardError();
        error.setTimeStamp(Instant.now());
        error.setStatus(status.value());
        error.setError("Requisição inválida");
        error.setMessage(message);
        error.setPath(request.getRequestURI());

        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<StandardError> responseStatusException(
            ResponseStatusException exception,
            HttpServletRequest request) {

        HttpStatus status = HttpStatus.resolve(exception.getStatusCode().value());
        StandardError error = new StandardError();
        error.setTimeStamp(Instant.now());
        error.setStatus(exception.getStatusCode().value());
        error.setError(status != null ? status.getReasonPhrase() : "Erro na requisição");
        error.setMessage(exception.getReason() != null
                ? exception.getReason()
                : "Não foi possível concluir a operação.");
        error.setPath(request.getRequestURI());

        return ResponseEntity.status(exception.getStatusCode()).body(error);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<StandardError> accessDeniedException(
            AccessDeniedException exception,
            HttpServletRequest request) {

        HttpStatus status = HttpStatus.FORBIDDEN;
        StandardError error = new StandardError();
        error.setTimeStamp(Instant.now());
        error.setStatus(status.value());
        error.setError("Acesso negado");
        error.setMessage(exception.getMessage());
        error.setPath(request.getRequestURI());

        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardError> unexpectedException(
            Exception exception,
            HttpServletRequest request) {

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        StandardError error = new StandardError();
        error.setTimeStamp(Instant.now());
        error.setStatus(status.value());
        error.setError("Erro interno do servidor");
        error.setMessage("Não foi possível concluir a operação. Tente novamente ou contate o suporte.");
        error.setPath(request.getRequestURI());

        return ResponseEntity.status(status).body(error);
    }
}
