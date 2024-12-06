package org.cresplanex.api.state.webgateway.composition.attach;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cresplanex.api.state.webgateway.composition.helper.OrganizationCompositionHelper;
import org.cresplanex.api.state.webgateway.composition.helper.TaskCompositionHelper;
import org.cresplanex.api.state.webgateway.composition.helper.TeamCompositionHelper;
import org.cresplanex.api.state.webgateway.composition.helper.UserPreferenceCompositionHelper;
import org.cresplanex.api.state.webgateway.dto.domain.ListRelation;
import org.cresplanex.api.state.webgateway.dto.domain.Relation;
import org.cresplanex.api.state.webgateway.dto.domain.organization.OrganizationDto;
import org.cresplanex.api.state.webgateway.dto.domain.organization.OrganizationOnUserProfileDto;
import org.cresplanex.api.state.webgateway.dto.domain.plan.TaskDto;
import org.cresplanex.api.state.webgateway.dto.domain.team.TeamDto;
import org.cresplanex.api.state.webgateway.dto.domain.team.TeamOnUserProfileDto;
import org.cresplanex.api.state.webgateway.dto.domain.userpreference.UserPreferenceDto;
import org.cresplanex.api.state.webgateway.dto.domain.userprofile.UserProfileDto;
import org.cresplanex.api.state.webgateway.proxy.query.*;
import org.cresplanex.api.state.webgateway.retriever.RetrievedCacheContainer;
import org.cresplanex.api.state.webgateway.retriever.domain.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AttachRelationUserProfile {

    private final UserProfileQueryProxy userProfileQueryProxy;
    private final TeamQueryProxy teamQueryProxy;
    private final UserPreferenceQueryProxy userPreferenceQueryProxy;
    private final OrganizationQueryProxy organizationQueryProxy;
    private final TaskQueryProxy taskQueryProxy;
    private final FileObjectQueryProxy fileObjectQueryProxy;

    public <T extends UserProfileDto> void attach(String operatorId, RetrievedCacheContainer cache, UserProfileRetriever retriever, T userProfileDto) {

        // organizations
        if (retriever.getOrganizationsRelationRetriever() != null) {
            log.info("attachRelationToOrganizations");
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

    public <T extends UserProfileDto> void attach(String operatorId, RetrievedCacheContainer cache, UserProfileRetriever retriever, List<T> userProfileDto) {

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

            this.attachRelationToOrganizations(
                    operatorId,
                    cache,
                    userProfileDto,
                    organizationDtoMap,
                    userProfileDto.stream()
                            .map(retriever.getOrganizationsRelationRetriever().getRelationRetriever())
                            .toList(),
                    retriever.getOrganizationsRelationRetriever().getChain()
            );
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

            this.attachRelationToTeams(
                    operatorId,
                    cache,
                    userProfileDto,
                    teamDtoMap,
                    userProfileDto.stream()
                            .map(retriever.getTeamsRelationRetriever().getRelationRetriever())
                            .toList(),
                    retriever.getTeamsRelationRetriever().getChain()
            );
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

            this.attachRelationToChargeTasks(
                    operatorId,
                    cache,
                    userProfileDto,
                    taskDtoMap,
                    userProfileDto.stream()
                            .map(retriever.getChargeTasksRelationRetriever().getRelationRetriever())
                            .toList(),
                    retriever.getChargeTasksRelationRetriever().getChain()
            );
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

            this.attachRelationToUserPreference(
                    operatorId,
                    cache,
                    userProfileDto,
                    userPreferenceDtoMap,
                    userProfileDto.stream()
                            .map(retriever.getUserPreferenceRelationRetriever().getRelationRetriever())
                            .toList(),
                    retriever.getUserPreferenceRelationRetriever().getChain()
            );
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

            this.attachRelationToOwnedOrganizations(
                    operatorId,
                    cache,
                    userProfileDto,
                    organizationDtoMap,
                    userProfileDto.stream()
                            .map(retriever.getOwnedOrganizationsRelationRetriever().getRelationRetriever())
                            .toList(),
                    retriever.getOwnedOrganizationsRelationRetriever().getChain()
            );
        }
    }

    private <T extends UserProfileDto> void attachRelationToOrganizations(
            T userProfileDto,
            Map<String, OrganizationDto> organizationDtoMap
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
        }
    }

    protected <T extends UserProfileDto> void attachRelationToOrganizations(
            String operatorId,
            RetrievedCacheContainer cache,
            T userProfileDto,
            Map<String, OrganizationDto> organizationDtoMap,
            ListRelation<OrganizationOnUserProfileDto> organizationsRelation,
            List<OrganizationRetriever> retrievers
    ) {
        this.attachRelationToOrganizations(
                userProfileDto,
                organizationDtoMap
        );
        retrievers.forEach(retriever -> {
            if (retriever != null && organizationsRelation.isHasValue() && organizationsRelation.getValue() != null) {
                AttachRelationOrganization attachRelationOrganization = new AttachRelationOrganization(
                        userProfileQueryProxy,
                        teamQueryProxy,
                        userPreferenceQueryProxy,
                        organizationQueryProxy,
                        taskQueryProxy,
                        fileObjectQueryProxy
                );
                attachRelationOrganization.attach(operatorId, cache, retriever, organizationsRelation.getValue());
            }
        });
    }

    protected <T extends UserProfileDto> void attachRelationToOrganizations(
            String operatorId,
            RetrievedCacheContainer cache,
            List<T> userProfileDto,
            Map<String, OrganizationDto> organizationDtoMap,
            List<ListRelation<OrganizationOnUserProfileDto>> organizationsRelation,
            List<OrganizationRetriever> retrievers
    ) {
        for (T dto : userProfileDto) {
            this.attachRelationToOrganizations(
                    dto,
                    organizationDtoMap
            );
        }
        retrievers.forEach(retriever -> {
            List<List<OrganizationOnUserProfileDto>> organizationsRelationList = organizationsRelation.stream()
                    .filter(ListRelation::isHasValue)
                    .map(ListRelation::getValue)
                    .toList();
            if (retriever != null && !organizationsRelationList.isEmpty()) {
                AttachRelationOrganization attachRelationOrganization = new AttachRelationOrganization(
                        userProfileQueryProxy,
                        teamQueryProxy,
                        userPreferenceQueryProxy,
                        organizationQueryProxy,
                        taskQueryProxy,
                        fileObjectQueryProxy
                );
                attachRelationOrganization.attach(operatorId, cache, retriever, organizationsRelationList.stream().flatMap(List::stream).toList());
            }
        });
    }

    private <T extends UserProfileDto> void attachRelationToTeams(
            T userProfileDto,
            Map<String, TeamDto> teamDtoMap
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
        }
    }

    protected <T extends UserProfileDto> void attachRelationToTeams(
            String operatorId,
            RetrievedCacheContainer cache,
            T userProfileDto,
            Map<String, TeamDto> teamDtoMap,
            ListRelation<TeamOnUserProfileDto> teamsRelation,
            List<TeamRetriever> retrievers
    ) {
        this.attachRelationToTeams(
                userProfileDto,
                teamDtoMap
        );
        retrievers.forEach(retriever -> {
            if (retriever != null && teamsRelation.isHasValue() && teamsRelation.getValue() != null) {
                AttachRelationTeam attachRelationTeam = new AttachRelationTeam(
                        userProfileQueryProxy,
                        teamQueryProxy,
                        userPreferenceQueryProxy,
                        organizationQueryProxy,
                        taskQueryProxy,
                        fileObjectQueryProxy
                );
                attachRelationTeam.attach(operatorId, cache, retriever, teamsRelation.getValue());
            }
        });
    }

    protected <T extends UserProfileDto> void attachRelationToTeams(
            String operatorId,
            RetrievedCacheContainer cache,
            List<T> userProfileDto,
            Map<String, TeamDto> teamDtoMap,
            List<ListRelation<TeamOnUserProfileDto>> teamsRelation,
            List<TeamRetriever> retrievers
    ) {
        for (T dto : userProfileDto) {
            this.attachRelationToTeams(
                    dto,
                    teamDtoMap
            );
        }
        retrievers.forEach(retriever -> {
            List<List<TeamOnUserProfileDto>> teamsRelationList = teamsRelation.stream()
                    .filter(ListRelation::isHasValue)
                    .map(ListRelation::getValue)
                    .toList();
            if (retriever != null && !teamsRelationList.isEmpty()) {
                AttachRelationTeam attachRelationTeam = new AttachRelationTeam(
                        userProfileQueryProxy,
                        teamQueryProxy,
                        userPreferenceQueryProxy,
                        organizationQueryProxy,
                        taskQueryProxy,
                        fileObjectQueryProxy
                );
                attachRelationTeam.attach(operatorId, cache, retriever, teamsRelationList.stream().flatMap(List::stream).toList());
            }
        });
    }

    private <T extends UserProfileDto> void attachRelationToChargeTasks(
            T userProfileDto,
            Map<String, TaskDto> taskDtoMap
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
        }
    }

    protected <T extends UserProfileDto> void attachRelationToChargeTasks(
            String operatorId,
            RetrievedCacheContainer cache,
            T userProfileDto,
            Map<String, TaskDto> taskDtoMap,
            ListRelation<TaskDto> chargeTasksRelation,
            List<TaskRetriever> retrievers
    ) {
        this.attachRelationToChargeTasks(
                userProfileDto,
                taskDtoMap
        );
        if (userProfileDto.getChargeTasks().isHasValue()) {
            retrievers.forEach(retriever -> {
                if (retriever != null && chargeTasksRelation.isHasValue() && chargeTasksRelation.getValue() != null) {
                    AttachRelationTask attachRelationTask = new AttachRelationTask(
                            userProfileQueryProxy,
                            teamQueryProxy,
                            userPreferenceQueryProxy,
                            organizationQueryProxy,
                            taskQueryProxy,
                            fileObjectQueryProxy
                    );
                    attachRelationTask.attach(operatorId, cache, retriever, chargeTasksRelation.getValue());
                }
            });
        }
    }

    protected <T extends UserProfileDto> void attachRelationToChargeTasks(
            String operatorId,
            RetrievedCacheContainer cache,
            List<T> userProfileDto,
            Map<String, TaskDto> taskDtoMap,
            List<ListRelation<TaskDto>> chargeTasksRelation,
            List<TaskRetriever> retrievers
    ) {
        for (T dto : userProfileDto) {
            this.attachRelationToChargeTasks(
                    dto,
                    taskDtoMap
            );
        }
        retrievers.forEach(retriever -> {
            List<List<TaskDto>> chargeTasksRelationList = chargeTasksRelation.stream()
                    .filter(ListRelation::isHasValue)
                    .map(ListRelation::getValue)
                    .toList();
            if (retriever != null && !chargeTasksRelationList.isEmpty()) {
                AttachRelationTask attachRelationTask = new AttachRelationTask(
                        userProfileQueryProxy,
                        teamQueryProxy,
                        userPreferenceQueryProxy,
                        organizationQueryProxy,
                        taskQueryProxy,
                        fileObjectQueryProxy
                );
                attachRelationTask.attach(operatorId, cache, retriever, chargeTasksRelationList.stream().flatMap(List::stream).toList());
            }
        });
    }

    private <T extends UserProfileDto> void attachRelationToOwnedOrganizations(
            T userProfileDto,
            Map<String, OrganizationDto> organizationDtoMap
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
        }
    }

    protected <T extends UserProfileDto> void attachRelationToOwnedOrganizations(
            String operatorId,
            RetrievedCacheContainer cache,
            T userProfileDto,
            Map<String, OrganizationDto> organizationDtoMap,
            ListRelation<OrganizationDto> ownedOrganizationsRelation,
            List<OrganizationRetriever> retrievers
    ) {
        this.attachRelationToOwnedOrganizations(
                userProfileDto,
                organizationDtoMap
        );
        retrievers.forEach(retriever -> {
            if (retriever != null && ownedOrganizationsRelation.isHasValue() && ownedOrganizationsRelation.getValue() != null) {
                AttachRelationOrganization attachRelationOrganization = new AttachRelationOrganization(
                        userProfileQueryProxy,
                        teamQueryProxy,
                        userPreferenceQueryProxy,
                        organizationQueryProxy,
                        taskQueryProxy,
                        fileObjectQueryProxy
                );
                attachRelationOrganization.attach(operatorId, cache, retriever, ownedOrganizationsRelation.getValue());
            }
        });
    }

    protected <T extends UserProfileDto> void attachRelationToOwnedOrganizations(
            String operatorId,
            RetrievedCacheContainer cache,
            List<T> userProfileDto,
            Map<String, OrganizationDto> organizationDtoMap,
            List<ListRelation<OrganizationDto>> ownedOrganizationsRelation,
            List<OrganizationRetriever> retrievers
    ) {
        for (T dto : userProfileDto) {
            this.attachRelationToOwnedOrganizations(
                    dto,
                    organizationDtoMap
            );
        }
        retrievers.forEach(retriever -> {
            List<List<OrganizationDto>> ownedOrganizationsRelationList = ownedOrganizationsRelation.stream()
                    .filter(ListRelation::isHasValue)
                    .map(ListRelation::getValue)
                    .toList();
            if (retriever != null && !ownedOrganizationsRelationList.isEmpty()) {
                AttachRelationOrganization attachRelationOrganization = new AttachRelationOrganization(
                        userProfileQueryProxy,
                        teamQueryProxy,
                        userPreferenceQueryProxy,
                        organizationQueryProxy,
                        taskQueryProxy,
                        fileObjectQueryProxy
                );
                attachRelationOrganization.attach(operatorId, cache, retriever, ownedOrganizationsRelationList.stream().flatMap(List::stream).toList());
            }
        });
    }

    private <T extends UserProfileDto> void internalAttachRelationToUserPreference(
            T userProfileDto,
            Map<String, UserPreferenceDto> userPreferenceDtoMap
    ) {
        userProfileDto.setUserPreference(
                Relation.<UserPreferenceDto>builder()
                        .hasValue(true)
                        .value(userPreferenceDtoMap.get(userProfileDto.getUserId()))
                        .build()
        );
    }

    protected <T extends UserProfileDto> void attachRelationToUserPreference(
            String operatorId,
            RetrievedCacheContainer cache,
            T userProfileDto,
            Map<String, UserPreferenceDto> userPreferenceDtoMap,
            Relation<UserPreferenceDto> userPreferenceRelation,
            List<UserPreferenceRetriever> retrievers
    ) {
        this.internalAttachRelationToUserPreference(userProfileDto, userPreferenceDtoMap);
        retrievers.forEach(retriever -> {
            if (retriever != null && userPreferenceRelation.isHasValue() && userPreferenceRelation.getValue() != null) {
                AttachRelationUserPreference attachRelationUserPreference = new AttachRelationUserPreference(
                        userProfileQueryProxy,
                        teamQueryProxy,
                        userPreferenceQueryProxy,
                        organizationQueryProxy,
                        taskQueryProxy,
                        fileObjectQueryProxy
                );
                attachRelationUserPreference.attach(operatorId, cache, retriever, userPreferenceRelation.getValue());
            }
        });
    }

    protected <T extends UserProfileDto> void attachRelationToUserPreference(
            String operatorId,
            RetrievedCacheContainer cache,
            List<T> userProfileDto,
            Map<String, UserPreferenceDto> userPreferenceDtoMap,
            List<Relation<UserPreferenceDto>> userPreferenceRelation,
            List<UserPreferenceRetriever> retrievers
    ) {
        for (T dto : userProfileDto) {
            this.internalAttachRelationToUserPreference(dto, userPreferenceDtoMap);
        }
        retrievers.forEach(retriever -> {
            List<UserPreferenceDto> userPreferenceRelationList = userPreferenceRelation.stream()
                    .filter(Relation::isHasValue)
                    .map(Relation::getValue)
                    .toList();
            if (retriever != null && !userPreferenceRelationList.isEmpty()) {
                AttachRelationUserPreference attachRelationUserPreference = new AttachRelationUserPreference(
                        userProfileQueryProxy,
                        teamQueryProxy,
                        userPreferenceQueryProxy,
                        organizationQueryProxy,
                        taskQueryProxy,
                        fileObjectQueryProxy
                );
                attachRelationUserPreference.attach(operatorId, cache, retriever, userPreferenceRelationList);
            }
        });
    }
}
