package org.cresplanex.api.state.webgateway.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class ResponseDto<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private boolean success;
    private T data;
    private String code;
    private String caption;
    private ErrorAttributeDto errorAttributes;
}
