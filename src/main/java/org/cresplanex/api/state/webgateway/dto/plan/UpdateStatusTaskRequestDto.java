package org.cresplanex.api.state.webgateway.dto.plan;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class UpdateStatusTaskRequestDto {

    @NotEmpty
    private String status;
}
