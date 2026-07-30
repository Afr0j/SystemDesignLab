package com.systemdesignlab.urlshortener.exception;

import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.systemdesignlab.urlshortener.dto.ErrorResponse;
import com.systemdesignlab.urlshortener.service.UrlService;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {
	private static final Logger log =LoggerFactory.getLogger(GlobalExceptionHandler.class);


    @ExceptionHandler(UrlNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUrlNotFound(
            UrlNotFoundException ex,
            HttpServletRequest request) {

        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
        
        log.warn(
        	    "Short URL not found. path={}",
        	    request.getRequestURI()
        	);

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }
}