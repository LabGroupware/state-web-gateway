package org.cresplanex.api.state.webgateway.dto.domain.userpreference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.cresplanex.api.state.webgateway.dto.domain.DeepCloneable;
import org.cresplanex.api.state.webgateway.dto.domain.DomainDto;

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
        try {
            return (UserPreferenceDto) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
