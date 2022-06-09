package com.superbox.study.config;

import com.superbox.study.payload.MessageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@ControllerAdvice
public class RestControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = { Exception.class })
    public ResponseEntity<?> unknownException(Exception ex) {
        log.error(ex.getMessage());
        ex.printStackTrace();
        return ResponseEntity.internalServerError().body(new MessageResponse(ex.getLocalizedMessage()));
    }

    @ExceptionHandler(value = { ResponseStatusException.class })
    public ResponseEntity<?> responseStatusException(ResponseStatusException e) {
        log.error(e.getMessage());
        e.printStackTrace();
        return ResponseEntity.status(e.getStatus()).body(new MessageResponse(e.getReason()));
    }
}
