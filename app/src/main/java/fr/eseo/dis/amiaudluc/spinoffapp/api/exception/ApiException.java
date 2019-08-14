package fr.eseo.dis.amiaudluc.spinoffapp.api.exception;

/**
 * Created by lucasamiaud on 03/03/2019.
 */

public class ApiException extends RuntimeException {

    public ApiException(String message) {
        super(message);
    }

    public ApiException(Throwable cause) {
        super(cause);
    }

}
