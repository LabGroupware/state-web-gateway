package org.cresplanex.api.state.webgateway.dto.organization;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class AddUsersOrganizationRequestDto {

    @NotEmpty
    private List<String> userIds;
}
