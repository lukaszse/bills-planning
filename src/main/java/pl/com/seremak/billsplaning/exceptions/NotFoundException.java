package pl.com.seremak.billsplaning.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;
    public static final String OBJECT_NOT_FOUND_ERROR_MSG = "The searched object was not found.";

    public NotFoundException() {
        this(OBJECT_NOT_FOUND_ERROR_MSG);
    }

    public NotFoundException(final String message) {
        super(message);
    }
}