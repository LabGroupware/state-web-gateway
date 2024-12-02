package org.cresplanex.api.state.webgateway.exception;

import lombok.Setter;

@Setter
public class UserNotFoundException extends ApplicationModelNotFoundException {

    private final FindType findType;
    private final Object findValue;
    private final String errorCode;

    public UserNotFoundException(FindType findType, Object findValue, String errorCode) {
        this(findType, findValue, errorCode, "Not Found: " + findType.name() + " with " + findValue);
    }

    public UserNotFoundException(FindType findType, Object findValue, String errorCode, String message) {
        super(message);
        this.findType = findType;
        this.findValue = findValue;
        this.errorCode = errorCode;
    }

    public UserNotFoundException(FindType findType, Object findValue,String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.findType = findType;
        this.findValue = findValue;
        this.errorCode = errorCode;
    }

    public enum FindType {
        EMAIL,
        USER_ID,
        USER_PROFILE_ID,
        USER_PREFERENCE_ID
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
            case EMAIL -> "User Not Found (Email = %s)".formatted(findValue);
            case USER_ID -> "User Not Found (USER_ID = %s)".formatted(findValue);
            case USER_PROFILE_ID -> "User Not Found (USER_PROFILE_ID = %s)".formatted(findValue);
            case USER_PREFERENCE_ID -> "User Not Found (USER_PREFERENCE_ID = %s)".formatted(findValue);
            default -> "User Not Found";
        };
    }
}
