package org.cresplanex.api.state.webgateway.dto.userpreference;

import lombok.Data;

@Data
public class UpdateUserPreferenceRequestDto {

    private String timezone;
    private String theme;
    private String language;
}
