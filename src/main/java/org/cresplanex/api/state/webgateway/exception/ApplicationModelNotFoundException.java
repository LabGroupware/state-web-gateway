package org.cresplanex.api.state.webgateway.exception;

import org.cresplanex.api.state.common.constants.WebGatewayApplicationCode;

public abstract class ApplicationModelNotFoundException extends RuntimeException {

    public ApplicationModelNotFoundException(String message) {
        super(message);
    }

    public ApplicationModelNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    abstract String getErrorCaption();

    abstract String getFindType();

    abstract String getFindValue();

    abstract String getErrorCode();
}
