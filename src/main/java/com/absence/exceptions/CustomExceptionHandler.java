package com.absence.exceptions;

import com.absence.auth.dtos.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler {

    private static final String ERROR = "error";

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> generalException() {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseDto.builder()
                        .code(HttpStatus.INTERNAL_SERVER_ERROR.toString())
                        .status(ERROR)
                        .message("Oops, something went wrong!")
                        .build());
    }

    @ExceptionHandler({BadCredentialException.class})
    public ResponseEntity<Object> badCredentialException(Exception exception) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ResponseDto.builder()
                        .code(HttpStatus.UNAUTHORIZED.toString())
                        .status(ERROR)
                        .message(exception.getMessage())
                        .build());
    }

    @ExceptionHandler({ResourceNotFoundException.class})
    public ResponseEntity<Object> resourceNotFoundException(Exception exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ResponseDto.builder()
                        .code(HttpStatus.BAD_REQUEST.toString())
                        .status(ERROR)
                        .message(exception.getMessage())
                        .build());
    }

    @ExceptionHandler({SendEmailException.class})
    public ResponseEntity<Object> sendEmailException(Exception exception) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseDto.builder()
                        .code(HttpStatus.INTERNAL_SERVER_ERROR.toString())
                        .status(ERROR)
                        .message(exception.getMessage())
                        .build());
    }

}
