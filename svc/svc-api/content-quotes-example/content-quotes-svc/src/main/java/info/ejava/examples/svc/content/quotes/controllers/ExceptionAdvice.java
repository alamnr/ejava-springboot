package info.ejava.examples.svc.content.quotes.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import info.ejava.examples.common.dto.MessageDTO;
import info.ejava.examples.common.exceptions.ClientErrorException.BadRequestException;
import info.ejava.examples.common.exceptions.ClientErrorException.InvalidInputException;
import info.ejava.examples.common.exceptions.ClientErrorException.NotFoundException;
import info.ejava.examples.common.exceptions.ServerErrorException.InternalErrorException;
import lombok.extern.slf4j.Slf4j;

/*
 * This class provide custom error handling for exceptions thrown by the 
 * controller. It is one of the several technique offered by Spring and 
 * selected primarily because we retain full control over the response 
 * and response header returned to the caller
 */

 @RestControllerAdvice
 @Slf4j
public class ExceptionAdvice {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<MessageDTO> handle(NotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<MessageDTO> handle(InvalidInputException ex){
        return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<MessageDTO> handle(BadRequestException ex){
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(InternalErrorException.class)
    public ResponseEntity<MessageDTO> handle(InternalErrorException ex){
        log.warn("{} - {}", ex.getMessage(), ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<MessageDTO> handle(RuntimeException ex){
        log.warn("{}  - {} ", ex.getMessage(), ex);
        String message = String.format("Unexpected error executing request : %s ", ex.toString());
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    protected ResponseEntity<MessageDTO> buildResponse(HttpStatus status, String text) {
        String url = ServletUriComponentsBuilder.fromCurrentRequest().toUriString();
        MessageDTO message = MessageDTO.builder()
                                .url(url).message(text).build();
        
        ResponseEntity<MessageDTO> response = ResponseEntity.status(status).body(message);
        return response;
    }
}
