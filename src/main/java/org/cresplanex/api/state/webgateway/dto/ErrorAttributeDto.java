package org.cresplanex.api.state.webgateway.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class ErrorAttributeDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final String timestamp;
    private final String path;
    private Object value;

    public ErrorAttributeDto(String path, Object errorAttributes) {
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        this.path = path;
        this.value = errorAttributes;
    }
}
