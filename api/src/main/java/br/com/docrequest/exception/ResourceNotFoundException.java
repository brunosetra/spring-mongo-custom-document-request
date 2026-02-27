package br.com.docrequest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public static ResourceNotFoundException of(String resourceType, String identifier) {
        return new ResourceNotFoundException(
            String.format("%s not found with identifier: %s", resourceType, identifier));
    }
}
