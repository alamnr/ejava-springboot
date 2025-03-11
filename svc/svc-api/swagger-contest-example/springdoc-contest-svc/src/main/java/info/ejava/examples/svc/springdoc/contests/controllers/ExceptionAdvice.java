package info.ejava.examples.svc.springdoc.contests.controllers;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import info.ejava.examples.common.exceptions.ClientErrorException.BadRequestException;
import info.ejava.examples.common.exceptions.ClientErrorException.InvalidInputException;
import info.ejava.examples.common.exceptions.ClientErrorException.NotFoundException;
import info.ejava.examples.common.exceptions.ServerErrorException.InternalErrorException;
import info.ejava.examples.svc.springdoc.contest.dto.MessageDTO;
import lombok.extern.slf4j.Slf4j;

/*
 * This class provides custom error handling for exceptions thrown by the 
 * controller. It is one of several technique offered by Spring and selected 
 * primarily because we retain full control over the response and response header 
 * returned to the caller
 */
@RestControllerAdvice
@Slf4j
public class ExceptionAdvice {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<MessageDTO> handle(NotFoundException ex){
        return buildResponse(HttpStatus.NOT_FOUND,ex.getMessage(),ex.getDate());
    }

    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<MessageDTO> handle(InvalidInputException ex) {
        return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), ex.getDate());
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<MessageDTO> handle(BadRequestException ex){
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage() , ex.getDate());
    }

    @ExceptionHandler(InternalErrorException.class)
    public ResponseEntity<MessageDTO> handle(InternalErrorException ex) {
        log.warn("{}", ex.getMessage(), ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), ex.getDate());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<MessageDTO> handle(RuntimeException ex){
        log.warn("{}", ex.getMessage(), ex);
        String text = String.format("unexpected error executing request : %d", ex.getMessage());
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, text, null);
    }

    private ResponseEntity<MessageDTO> buildResponse(HttpStatus status, String text, Instant date) {
        String url = ServletUriComponentsBuilder.fromCurrentRequestUri().toString();
        MessageDTO message = MessageDTO.builder()
                                .url(url)
                                .text(text)
                                .date(date != null ? date : Instant.now() )
                                .build();
        ResponseEntity<MessageDTO> response = ResponseEntity
                                                .status(status)
                                                .body(message);

        return response;
    }

}
