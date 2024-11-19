package org.cresplanex.api.state.webgateway.dto;

import lombok.*;

import java.io.Serial;

@Data
public class CommandResponseDto extends ResponseDto<CommandResponseDto.Data> {

    @Serial
    private static final long serialVersionUID = 1L;

    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Data {
        private String jobId;
    }
}
