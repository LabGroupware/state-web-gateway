package org.cresplanex.api.state.webgateway.composition;

import org.cresplanex.api.state.webgateway.dto.ListResponseDto;
import org.cresplanex.api.state.webgateway.dto.domain.OverMerge;
import org.cresplanex.api.state.webgateway.dto.domain.Relation;
import org.cresplanex.api.state.webgateway.dto.domain.team.TeamDto;
import org.cresplanex.api.state.webgateway.dto.domain.userprofile.UserProfileDto;
import org.cresplanex.api.state.webgateway.dto.domain.userprofile.UserProfileOnTeamDto;
import org.cresplanex.api.state.webgateway.proxy.query.UserProfileQueryProxy;
import org.cresplanex.api.state.webgateway.retriever.RelationRetriever;
import org.cresplanex.api.state.webgateway.retriever.RootRelationRetriever;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ForUserProfileUtils {

    public static Map<String, UserProfileDto> getStringUserProfileDtoMap(UserProfileQueryProxy userProfileQueryProxy, String operatorId, Set<String> userIds) {

        ListResponseDto.InternalData<UserProfileDto> userProfiles = userProfileQueryProxy.getPluralUserProfilesByUserIds(
                operatorId,
                List.copyOf(userIds),
                null,
                null
        );

        return userProfiles.getListData().stream()
                .collect(Collectors.toMap(UserProfileDto::getUserId, userProfile -> userProfile));
    }

    public static <T extends UserProfileDto & OverMerge<UserProfileDto, T>> void attachUserProfileToUserProfile(UserProfileQueryProxy userProfileQueryProxy, String operatorId, ListResponseDto.InternalData<T> userProfileDto) {
        Set<String> userIds = CommonUtils.createIdsSet(userProfileDto.getListData(), UserProfileDto::getUserId);

        Map<String, UserProfileDto> userProfileDtoMap = getStringUserProfileDtoMap(
                userProfileQueryProxy,
                operatorId,
                userIds
        );

        userProfileDto.setListData(userProfileDto.getListData().stream()
                .map(user -> {
                    UserProfileDto fetchedUserProfile = userProfileDtoMap.get(user.getUserId());
                    if (fetchedUserProfile == null) {
                        return null;
                    }
                    return user.overMerge(fetchedUserProfile);
                })
                .collect(Collectors.toList())
        );
    }

    public static void attachUserProfilesToTeam(UserProfileQueryProxy userProfileQueryProxy, String operatorId, ListResponseDto.InternalData<TeamDto> teamDto) {
        Set<String> userIds = new java.util.HashSet<String>(Set.of());

        teamDto.getListData()
                .forEach(org -> {
                    Set<String> addIds = CommonUtils.createIdsSet(org.getUsers().getValue(), UserProfileOnTeamDto::getUserId);
                    userIds.addAll(addIds);
                });

        Map<String, UserProfileDto> userProfileMap = ForUserProfileUtils.getStringUserProfileDtoMap(
                userProfileQueryProxy,
                operatorId,
                userIds
        );

        teamDto.getListData().forEach(org -> {
            org.setUsers(Relation.<List<UserProfileOnTeamDto>>builder()
                    .hasValue(true)
                    .value(org.getUsers().getValue().stream()
                            .map(user -> {
                                UserProfileDto userProfile = userProfileMap.get(user.getUserId());
                                if (userProfile == null) {
                                    return null;
                                }
                                return new UserProfileOnTeamDto(userProfile);
                            })
                            .collect(Collectors.toList())
                    )
                    .build());
        });
    }

    public static void attachUserProfilesToTeam(UserProfileQueryProxy userProfileQueryProxy, String operatorId, ListResponseDto.InternalData<TeamDto> teamDto, RelationRetriever<TeamDto> retriever) {
        Set<String> userIds = new java.util.HashSet<String>(Set.of());

        teamDto.getListData()
                .forEach(org -> {
                    Set<String> addIds = CommonUtils.createIdsSet(org.getUsers().getValue(), UserProfileOnTeamDto::getUserId);
                    userIds.addAll(addIds);
                });

        Map<String, UserProfileDto> userProfileMap = ForUserProfileUtils.getStringUserProfileDtoMap(
                userProfileQueryProxy,
                operatorId,
                userIds
        );

        teamDto.getListData().forEach(org -> {
            org.setUsers(Relation.<List<UserProfileOnTeamDto>>builder()
                    .hasValue(true)
                    .value(org.getUsers().getValue().stream()
                            .map(user -> {
                                UserProfileDto userProfile = userProfileMap.get(user.getUserId());
                                if (userProfile == null) {
                                    return null;
                                }
                                return new UserProfileOnTeamDto(userProfile);
                            })
                            .collect(Collectors.toList())
                    )
                    .build());
        });
    }
}
