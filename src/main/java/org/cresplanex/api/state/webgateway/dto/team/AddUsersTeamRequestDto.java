package org.cresplanex.api.state.webgateway.dto.team;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class AddUsersTeamRequestDto {

    @NotEmpty
    private List<String> userIds;
}
