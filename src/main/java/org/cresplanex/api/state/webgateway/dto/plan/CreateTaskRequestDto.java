package org.cresplanex.api.state.webgateway.dto.plan;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class CreateTaskRequestDto {

    @NotEmpty
    private String chargeUserId;

    @NotEmpty
    private String title;

    @NotEmpty
    private String description;

    @NotEmpty
    private String startDatetime;

    @NotEmpty
    private String dueDatetime;

    @NotEmpty
    private List<String> attachmentIds;
}
