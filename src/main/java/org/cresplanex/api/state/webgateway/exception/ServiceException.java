package org.cresplanex.api.state.webgateway.exception;

public abstract class ServiceException extends RuntimeException {
    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    abstract public ApplicationServerError getErrorCode();
}
