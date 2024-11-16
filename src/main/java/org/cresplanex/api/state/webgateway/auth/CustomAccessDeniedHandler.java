package org.cresplanex.api.state.webgateway.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.cresplanex.api.state.common.constants.WebGatewayApplicationCode;
import org.cresplanex.api.state.webgateway.dto.ErrorAttributeDto;
import org.cresplanex.api.state.webgateway.dto.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException, ServletException {
        log.info("Access denied: {}", accessDeniedException.getMessage());

        Map<String, Object> errorAttributes = new HashMap<>();
        errorAttributes.put("error", HttpStatus.FORBIDDEN.getReasonPhrase());

        ErrorAttributeDto errorAttributeDTO = new ErrorAttributeDto(
                request.getRequestURI(),
                errorAttributes
        );

        ErrorResponseDto errorResponseDTO = ErrorResponseDto.create(
                WebGatewayApplicationCode.ACCESS_DENIED,
                "Access denied",
                errorAttributeDTO
        );
        String jsonResponse = objectMapper.writeValueAsString(errorResponseDTO);

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(jsonResponse);
    }
}
