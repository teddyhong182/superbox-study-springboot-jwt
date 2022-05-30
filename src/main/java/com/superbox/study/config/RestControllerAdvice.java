package com.superbox.study.config;

import com.superbox.study.payload.MessageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.NestedRuntimeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@ControllerAdvice
public class RestControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = { Exception.class })
    public ResponseEntity<?> unknownException(Exception ex) {
        log.error(ex.getMessage());
        ex.printStackTrace();
        return ResponseEntity.internalServerError().body(new MessageResponse("서버가 정상작동하지 않습니다. 다시 시도해 주시기 바랍니다."));
    }

    @ExceptionHandler(value = { ResponseStatusException.class })
    public ResponseEntity<?> responseStatusException(ResponseStatusException e) {
        log.error(e.getMessage());
        e.printStackTrace();
        return ResponseEntity.badRequest().body(new MessageResponse(e.getReason()));
    }
}
