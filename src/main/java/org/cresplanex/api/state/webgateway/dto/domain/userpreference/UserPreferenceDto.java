package org.cresplanex.api.state.webgateway.dto.domain.userpreference;

import lombok.*;
import org.cresplanex.api.state.webgateway.dto.domain.DeepCloneable;
import org.cresplanex.api.state.webgateway.dto.domain.DomainDto;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserPreferenceDto extends DomainDto implements DeepCloneable {

    private String userPreferenceId;

    private String userId;

    private String timezone;

    private String theme;

    private String language;

    private String notificationSettingId;

    @Override
    public UserPreferenceDto deepClone() {
        return (UserPreferenceDto) super.clone();
    }
}
