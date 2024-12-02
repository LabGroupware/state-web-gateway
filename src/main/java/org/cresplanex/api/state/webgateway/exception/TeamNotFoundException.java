package org.cresplanex.api.state.webgateway.exception;

import lombok.Setter;

@Setter
public class TeamNotFoundException extends ApplicationModelNotFoundException {

    private final FindType findType;
    private final Object findValue;
    private final String errorCode;

    public TeamNotFoundException(FindType findType, Object findValue, String errorCode) {
        this(findType, findValue, errorCode, "Not Found: " + findType.name() + " with " + findValue);
    }

    public TeamNotFoundException(FindType findType, Object findValue, String errorCode, String message) {
        super(message);
        this.findType = findType;
        this.findValue = findValue;
        this.errorCode = errorCode;
    }

    public TeamNotFoundException(FindType findType, Object findValue, String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.findType = findType;
        this.findValue = findValue;
        this.errorCode = errorCode;
    }

    public enum FindType {
        TEAM_ID,
    }

    @Override
    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String getFindType() {
        return findType.name();
    }

    @Override
    public String getFindValue() {
        return findValue.toString();
    }

    @Override
    public String getErrorCaption() {
        return switch (findType) {
            case TEAM_ID -> "Team Not Found (TEAM_ID = %s)".formatted(findValue);
            default -> "Team Not Found";
        };
    }
}
