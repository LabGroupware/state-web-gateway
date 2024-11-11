package org.cresplanex.api.state.webgateway.constants;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ServerErrorCode {

    public static final String SUCCESS = "0000.0000";
    public static final String INTERNAL_SERVER_ERROR = "0000.1000";
    public static final String VALIDATION_ERROR = "0000.1001";
    public static final String METHOD_NOT_ALLOWED = "0000.1002";
    public static final String NOT_SUPPORT_CONTENT_TYPE = "0000.1003";
    public static final String AUTHENTICATION_FAILED = "0000.1004";
    public static final String AUTHORIZATION_FAILED = "0000.1005";
    public static final String ACCESS_DENIED = "0000.1006";
    public static final String METHOD_ARGUMENT_TYPE_MISMATCH = "0000.1007";
    public static final String MISSING_PATH_VARIABLE = "0000.1008";
    public static final String EXCEED_MAX_UPLOAD_SIZE = "0000.1009";
    public static final String NOT_FOUND_HANDLER = "0000.1010";
    public static final String NOT_READABLE_REQUEST = "0000.1011";

    public static final String JOB_COMPLETED = "0001.0000";
    public static final String JOB_FAILED = "0001.0001";
    public static final String JOB_PROCESSING = "0001.0002";
    public static final String JOB_NOT_FOUND = "0001.0003";
}
