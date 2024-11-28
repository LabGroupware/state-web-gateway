package org.cresplanex.api.state.webgateway.mapper;

import build.buf.gen.userpreference.v1.UserPreference;
import org.cresplanex.api.state.webgateway.dto.domain.userpreference.UserPreferenceDto;

public class UserPreferenceMapper {

    public static UserPreferenceDto convert(UserPreference userPreference) {
        return UserPreferenceDto.builder()
                .userPreferenceId(userPreference.getUserPreferenceId())
                .userId(userPreference.getUserId())
                .theme(userPreference.getTheme().getHasValue() ? userPreference.getTheme().getValue() : null)
                .language(userPreference.getLanguage().getHasValue() ? userPreference.getLanguage().getValue() : null)
                .timezone(userPreference.getTimezone().getHasValue() ? userPreference.getTimezone().getValue() : null)
                .build();
    }

    public static UserPreferenceDto convertFromUserPreferenceId(String userPreferenceId) {
        return UserPreferenceDto.builder()
                .userPreferenceId(userPreferenceId)
                .build();
    }

    public static UserPreferenceDto convertFromUserId(String userId) {
        return UserPreferenceDto.builder()
                .userId(userId)
                .build();
    }
}
