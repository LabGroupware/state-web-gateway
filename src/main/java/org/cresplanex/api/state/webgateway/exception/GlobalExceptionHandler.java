package org.cresplanex.api.state.webgateway.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.cresplanex.api.state.common.constants.WebGatewayApplicationCode;
import org.cresplanex.api.state.webgateway.dto.ErrorAttributeDto;
import org.cresplanex.api.state.webgateway.dto.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    // Validation Error
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        ErrorAttributeDto errorAttributeDTO = getErrorAttributeDto(ex, request);

        ErrorResponseDto errorResponseDTO = ErrorResponseDto.create(
                WebGatewayApplicationCode.VALIDATION_ERROR,
                "Validation Error",
                errorAttributeDTO
        );

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.BAD_REQUEST);
    }

    private static ErrorAttributeDto getErrorAttributeDto(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> validationErrors = new HashMap<>();
        List<ObjectError> validationErrorList = ex.getBindingResult().getAllErrors();

        validationErrorList.forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String validationMsg = error.getDefaultMessage();
            validationErrors.put(fieldName, validationMsg);
        });

        return new ErrorAttributeDto(
                request.getRequestURI(),
                validationErrors
        );
    }

    // HTTP Method Not Supported
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Object> handleHttpRequestMethodNotSupported(
             HttpRequestMethodNotSupportedException ex,
             HttpServletRequest request,
             HttpServletResponse response
    ) {
        Map<String, String> errorAttributes = new HashMap<>();
        errorAttributes.put("supportedMethods", Objects.requireNonNull(ex.getSupportedHttpMethods()).toString());
        errorAttributes.put("requestMethod", ex.getMethod());
        errorAttributes.put("requestUrl", request.getRequestURI());

        ErrorAttributeDto errorAttributeDTO = new ErrorAttributeDto(
                request.getRequestURI(),
                errorAttributes
        );

        ErrorResponseDto errorResponseDTO = ErrorResponseDto.create(
                WebGatewayApplicationCode.METHOD_NOT_ALLOWED,
                "Method Not Supported",
                errorAttributeDTO
        );

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.METHOD_NOT_ALLOWED);
    }

    // サポートされていないContent-Type
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Object> handleHttpMediaTypeNotSupported(
             HttpMediaTypeNotSupportedException ex, HttpServletRequest request,  HttpServletResponse response) {

        Map<String, Object> errorAttributes = new HashMap<>();
        errorAttributes.put("supportedMediaTypes", ex.getSupportedMediaTypes().toString());
        errorAttributes.put("contentType", ex.getContentType());

        ErrorAttributeDto errorAttributeDTO = new ErrorAttributeDto(
                request.getRequestURI(),
                errorAttributes
        );

        ErrorResponseDto errorResponseDTO = ErrorResponseDto.create(
                WebGatewayApplicationCode.NOT_SUPPORT_CONTENT_TYPE,
                "Media Type Not Supported",
                errorAttributeDTO
        );

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    // Missing Path Variable
    @ExceptionHandler(MissingPathVariableException.class)
    public ResponseEntity<Object> handleMissingPathVariable(
             MissingPathVariableException ex, HttpServletRequest request,  HttpServletResponse response) {

        Map<String, String> errorAttributes = new HashMap<>();
        errorAttributes.put("parameterName", ex.getVariableName());
        errorAttributes.put("parameterType", ex.getParameter().getParameterType().getSimpleName());

        ErrorAttributeDto errorAttributeDTO = new ErrorAttributeDto(
                request.getRequestURI(),
                errorAttributes
        );

        ErrorResponseDto errorResponseDTO = ErrorResponseDto.create(
                WebGatewayApplicationCode.MISSING_PATH_VARIABLE,
                "Validation Error",
                errorAttributeDTO
        );

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.BAD_REQUEST);
    }

    // Max Upload Size Exceeded
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Object> handleMaxUploadSizeExceededException(
             MaxUploadSizeExceededException ex,  HttpServletRequest request, HttpServletResponse response) {

        Map<String, String> errorAttributes = new HashMap<>();
        errorAttributes.put("maxUploadSize", ex.getMaxUploadSize() + " bytes");

        ErrorAttributeDto errorAttributeDTO = new ErrorAttributeDto(
                request.getRequestURI(),
                errorAttributes
        );

        ErrorResponseDto errorResponseDTO = ErrorResponseDto.create(
                WebGatewayApplicationCode.EXCEED_MAX_UPLOAD_SIZE,
                "Validation Error",
                errorAttributeDTO
        );

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.PAYLOAD_TOO_LARGE);
    }

    // No Resource Found
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Object> handleNoResourceFoundException(
             NoResourceFoundException ex,  HttpServletRequest request, HttpServletResponse response) {

        Map<String, String> errorAttributes = new HashMap<>();
        errorAttributes.put("requestUrl", request.getRequestURI());

        ErrorAttributeDto errorAttributeDTO = new ErrorAttributeDto(
                request.getRequestURI(),
                errorAttributes
        );

        ErrorResponseDto errorResponseDTO = ErrorResponseDto.create(
                WebGatewayApplicationCode.NOT_FOUND_HANDLER,
                "Not Found",
                errorAttributeDTO
        );

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.NOT_FOUND);
    }

    // Not Found Handler
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Object> handleNoHandlerFoundException(
             NoHandlerFoundException ex,  HttpServletRequest request, HttpServletResponse response) {

        Map<String, String> errorAttributes = new HashMap<>();
        errorAttributes.put("requestMethod", ex.getHttpMethod());
        errorAttributes.put("requestUrl", ex.getRequestURL());

        ErrorAttributeDto errorAttributeDTO = new ErrorAttributeDto(
                request.getRequestURI(),
                errorAttributes
        );

        ErrorResponseDto errorResponseDTO = ErrorResponseDto.create(
                WebGatewayApplicationCode.NOT_FOUND_HANDLER,
                "Internal Server Error",
                errorAttributeDTO
        );

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.NOT_FOUND);
    }

    // not readable
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleHttpMessageNotReadable(
             HttpMessageNotReadableException ex,  HttpServletRequest request, HttpServletResponse response) {

        Map<String, String> errorAttributes = new HashMap<>();

        ErrorAttributeDto errorAttributeDTO = new ErrorAttributeDto(
                request.getRequestURI(),
                errorAttributes
        );

        ErrorResponseDto errorResponseDTO = ErrorResponseDto.create(
                WebGatewayApplicationCode.NOT_READABLE_REQUEST,
                "Not Readable Request",
                errorAttributeDTO
        );

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.BAD_REQUEST);
    }


    // Authentication Failed
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Object> handleAuthenticationException(
            AuthenticationException ex, HttpServletRequest request, HttpServletResponse response) {

        ErrorAttributeDto errorAttributeDTO = new ErrorAttributeDto(
                request.getRequestURI(),
                null
        );

        ErrorResponseDto errorResponseDTO = ErrorResponseDto.create(
                WebGatewayApplicationCode.AUTHENTICATION_FAILED,
                "Authentication Error",
                errorAttributeDTO
        );

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.UNAUTHORIZED);
    }

    // Access Denied
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(
            AccessDeniedException ex, HttpServletRequest request, HttpServletResponse response) {

        Map<String, String> errorAttributes = new HashMap<>();
        errorAttributes.put("message", ex.getMessage());

        ErrorAttributeDto errorAttributeDTO = new ErrorAttributeDto(
                request.getRequestURI(),
                errorAttributes
        );

        ErrorResponseDto errorResponseDTO = ErrorResponseDto.create(
                WebGatewayApplicationCode.ACCESS_DENIED,
                "Access Denied",
                errorAttributeDTO
        );

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.FORBIDDEN);
    }

    // Method Argument Type Mismatch
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request, HttpServletResponse response) {

        Map<String, Object> errorAttributes = new HashMap<>();
        errorAttributes.put("parameterName", ex.getName());
        errorAttributes.put("parameterValue", ex.getValue());
        errorAttributes.put("parameterType", Objects.requireNonNull(ex.getRequiredType()).getSimpleName());

        ErrorAttributeDto errorAttributeDTO = new ErrorAttributeDto(
                request.getRequestURI(),
                errorAttributes
        );

        ErrorResponseDto errorResponseDTO = ErrorResponseDto.create(
                WebGatewayApplicationCode.METHOD_ARGUMENT_TYPE_MISMATCH,
                "Validation Error",
                errorAttributeDTO
        );

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.BAD_REQUEST);
    }

    // Global Exception
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGlobalException(
            Exception exception, HttpServletRequest request, HttpServletResponse response) {
        log.error("Global Exception", exception);
        log.error("Request URI: {}", request.getRequestURI());

        ErrorAttributeDto errorAttributeDTO = new ErrorAttributeDto(
                request.getRequestURI(),
                null
        );

        ErrorResponseDto errorResponseDTO = ErrorResponseDto.create(
                WebGatewayApplicationCode.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                errorAttributeDTO
        );

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

