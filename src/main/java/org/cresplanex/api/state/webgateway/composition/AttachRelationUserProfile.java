package org.cresplanex.api.state.webgateway.composition;

import lombok.RequiredArgsConstructor;
import org.cresplanex.api.state.webgateway.dto.domain.ListRelation;
import org.cresplanex.api.state.webgateway.dto.domain.Relation;
import org.cresplanex.api.state.webgateway.dto.domain.organization.OrganizationDto;
import org.cresplanex.api.state.webgateway.dto.domain.organization.OrganizationOnUserProfileDto;
import org.cresplanex.api.state.webgateway.dto.domain.plan.TaskDto;
import org.cresplanex.api.state.webgateway.dto.domain.team.TeamDto;
import org.cresplanex.api.state.webgateway.dto.domain.team.TeamOnUserProfileDto;
import org.cresplanex.api.state.webgateway.dto.domain.userpreference.UserPreferenceDto;
import org.cresplanex.api.state.webgateway.dto.domain.userprofile.UserProfileDto;
import org.cresplanex.api.state.webgateway.proxy.query.OrganizationQueryProxy;
import org.cresplanex.api.state.webgateway.proxy.query.TaskQueryProxy;
import org.cresplanex.api.state.webgateway.proxy.query.TeamQueryProxy;
import org.cresplanex.api.state.webgateway.proxy.query.UserPreferenceQueryProxy;
import org.cresplanex.api.state.webgateway.retriever.RetrievedCacheContainer;
import org.cresplanex.api.state.webgateway.retriever.domain.*;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class AttachRelationUserProfile {

    private final TeamQueryProxy teamQueryProxy;
    private final UserPreferenceQueryProxy userPreferenceQueryProxy;
    private final OrganizationQueryProxy organizationQueryProxy;
    private final TaskQueryProxy taskQueryProxy;

    public void attach(String operatorId, RetrievedCacheContainer cache, UserProfileRetriever retriever, UserProfileDto userProfileDto) {

        // organizations
        if (retriever.getOrganizationsRelationRetriever() != null) {
            OrganizationCompositionHelper.preAttachToUserProfile(
                    organizationQueryProxy,
                    cache,
                    operatorId,
                    List.of(userProfileDto)
            );

            Map<String, OrganizationDto> organizationDtoMap = OrganizationCompositionHelper.createOrganizationDtoMap(
                    organizationQueryProxy,
                    cache,
                    operatorId,
                    retriever.getOrganizationsRelationRetriever().getIdRetriever().apply(userProfileDto),
                    retriever.getOrganizationsRelationRetriever().getChain()
            );

            this.attachRelationToOrganizations(
                    operatorId,
                    cache,
                    userProfileDto,
                    organizationDtoMap,
                    retriever.getOrganizationsRelationRetriever().getRelationRetriever().apply(userProfileDto),
                    retriever.getOrganizationsRelationRetriever().getChain()
            );
        }

        // teams
        if (retriever.getTeamsRelationRetriever() != null) {
            TeamCompositionHelper.preAttachToUserProfile(
                    teamQueryProxy,
                    cache,
                    operatorId,
                    List.of(userProfileDto)
            );

            Map<String, TeamDto> teamDtoMap = TeamCompositionHelper.createTeamDtoMap(
                    teamQueryProxy,
                    cache,
                    operatorId,
                    retriever.getTeamsRelationRetriever().getIdRetriever().apply(userProfileDto),
                    retriever.getTeamsRelationRetriever().getChain()
            );

            this.attachRelationToTeams(
                    operatorId,
                    cache,
                    userProfileDto,
                    teamDtoMap,
                    retriever.getTeamsRelationRetriever().getRelationRetriever().apply(userProfileDto),
                    retriever.getTeamsRelationRetriever().getChain()
            );
        }

        // charge tasks
        if (retriever.getChargeTasksRelationRetriever() != null) {

            TaskCompositionHelper.preAttachToChargeUser(
                    taskQueryProxy,
                    cache,
                    operatorId,
                    List.of(userProfileDto)
            );

            Map<String, TaskDto> taskDtoMap = TaskCompositionHelper.createTaskDtoMap(
                    taskQueryProxy,
                    cache,
                    operatorId,
                    retriever.getChargeTasksRelationRetriever().getIdRetriever().apply(userProfileDto),
                    retriever.getChargeTasksRelationRetriever().getChain()
            );

            this.attachRelationToChargeTasks(
                    operatorId,
                    cache,
                    userProfileDto,
                    taskDtoMap,
                    retriever.getChargeTasksRelationRetriever().getRelationRetriever().apply(userProfileDto),
                    retriever.getChargeTasksRelationRetriever().getChain()
            );
        }

        // userPreference
        if (retriever.getUserPreferenceRelationRetriever() != null) {
            String userId = retriever.getUserPreferenceRelationRetriever().getIdRetriever().apply(userProfileDto);

            Map<String, UserPreferenceDto> userPreferenceDtoMap = UserPreferenceCompositionHelper.createUserPreferenceDtoMap(
                    userPreferenceQueryProxy,
                    cache,
                    operatorId,
                    List.of(userId),
                    retriever.getUserPreferenceRelationRetriever().getChain()
            );

            this.attachRelationToUserPreference(
                    operatorId,
                    cache,
                    userProfileDto,
                    userPreferenceDtoMap,
                    userId,
                    retriever.getUserPreferenceRelationRetriever().getRelationRetriever().apply(userProfileDto),
                    retriever.getUserPreferenceRelationRetriever().getChain()
            );
        }

        // owned organizations
        if (retriever.getOwnedOrganizationsRelationRetriever() != null) {
            OrganizationCompositionHelper.preAttachToOwner(
                    organizationQueryProxy,
                    cache,
                    operatorId,
                    List.of(userProfileDto)
            );

            Map<String, OrganizationDto> organizationDtoMap = OrganizationCompositionHelper.createOrganizationDtoMap(
                    organizationQueryProxy,
                    cache,
                    operatorId,
                    retriever.getOwnedOrganizationsRelationRetriever().getIdRetriever().apply(userProfileDto),
                    retriever.getOwnedOrganizationsRelationRetriever().getChain()
            );

            this.attachRelationToOwnedOrganizations(
                    operatorId,
                    cache,
                    userProfileDto,
                    organizationDtoMap,
                    retriever.getOwnedOrganizationsRelationRetriever().getRelationRetriever().apply(userProfileDto),
                    retriever.getOwnedOrganizationsRelationRetriever().getChain()
            );
        }
    }

    public void attach(String operatorId, RetrievedCacheContainer cache, UserProfileRetriever retriever, List<UserProfileDto> userProfileDto) {

        // organizations
        if (retriever.getOrganizationsRelationRetriever() != null) {

            OrganizationCompositionHelper.preAttachToUserProfile(
                    organizationQueryProxy,
                    cache,
                    operatorId,
                    userProfileDto
            );

            Map<String, OrganizationDto> organizationDtoMap = OrganizationCompositionHelper.createOrganizationDtoMap(
                    organizationQueryProxy,
                    cache,
                    operatorId,
                    userProfileDto.stream()
                            .map(retriever.getOrganizationsRelationRetriever().getIdRetriever())
                            .flatMap(List::stream)
                            .toList(),
                    retriever.getOrganizationsRelationRetriever().getChain()
            );

            userProfileDto.forEach(user -> {
                this.attachRelationToOrganizations(
                        operatorId,
                        cache,
                        user,
                        organizationDtoMap,
                        retriever.getOrganizationsRelationRetriever().getRelationRetriever().apply(user),
                        retriever.getOrganizationsRelationRetriever().getChain()
                );
            });
        }

        // teams
        if (retriever.getTeamsRelationRetriever() != null) {

            TeamCompositionHelper.preAttachToUserProfile(
                    teamQueryProxy,
                    cache,
                    operatorId,
                    userProfileDto
            );

            Map<String, TeamDto> teamDtoMap = TeamCompositionHelper.createTeamDtoMap(
                    teamQueryProxy,
                    cache,
                    operatorId,
                    userProfileDto.stream()
                            .map(retriever.getTeamsRelationRetriever().getIdRetriever())
                            .flatMap(List::stream)
                            .toList(),
                    retriever.getTeamsRelationRetriever().getChain()
            );

            userProfileDto.forEach(user -> {
                this.attachRelationToTeams(
                        operatorId,
                        cache,
                        user,
                        teamDtoMap,
                        retriever.getTeamsRelationRetriever().getRelationRetriever().apply(user),
                        retriever.getTeamsRelationRetriever().getChain()
                );
            });
        }

        // charge tasks
        if (retriever.getChargeTasksRelationRetriever() != null) {

            TaskCompositionHelper.preAttachToChargeUser(
                    taskQueryProxy,
                    cache,
                    operatorId,
                    userProfileDto
            );

            Map<String, TaskDto> taskDtoMap = TaskCompositionHelper.createTaskDtoMap(
                    taskQueryProxy,
                    cache,
                    operatorId,
                    userProfileDto.stream()
                            .map(retriever.getChargeTasksRelationRetriever().getIdRetriever())
                            .flatMap(List::stream)
                            .toList(),
                    retriever.getChargeTasksRelationRetriever().getChain()
            );

            userProfileDto.forEach(user -> {
                this.attachRelationToChargeTasks(
                        operatorId,
                        cache,
                        user,
                        taskDtoMap,
                        retriever.getChargeTasksRelationRetriever().getRelationRetriever().apply(user),
                        retriever.getChargeTasksRelationRetriever().getChain()
                );
            });
        }

        // userPreference
        if (retriever.getUserPreferenceRelationRetriever() != null) {

            Map<String, UserPreferenceDto> userPreferenceDtoMap = UserPreferenceCompositionHelper.createUserPreferenceDtoMap(
                    userPreferenceQueryProxy,
                    cache,
                    operatorId,
                    userProfileDto.stream().map(retriever.getUserPreferenceRelationRetriever().getIdRetriever()).toList(),
                    retriever.getUserPreferenceRelationRetriever().getChain()
            );

            userProfileDto.forEach(user -> {
                this.attachRelationToUserPreference(
                        operatorId,
                        cache,
                        user,
                        userPreferenceDtoMap,
                        retriever.getUserPreferenceRelationRetriever().getIdRetriever().apply(user),
                        retriever.getUserPreferenceRelationRetriever().getRelationRetriever().apply(user),
                        retriever.getUserPreferenceRelationRetriever().getChain()
                );
            });
        }

        // owned organizations
        if (retriever.getOwnedOrganizationsRelationRetriever() != null) {

            OrganizationCompositionHelper.preAttachToOwner(
                    organizationQueryProxy,
                    cache,
                    operatorId,
                    userProfileDto
            );

            Map<String, OrganizationDto> organizationDtoMap = OrganizationCompositionHelper.createOrganizationDtoMap(
                    organizationQueryProxy,
                    cache,
                    operatorId,
                    userProfileDto.stream()
                            .map(retriever.getOwnedOrganizationsRelationRetriever().getIdRetriever())
                            .flatMap(List::stream)
                            .toList(),
                    retriever.getOwnedOrganizationsRelationRetriever().getChain()
            );

            userProfileDto.forEach(user -> {
                this.attachRelationToOwnedOrganizations(
                        operatorId,
                        cache,
                        user,
                        organizationDtoMap,
                        retriever.getOwnedOrganizationsRelationRetriever().getRelationRetriever().apply(user),
                        retriever.getOwnedOrganizationsRelationRetriever().getChain()
                );
            });
        }
    }

    protected void attachRelationToOrganizations(
            String operatorId,
            RetrievedCacheContainer cache,
            UserProfileDto userProfileDto,
            Map<String, OrganizationDto> organizationDtoMap,
            ListRelation<OrganizationOnUserProfileDto> organizationsRelation,
            List<OrganizationRetriever> retrievers
    ) {
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
            retrievers.forEach(retriever -> {
                if (retriever != null && organizationsRelation.isHasValue() && organizationsRelation.getValue() != null) {
                    // TODO: Implement this
                }
            });
        }
    }

    protected void attachRelationToTeams(
            String operatorId,
            RetrievedCacheContainer cache,
            UserProfileDto userProfileDto,
            Map<String, TeamDto> teamDtoMap,
            ListRelation<TeamOnUserProfileDto> teamsRelation,
            List<TeamRetriever> retrievers
    ) {
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
            retrievers.forEach(retriever -> {
                if (retriever != null && teamsRelation.isHasValue() && teamsRelation.getValue() != null) {
                    // TODO: Implement this
                }
            });
        }
    }

    protected void attachRelationToChargeTasks(
            String operatorId,
            RetrievedCacheContainer cache,
            UserProfileDto userProfileDto,
            Map<String, TaskDto> taskDtoMap,
            ListRelation<TaskDto> chargeTasksRelation,
            List<TaskRetriever> retrievers
    ) {
        if (userProfileDto.getChargeTasks().isHasValue()) {
            List<TaskDto> originAttachTasks = userProfileDto.getChargeTasks().getValue();
            List<String> attachTaskIds = originAttachTasks
                    .stream()
                    .map(TaskDto::getTaskId)
                    .toList();
            List<TaskDto> attachTasks = attachTaskIds.stream()
                    .map(taskDtoMap::get)
                    .toList();
            userProfileDto.setChargeTasks(ListRelation.<TaskDto>builder()
                    .value(attachTasks)
                    .build()
            );
            retrievers.forEach(retriever -> {
                if (retriever != null && chargeTasksRelation.isHasValue() && chargeTasksRelation.getValue() != null) {
                    // TODO: Implement this
                }
            });
        }
    }

    protected void attachRelationToOwnedOrganizations(
            String operatorId,
            RetrievedCacheContainer cache,
            UserProfileDto userProfileDto,
            Map<String, OrganizationDto> organizationDtoMap,
            ListRelation<OrganizationDto> ownedOrganizationsRelation,
            List<OrganizationRetriever> retrievers
    ) {
        if (userProfileDto.getOwnedOrganizations().isHasValue()) {
            List<OrganizationDto> originAttachOrganizationIds = userProfileDto.getOwnedOrganizations().getValue();
            List<String> attachOrganizationIds = originAttachOrganizationIds
                    .stream()
                    .map(OrganizationDto::getOrganizationId)
                    .toList();
            List<OrganizationDto> attachOrganizations = attachOrganizationIds.stream()
                    .map(organizationDtoMap::get)
                    .toList();
            userProfileDto.setOwnedOrganizations(ListRelation.<OrganizationDto>builder()
                    .value(attachOrganizations)
                    .build()
            );
            retrievers.forEach(retriever -> {
                if (retriever != null && ownedOrganizationsRelation.isHasValue() && ownedOrganizationsRelation.getValue() != null) {
                    // TODO: Implement this
                }
            });
        }
    }

    protected void attachRelationToUserPreference(
            String operatorId,
            RetrievedCacheContainer cache,
            UserProfileDto userProfileDto,
            Map<String, UserPreferenceDto> userPreferenceDtoMap,
            String userId,
            Relation<UserPreferenceDto> userPreferenceRelation,
            List<UserPreferenceRetriever> retrievers
    ) {
        userProfileDto.setUserPreference(
                Relation.<UserPreferenceDto>builder()
                        .hasValue(true)
                        .value(userPreferenceDtoMap.get(userId))
                        .build()
        );
        retrievers.forEach(retriever -> {
            if (retriever != null && userPreferenceRelation.isHasValue() && userPreferenceRelation.getValue() != null) {
                // TODO: Implement this
                AttachRelationUserPreference a = new AttachRelationUserPreference();
                a.attach(operatorId, cache, retriever, userPreferenceDtoMap.get(userId));
            }
        });
    }
}
