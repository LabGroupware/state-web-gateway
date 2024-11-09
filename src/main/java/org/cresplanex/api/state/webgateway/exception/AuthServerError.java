package org.cresplanex.api.state.webgateway.exception;

import lombok.Getter;
import org.cresplanex.api.state.webgateway.constants.ServerErrorCode;

@Getter
public class AuthServerError {

    private final String code;
    private final String message;

    public AuthServerError() {
        this(ServerErrorCode.INTERNAL_SERVER_ERROR, "Internal Server Error");
    }

    public AuthServerError(String code, String message) {
        this.code = code;
        this.message = message;
    }

}
