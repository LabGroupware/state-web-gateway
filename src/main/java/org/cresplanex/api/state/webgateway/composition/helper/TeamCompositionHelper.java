package org.cresplanex.api.state.webgateway.composition.helper;

import org.cresplanex.api.state.webgateway.dto.domain.ListRelation;
import org.cresplanex.api.state.webgateway.dto.domain.organization.OrganizationDto;
import org.cresplanex.api.state.webgateway.dto.domain.team.TeamDto;
import org.cresplanex.api.state.webgateway.dto.domain.team.TeamOnUserProfileDto;
import org.cresplanex.api.state.webgateway.dto.domain.userprofile.UserProfileDto;
import org.cresplanex.api.state.webgateway.hasher.TeamHasher;
import org.cresplanex.api.state.webgateway.proxy.query.TeamQueryProxy;
import org.cresplanex.api.state.webgateway.retriever.RetrievedCacheContainer;
import org.cresplanex.api.state.webgateway.retriever.domain.TeamRetriever;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TeamCompositionHelper {

    public static final int NEED_TEAM_USERS = 1 << 0;

    public static final int GET_TEAM_WITH_USERS = NEED_TEAM_USERS;

    public static int calculateNeedQuery(List<TeamRetriever> retrievers) {
        int needQuery = 0;
        for (TeamRetriever retriever : retrievers) {
            if (retriever != null && retriever.getUsersRelationRetriever() != null) {
                needQuery |= NEED_TEAM_USERS;
            }
        }
        return needQuery;
    }

    public static Map<String, TeamDto> createTeamDtoMap(
            TeamQueryProxy teamQueryProxy,
            RetrievedCacheContainer cache,
            String operatorId,
            List<String> teamIds,
            List<TeamRetriever> retrievers
    ) {
        int need = calculateNeedQuery(retrievers);
        List<String> needRetrieveAttachedTeamIds = new ArrayList<>();
        Map<String, TeamDto> teamDtoMap = new HashMap<>();
        List<TeamDto> team;

        switch (need) {
            case GET_TEAM_WITH_USERS:
                for (String teamId : teamIds) {
                    if (cache.getCache().containsKey(TeamHasher.hashTeamWithUsers(teamId))) {
                        teamDtoMap.put(teamId, ((TeamDto) cache.getCache().get(TeamHasher.hashTeamWithUsers(teamId))).deepClone());
                        break;
                    } else {
                        needRetrieveAttachedTeamIds.add(teamId);
                    }
                }
                if (!needRetrieveAttachedTeamIds.isEmpty()) {
                    team = teamQueryProxy.getPluralTeamsWithUsers(
                            operatorId,
                            needRetrieveAttachedTeamIds,
                            null,
                            null
                    ).getListData();

                    for (TeamDto dto : team) {
                        teamDtoMap.put(dto.getTeamId(), dto);
                        cache.getCache().put(TeamHasher.hashTeamWithUsers(dto.getTeamId()), dto.deepClone());
                    }
                }
                break;
            default:
                for (String teamId : teamIds) {
                    if (cache.getCache().containsKey(TeamHasher.hashTeam(teamId))) {
                        teamDtoMap.put(teamId, ((TeamDto) cache.getCache().get(TeamHasher.hashTeam(teamId))).deepClone());
                        break;
                    } else {
                        needRetrieveAttachedTeamIds.add(teamId);
                    }
                }

                if (!needRetrieveAttachedTeamIds.isEmpty()) {
                    team = teamQueryProxy.getPluralTeams(
                            operatorId,
                            needRetrieveAttachedTeamIds,
                            null,
                            null
                    ).getListData();
                    for (TeamDto dto : team) {
                        teamDtoMap.put(dto.getTeamId(), dto);
                        cache.getCache().put(TeamHasher.hashTeam(dto.getTeamId()), dto.deepClone());
                    }
                }
                break;
        }

        return teamDtoMap;
    }

    public static <T extends UserProfileDto> void preAttachToUserProfile(
            TeamQueryProxy teamQueryProxy,
            RetrievedCacheContainer cache,
            String operatorId,
            List<T> userProfileDtos
    ) {
        List<TeamDto> relationTeams = teamQueryProxy.getTeamsWithUsers(
                operatorId,
                0,
                0,
                null,
                null,
                null,
                null,
                false,
                false,
                false,
                false,
                null,
                true,
                userProfileDtos.stream().map(UserProfileDto::getUserId).toList(),
                "any"
        ).getListData();

        Map<String, UserProfileDto> userProfileDtoMap = userProfileDtos.stream()
                .collect(Collectors.toMap(UserProfileDto::getUserId, Function.identity()));
        Map<String, List<TeamOnUserProfileDto>> teamOnUserProfileDtoMap = new HashMap<>();

        for (TeamDto dto : relationTeams) {
            cache.getCache().put(TeamHasher.hashTeamWithUsers(dto.getTeamId()), dto.deepClone());
            if (dto.getUsers().isHasValue()) {
                dto.getUsers().getValue().forEach(userOnTeam -> {
                    UserProfileDto targetUserProfileDto = userProfileDtoMap.get(userOnTeam.getUserId());
                    if (targetUserProfileDto != null) {
                        teamOnUserProfileDtoMap.computeIfAbsent(targetUserProfileDto.getUserId(), k -> new ArrayList<>()).add(new TeamOnUserProfileDto(dto));
                    }
                });
            }
        }

        for (Map.Entry<String, List<TeamOnUserProfileDto>> entry : teamOnUserProfileDtoMap.entrySet()) {
            UserProfileDto targetUserProfileDto = userProfileDtoMap.get(entry.getKey());
            if (targetUserProfileDto == null) {
                continue;
            }
            targetUserProfileDto.setTeams(ListRelation.<TeamOnUserProfileDto>builder()
                    .hasValue(true)
                    .value(entry.getValue())
                    .build()
            );
        }
    }

    public static <T extends OrganizationDto> void preAttachToOrganization(
            TeamQueryProxy teamQueryProxy,
            RetrievedCacheContainer cache,
            String operatorId,
            List<T> organizationDtos
    ) {
        List<TeamDto> relationTeams = teamQueryProxy.getTeams(
                operatorId,
                0,
                0,
                null,
                null,
                null,
                null,
                false,
                false,
                false,
                true,
                organizationDtos.stream().map(OrganizationDto::getOrganizationId).toList(),
                false,
                null,
                null
        ).getListData();

        Map<String, OrganizationDto> organizationDtoMap = organizationDtos.stream()
                .collect(Collectors.toMap(OrganizationDto::getOrganizationId, Function.identity()));
        Map<String, List<TeamDto>> teamDtoMap = new HashMap<>();

        for (TeamDto dto : relationTeams) {
            cache.getCache().put(TeamHasher.hashTeam(dto.getTeamId()), dto.deepClone());
            String organizationId = dto.getOrganizationId();
            if (organizationId != null) {
                teamDtoMap.computeIfAbsent(organizationId, k -> new ArrayList<>()).add(dto);
            }
        }

        for (Map.Entry<String, List<TeamDto>> entry : teamDtoMap.entrySet()) {
            OrganizationDto targetOrganizationDto = organizationDtoMap.get(entry.getKey());
            if (targetOrganizationDto == null) {
                continue;
            }
            targetOrganizationDto.setTeams(ListRelation.<TeamDto>builder()
                    .hasValue(true)
                    .value(entry.getValue())
                    .build()
            );
        }
    }
}
