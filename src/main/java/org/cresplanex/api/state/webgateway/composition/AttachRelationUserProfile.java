package org.cresplanex.api.state.webgateway.composition;

import lombok.RequiredArgsConstructor;
import org.cresplanex.api.state.webgateway.dto.ListResponseDto;
import org.cresplanex.api.state.webgateway.dto.domain.ListRelation;
import org.cresplanex.api.state.webgateway.dto.domain.Relation;
import org.cresplanex.api.state.webgateway.dto.domain.organization.OrganizationDto;
import org.cresplanex.api.state.webgateway.dto.domain.organization.OrganizationOnUserProfileDto;
import org.cresplanex.api.state.webgateway.dto.domain.plan.TaskDto;
import org.cresplanex.api.state.webgateway.dto.domain.team.TeamDto;
import org.cresplanex.api.state.webgateway.dto.domain.team.TeamOnUserProfileDto;
import org.cresplanex.api.state.webgateway.dto.domain.userpreference.UserPreferenceDto;
import org.cresplanex.api.state.webgateway.dto.domain.userprofile.UserProfileDto;
import org.cresplanex.api.state.webgateway.hasher.OrganizationHasher;
import org.cresplanex.api.state.webgateway.hasher.TaskHasher;
import org.cresplanex.api.state.webgateway.hasher.TeamHasher;
import org.cresplanex.api.state.webgateway.proxy.query.OrganizationQueryProxy;
import org.cresplanex.api.state.webgateway.proxy.query.TaskQueryProxy;
import org.cresplanex.api.state.webgateway.proxy.query.TeamQueryProxy;
import org.cresplanex.api.state.webgateway.proxy.query.UserPreferenceQueryProxy;
import org.cresplanex.api.state.webgateway.retriever.RetrievedCacheContainer;
import org.cresplanex.api.state.webgateway.retriever.domain.OrganizationRetriever;
import org.cresplanex.api.state.webgateway.retriever.domain.TaskRetriever;
import org.cresplanex.api.state.webgateway.retriever.domain.TeamRetriever;
import org.cresplanex.api.state.webgateway.retriever.domain.UserProfileRetriever;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class AttachRelationUserProfile {

    public static final int NEED_ORGANIZATION_USERS = 1 << 0;
    public static final int NEED_TASK_ATTACHED_FILE_OBJECTS = 1 << 0;
    public static final int NEED_TEAM_USERS = 1 << 0;

    public static final int GET_ORGANIZATION_WITH_USERS = NEED_ORGANIZATION_USERS;
    public static final int GET_TASK_ATTACHED_FILE_OBJECTS = NEED_TASK_ATTACHED_FILE_OBJECTS;
    public static final int GET_TEAM_WITH_USERS = NEED_TEAM_USERS;

    private final TeamQueryProxy teamQueryProxy;
    private final UserPreferenceQueryProxy userPreferenceQueryProxy;
    private final OrganizationQueryProxy organizationQueryProxy;
    private final TaskQueryProxy taskQueryProxy;

    public void attach(String operatorId, RetrievedCacheContainer cache, UserProfileRetriever retriever, UserProfileDto userProfileDto) {

        // organizations
        if (retriever.getOrganizationsRelationRetriever() != null) {
            int need = 0;
            for (OrganizationRetriever subRetriever : retriever.getOrganizationsRelationRetriever().getChain()) {
                if (subRetriever != null && subRetriever.getUsersRelationRetriever() != null) {
                    need |= NEED_ORGANIZATION_USERS;
                }
            }
            List<String> organizationIds = retriever.getOrganizationsRelationRetriever().getIdRetriever().apply(userProfileDto);
            List<String> needRetrieveOrganizationIds = new ArrayList<>();
            Map<String, OrganizationDto> organizationDtoMap = new HashMap<>();
            List<OrganizationDto> organization = new ArrayList<>();

            switch (need) {
                case GET_ORGANIZATION_WITH_USERS:
                    for (String organizationId : organizationIds) {
                        if (cache.getCache().containsKey(OrganizationHasher.hashOrganizationWithUsers(organizationId))) {
                            organizationDtoMap.put(organizationId, (OrganizationDto) cache.getCache().get(OrganizationHasher.hashOrganizationWithUsers(organizationId)));
                        } else {
                            needRetrieveOrganizationIds.add(organizationId);
                        }
                    }
                    if (!needRetrieveOrganizationIds.isEmpty()) {
                        organization = organizationQueryProxy.getPluralOrganizationsWithUsers(
                                operatorId,
                                needRetrieveOrganizationIds,
                                null,
                                null
                        ).getListData();
                        for (OrganizationDto dto : organization) {
                            organizationDtoMap.put(dto.getOrganizationId(), dto);
                            cache.getCache().put(OrganizationHasher.hashOrganizationWithUsers(dto.getOrganizationId()), dto.deepClone());
                        }
                    }
                    break;
                default:
                    for (String organizationId : organizationIds) {
                        if (cache.getCache().containsKey(OrganizationHasher.hashOrganization(organizationId))) {
                            organizationDtoMap.put(organizationId, (OrganizationDto) cache.getCache().get(OrganizationHasher.hashOrganization(organizationId)));
                        } else {
                            needRetrieveOrganizationIds.add(organizationId);
                        }
                    }
                    if (!needRetrieveOrganizationIds.isEmpty()) {
                        organization = organizationQueryProxy.getPluralOrganizations(
                                operatorId,
                                needRetrieveOrganizationIds,
                                null,
                                null
                        ).getListData();
                        for (OrganizationDto dto : organization) {
                            organizationDtoMap.put(dto.getOrganizationId(), dto);
                            cache.getCache().put(OrganizationHasher.hashOrganization(dto.getOrganizationId()), dto.deepClone());
                        }
                    }
                    break;
            }

            if (userProfileDto.getOrganizations().isHasValue()) {
                List<OrganizationOnUserProfileDto> originAttachOrganizationIds = userProfileDto.getOrganizations().getValue();
                List<String> attachOrganizationIds = originAttachOrganizationIds
                        .stream()
                        .map(OrganizationDto::getOrganizationId)
                        .toList();
                List<OrganizationDto> attachOrganizations = attachOrganizationIds.stream()
                        .map(organizationDtoMap::get)
                        .toList();
                userProfileDto.setOrganizations(ListRelation.<OrganizationOnUserProfileDto>builder()
                        .value(
                                attachOrganizations.stream()
                                        .map(org -> new OrganizationOnUserProfileDto(org, originAttachOrganizationIds.stream()
                                                .filter(origin -> origin.getOrganizationId().equals(org.getOrganizationId()))
                                                .findFirst()
                                                .orElse(null)))
                                        .toList()
                        )
                        .build()
                );
                ListRelation<OrganizationOnUserProfileDto> organizationsRelation = retriever.getOrganizationsRelationRetriever().getRelationRetriever().apply(userProfileDto);
                retriever.getOrganizationsRelationRetriever().getChain().forEach(subRetriever -> {
                    if (subRetriever != null && organizationsRelation.isHasValue() && organizationsRelation.getValue() != null) {
                        // TODO: Implement this
                    }
                });
            }
        }

        // teams
        if (retriever.getTeamsRelationRetriever() != null) {
            int need = 0;
            for (TeamRetriever subRetriever : retriever.getTeamsRelationRetriever().getChain()) {
                if (subRetriever != null && subRetriever.getUsersRelationRetriever() != null) {
                    need |= NEED_TEAM_USERS;
                }
            }
            List<String> teamIds = retriever.getTeamsRelationRetriever().getIdRetriever().apply(userProfileDto);
            List<String> needRetrieveTeamIds = new ArrayList<>();
            Map<String, TeamDto> teamDtoMap = new HashMap<>();
            List<TeamDto> teams = new ArrayList<>();

            switch (need) {
                case GET_TEAM_WITH_USERS:
                    for (String teamId : teamIds) {
                        if (cache.getCache().containsKey(TeamHasher.hashTeamWithUsers(teamId))) {
                            teamDtoMap.put(teamId, (TeamDto) cache.getCache().get(TeamHasher.hashTeamWithUsers(teamId)));
                        } else {
                            needRetrieveTeamIds.add(teamId);
                        }
                    }
                    if (!needRetrieveTeamIds.isEmpty()) {
                        teams = teamQueryProxy.getPluralTeamsWithUsers(
                                operatorId,
                                needRetrieveTeamIds,
                                null,
                                null
                        ).getListData();
                        for (TeamDto dto : teams) {
                            teamDtoMap.put(dto.getTeamId(), dto);
                            cache.getCache().put(TeamHasher.hashTeamWithUsers(dto.getTeamId()), dto.deepClone());
                        }
                    }
                    break;
                default:
                    for (String teamId : teamIds) {
                        if (cache.getCache().containsKey(TeamHasher.hashTeam(teamId))) {
                            teamDtoMap.put(teamId, (TeamDto) cache.getCache().get(TeamHasher.hashTeam(teamId)));
                        } else {
                            needRetrieveTeamIds.add(teamId);
                        }
                    }
                    if (!needRetrieveTeamIds.isEmpty()) {
                        teams = teamQueryProxy.getPluralTeams(
                                operatorId,
                                needRetrieveTeamIds,
                                null,
                                null
                        ).getListData();
                        for (TeamDto dto : teams) {
                            teamDtoMap.put(dto.getTeamId(), dto);
                            cache.getCache().put(TeamHasher.hashTeam(dto.getTeamId()), dto.deepClone());
                        }
                    }
                    break;
            }

            if (userProfileDto.getTeams().isHasValue()) {
                List<TeamOnUserProfileDto> originAttachTeamIds = userProfileDto.getTeams().getValue();
                List<String> attachTeamIds = originAttachTeamIds
                        .stream()
                        .map(TeamDto::getTeamId)
                        .toList();
                List<TeamDto> attachTeams = attachTeamIds.stream()
                        .map(teamDtoMap::get)
                        .toList();
                userProfileDto.setTeams(ListRelation.<TeamOnUserProfileDto>builder()
                        .value(
                                attachTeams.stream()
                                        .map(team -> new TeamOnUserProfileDto(team, originAttachTeamIds.stream()
                                                .filter(origin -> origin.getTeamId().equals(team.getTeamId()))
                                                .findFirst()
                                                .orElse(null)))
                                        .toList()
                        )
                        .build()
                );
                ListRelation<TeamOnUserProfileDto> teamsRelation = retriever.getTeamsRelationRetriever().getRelationRetriever().apply(userProfileDto);
                retriever.getTeamsRelationRetriever().getChain().forEach(subRetriever -> {
                    if (subRetriever != null && teamsRelation.isHasValue() && teamsRelation.getValue() != null) {
                        // TODO: Implement this
                    }
                });
            }
        }

        // charge tasks
        if (retriever.getChargeTasksRelationRetriever() != null) {
            int need = 0;
            for (TaskRetriever subRetriever : retriever.getChargeTasksRelationRetriever().getChain()) {
                if (subRetriever != null && subRetriever.getAttachmentsRelationRetriever() != null) {
                    need |= NEED_TASK_ATTACHED_FILE_OBJECTS;
                }
            }
            List<String> taskIds = retriever.getChargeTasksRelationRetriever().getIdRetriever().apply(userProfileDto);
            List<String> needRetrieveTaskIds = new ArrayList<>();
            Map<String, TaskDto> taskDtoMap = new HashMap<>();
            List<TaskDto> tasks = new ArrayList<>();

            switch (need) {
                case GET_TASK_ATTACHED_FILE_OBJECTS:
                    for (String taskId : taskIds) {
                        if (cache.getCache().containsKey(TaskHasher.hashTaskWithAttachments(taskId))) {
                            taskDtoMap.put(taskId, (TaskDto) cache.getCache().get(TaskHasher.hashTaskWithAttachments(taskId)));
                        } else {
                            needRetrieveTaskIds.add(taskId);
                        }
                    }
                    if (!needRetrieveTaskIds.isEmpty()) {
                        tasks = taskQueryProxy.getPluralTasksWithAttachments(
                                operatorId,
                                needRetrieveTaskIds,
                                null,
                                null
                        ).getListData();
                        for (TaskDto dto : tasks) {
                            taskDtoMap.put(dto.getTaskId(), dto);
                            cache.getCache().put(TaskHasher.hashTaskWithAttachments(dto.getTaskId()), dto.deepClone());
                        }
                    }
                    break;
                default:
                    for (String taskId : taskIds) {
                        if (cache.getCache().containsKey(TaskHasher.hashTask(taskId))) {
                            taskDtoMap.put(taskId, (TaskDto) cache.getCache().get(TaskHasher.hashTask(taskId)));
                        } else {
                            needRetrieveTaskIds.add(taskId);
                        }
                    }
                    if (!needRetrieveTaskIds.isEmpty()) {
                        tasks = taskQueryProxy.getPluralTasks(
                                operatorId,
                                needRetrieveTaskIds,
                                null,
                                null
                        ).getListData();
                        for (TaskDto dto : tasks) {
                            taskDtoMap.put(dto.getTaskId(), dto);
                            cache.getCache().put(TaskHasher.hashTask(dto.getTaskId()), dto.deepClone());
                        }
                    }
                    break;
            }

            userProfileDto.setChargeTasks(ListRelation.<TaskDto>builder()
                    .value(
                            taskIds.stream()
                                    .map(taskDtoMap::get)
                                    .toList()
                    )
                    .build()
            );
            ListRelation<TaskDto> chargeTasksRelation = retriever.getChargeTasksRelationRetriever().getRelationRetriever().apply(userProfileDto);
            retriever.getChargeTasksRelationRetriever().getChain().forEach(subRetriever -> {
                if (subRetriever != null && chargeTasksRelation.isHasValue() && chargeTasksRelation.getValue() != null) {
                    // TODO: Implement this
                }
            });
        }

        // userPreference
        if (retriever.getUserPreferenceRelationRetriever() != null) {
            UserPreferenceDto userPreferenceDto;
            String userId = retriever.getUserPreferenceRelationRetriever().getIdRetriever().apply(userProfileDto);
            if (cache.getCache().containsKey(userId)) {
                userPreferenceDto = (UserPreferenceDto) cache.getCache().get(userId);
            } else {
                ListResponseDto.InternalData<UserPreferenceDto> preferences = userPreferenceQueryProxy.getPluralUserPreferencesByUserIds(
                        operatorId,
                        List.of(userId),
                        null,
                        null
                );
                if (preferences.getListData().isEmpty()) {
                    userPreferenceDto = null;
                } else {
                    userPreferenceDto = preferences.getListData().getFirst();
                    cache.getCache().put(userId, userPreferenceDto.deepClone());
                }
            }
            userProfileDto.setUserPreference(
                    Relation.<UserPreferenceDto>builder()
                            .hasValue(true)
                            .value(userPreferenceDto)
                            .build()
            );
            Relation<UserPreferenceDto> userPreferenceRelation = retriever.getUserPreferenceRelationRetriever().getRelationRetriever().apply(userProfileDto);
            retriever.getUserPreferenceRelationRetriever().getChain().forEach(subRetriever -> {
                if (subRetriever != null && userPreferenceRelation.isHasValue() && userPreferenceRelation.getValue() != null) {
                    // TODO: Implement this
                }
            });
        }

        // owned organizations
        if (retriever.getOwnedOrganizationsRelationRetriever() != null) {
            int need = 0;
            for (OrganizationRetriever subRetriever : retriever.getOwnedOrganizationsRelationRetriever().getChain()) {
                if (subRetriever != null && subRetriever.getUsersRelationRetriever() != null) {
                    need |= NEED_ORGANIZATION_USERS;
                }
            }
            List<String> organizationIds = retriever.getOwnedOrganizationsRelationRetriever().getIdRetriever().apply(userProfileDto);
            List<String> needRetrieveOrganizationIds = new ArrayList<>();
            Map<String, OrganizationDto> organizationDtoMap = new HashMap<>();
            List<OrganizationDto> organization = new ArrayList<>();

            switch (need) {
                case GET_ORGANIZATION_WITH_USERS:
                    for (String organizationId : organizationIds) {
                        if (cache.getCache().containsKey(OrganizationHasher.hashOrganizationWithUsers(organizationId))) {
                            organizationDtoMap.put(organizationId, (OrganizationDto) cache.getCache().get(OrganizationHasher.hashOrganizationWithUsers(organizationId)));
                        } else {
                            needRetrieveOrganizationIds.add(organizationId);
                        }
                    }
                    if (!needRetrieveOrganizationIds.isEmpty()) {
                        organization = organizationQueryProxy.getPluralOrganizationsWithUsers(
                                operatorId,
                                needRetrieveOrganizationIds,
                                null,
                                null
                        ).getListData();
                        for (OrganizationDto dto : organization) {
                            organizationDtoMap.put(dto.getOrganizationId(), dto);
                            cache.getCache().put(OrganizationHasher.hashOrganizationWithUsers(dto.getOrganizationId()), dto.deepClone());
                        }
                    }
                    break;
                default:
                    for (String organizationId : organizationIds) {
                        if (cache.getCache().containsKey(OrganizationHasher.hashOrganization(organizationId))) {
                            organizationDtoMap.put(organizationId, (OrganizationDto) cache.getCache().get(OrganizationHasher.hashOrganization(organizationId)));
                        } else {
                            needRetrieveOrganizationIds.add(organizationId);
                        }
                    }
                    if (!needRetrieveOrganizationIds.isEmpty()) {
                        organization = organizationQueryProxy.getPluralOrganizations(
                                operatorId,
                                needRetrieveOrganizationIds,
                                null,
                                null
                        ).getListData();
                        for (OrganizationDto dto : organization) {
                            organizationDtoMap.put(dto.getOrganizationId(), dto);
                            cache.getCache().put(OrganizationHasher.hashOrganization(dto.getOrganizationId()), dto.deepClone());
                        }
                    }
                    break;
                    // TODO: 追加実装
            }
        }

    }

    public void attach(String operatorId, RetrievedCacheContainer cache, UserProfileRetriever retriever, List<UserProfileDto> userProfileDto) {
    }
}
