package org.cresplanex.api.state.webgateway.dto;

import lombok.*;

import java.io.Serial;

@EqualsAndHashCode(callSuper = true)
@Data
public class CommandResponseDto extends ResponseDto<CommandResponseDto.InternalData> {

    @Serial
    private static final long serialVersionUID = 1L;

    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InternalData {
        private String jobId;
    }
}
