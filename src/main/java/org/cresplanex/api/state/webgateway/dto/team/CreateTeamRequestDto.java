package org.cresplanex.api.state.webgateway.dto.team;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class CreateTeamRequestDto {

    @NotEmpty
    private String organizationId;

    @NotEmpty
    private String name;

    @NotEmpty
    private String description;

    @NotEmpty
    private List<String> userIds;
}
