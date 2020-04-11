package com.notsoold.restboxes.controller;

import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class InternalServerErrorAdvice {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handle(Exception ex) {
	if (ex instanceof InvalidDataAccessApiUsageException) {
	    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}
	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

}
