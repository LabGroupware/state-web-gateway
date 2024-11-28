package org.cresplanex.api.state.webgateway.composition;

import org.cresplanex.api.state.webgateway.dto.ListResponseDto;
import org.cresplanex.api.state.webgateway.dto.domain.Relation;
import org.cresplanex.api.state.webgateway.dto.domain.team.TeamDto;
import org.cresplanex.api.state.webgateway.dto.domain.userpreference.UserPreferenceDto;
import org.cresplanex.api.state.webgateway.dto.domain.userprofile.UserProfileDto;
import org.cresplanex.api.state.webgateway.dto.domain.userprofile.UserProfileOnTeamDto;
import org.cresplanex.api.state.webgateway.proxy.query.UserPreferenceQueryProxy;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ForUserPreferenceUtils {

    public static Map<String, UserPreferenceDto> getStringUserPreferenceDtoMap(UserPreferenceQueryProxy userPreferenceQueryProxy, String operatorId, Set<String> userIds) {

        ListResponseDto.InternalData<UserPreferenceDto> userPreferences = userPreferenceQueryProxy.getPluralUserPreferencesByUserIds(
                operatorId,
                List.copyOf(userIds),
                null,
                null
        );

        return userPreferences.getListData().stream()
                .collect(Collectors.toMap(UserPreferenceDto::getUserId, userPreference -> userPreference));
    }

    public static void attachUserPreferenceToUserProfile(UserPreferenceQueryProxy userPreferenceQueryProxy, String operatorId, ListResponseDto.InternalData<UserProfileDto> userProfileDto) {
        Set<String> userIds = CommonUtils.createIdsSet(userProfileDto.getListData(), UserProfileDto::getUserId);

        Map<String, UserPreferenceDto> userPreferenceDtoMap = ForUserPreferenceUtils.getStringUserPreferenceDtoMap(
                userPreferenceQueryProxy,
                operatorId,
                userIds
        );

        userProfileDto.setListData(userProfileDto.getListData().stream()
                .map(user -> {
                    UserPreferenceDto userPreference = userPreferenceDtoMap.get(user.getUserId());
                    if (userPreference == null) {
                        return null;
                    }
                    user.setUserPreference(
                            Relation.<UserPreferenceDto>builder()
                                    .hasValue(true)
                                    .value(userPreference)
                                    .build()
                    );
                    return user;
                })
                .collect(Collectors.toList())
        );
    }

    public static void attacheUserPreferenceToTeamUsers(UserPreferenceQueryProxy userPreferenceQueryProxy, String operatorId, ListResponseDto.InternalData<TeamDto> teamDto) {
        Set<String> userIds = new java.util.HashSet<String>(Set.of());

        teamDto.getListData()
                .forEach(org -> {
                    Set<String> addIds = CommonUtils.createIdsSet(org.getUsers().getValue(), UserProfileOnTeamDto::getUserId);
                    userIds.addAll(addIds);
                });

        Map<String, UserPreferenceDto> userPreferenceDtoMap = getStringUserPreferenceDtoMap(
                userPreferenceQueryProxy,
                operatorId,
                userIds
        );

        teamDto.setListData(teamDto.getListData().stream()
                .peek(team -> {
                    if(!team.getUsers().isHasValue()) {
                        return;
                    }
                    team.setUsers(Relation.<List<UserProfileOnTeamDto>>builder()
                            .value(team.getUsers().getValue().stream()
                                    .map(user -> {
                                        UserPreferenceDto userPreference = userPreferenceDtoMap.get(user.getUserId());
                                        if (userPreference == null) {
                                            return null;
                                        }
                                        user.setUserPreference(
                                                Relation.<UserPreferenceDto>builder()
                                                        .hasValue(true)
                                                        .value(userPreference)
                                                        .build()
                                        );
                                        return user;
                                    })
                                    .collect(Collectors.toList())
                            )
                            .build());
                })
                .collect(Collectors.toList())
        );
    }
}
