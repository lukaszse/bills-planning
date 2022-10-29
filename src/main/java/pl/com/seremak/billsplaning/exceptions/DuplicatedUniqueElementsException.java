package pl.com.seremak.billsplaning.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class DuplicatedUniqueElementsException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;
    private static final String DUPLICATED_ELEMENTS_ERROR_MSG = """
            Internal error occurred. Multiple elements found for elements which should be unique.""";

    public DuplicatedUniqueElementsException() {
        this(DUPLICATED_ELEMENTS_ERROR_MSG);
    }

    public DuplicatedUniqueElementsException(final String message) {
        super(message);
    }
}