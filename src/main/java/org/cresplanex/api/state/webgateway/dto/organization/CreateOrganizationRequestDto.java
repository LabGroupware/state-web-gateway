package org.cresplanex.api.state.webgateway.dto.organization;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class CreateOrganizationRequestDto {

    @NotEmpty
    private String name;

    @NotEmpty
    private String plan;

    @NotEmpty
    private List<String> userIds;
}
