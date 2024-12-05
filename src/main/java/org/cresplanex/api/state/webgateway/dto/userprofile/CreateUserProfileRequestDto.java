package org.cresplanex.api.state.webgateway.dto.userprofile;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class CreateUserProfileRequestDto {

    @NotEmpty
    private String userId;

    @NotEmpty
    private String name;

    @NotEmpty
    private String email;
}
