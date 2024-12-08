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

import java.util.*;

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

    public <T extends UserProfileDto> T attach(String operatorId, RetrievedCacheContainer cache, UserProfileRetriever retriever, T userProfileDto) {

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
                    retriever.getOwnedOrganizationsRelationRetriever().getChain()
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
                    retriever.getUserPreferenceRelationRetriever().getChain()
            );
        }

        return userProfileDto;
    }

    public <T extends UserProfileDto> List<T> attach(
            String operatorId,
            RetrievedCacheContainer cache,
            UserProfileRetriever retriever,
            List<T> userProfileDto
    ) {
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
                            .distinct()
                            .toList(),
                    retriever.getOrganizationsRelationRetriever().getChain()
            );

            this.attachRelationToOrganizations(
                    operatorId,
                    cache,
                    userProfileDto,
                    organizationDtoMap,
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
                            .distinct()
                            .toList(),
                    retriever.getTeamsRelationRetriever().getChain()
            );

            this.attachRelationToTeams(
                    operatorId,
                    cache,
                    userProfileDto,
                    teamDtoMap,
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
                            .distinct()
                            .toList(),
                    retriever.getChargeTasksRelationRetriever().getChain()
            );

            this.attachRelationToChargeTasks(
                    operatorId,
                    cache,
                    userProfileDto,
                    taskDtoMap,
                    retriever.getChargeTasksRelationRetriever().getChain()
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
                            .distinct()
                            .toList(),
                    retriever.getOwnedOrganizationsRelationRetriever().getChain()
            );

            this.attachRelationToOwnedOrganizations(
                    operatorId,
                    cache,
                    userProfileDto,
                    organizationDtoMap,
                    retriever.getOwnedOrganizationsRelationRetriever().getChain()
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
                    retriever.getUserPreferenceRelationRetriever().getChain()
            );
        }

        return userProfileDto;
    }

    private <T extends UserProfileDto, U extends OrganizationDto> void internalAttachRelationToOrganizations(
            T userProfileDto,
            Map<String, U> organizationDtoMap
    ) {
        if (userProfileDto.getOrganizations().isHasValue()) {
            List<OrganizationOnUserProfileDto> originAttachOrganizationIds = userProfileDto.getOrganizations().getValue();
            List<String> attachOrganizationIds = originAttachOrganizationIds
                    .stream()
                    .map(OrganizationDto::getOrganizationId)
                    .toList();
            List<U> attachOrganizations = attachOrganizationIds.stream()
                    .map(organizationDtoMap::get)
                    .toList();
            userProfileDto.setOrganizations(ListRelation.<OrganizationOnUserProfileDto>builder()
                    .hasValue(true)
                    .value(
                            attachOrganizations.stream()
                                    .map(org -> new OrganizationOnUserProfileDto(org.merge(originAttachOrganizationIds.stream()
                                            .filter(origin -> origin.getOrganizationId().equals(org.getOrganizationId()))
                                            .findFirst()
                                            .orElse(null))))
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
            List<OrganizationRetriever> retrievers
    ) {
        this.internalAttachRelationToOrganizations(
                userProfileDto,
                organizationDtoMap
        );
        retrievers.forEach(retriever -> {
            if (userProfileDto.getOrganizations().isHasValue() && userProfileDto.getOrganizations().getValue() != null) {
                AttachRelationOrganization attachRelationOrganization = new AttachRelationOrganization(
                        userProfileQueryProxy,
                        teamQueryProxy,
                        userPreferenceQueryProxy,
                        organizationQueryProxy,
                        taskQueryProxy,
                        fileObjectQueryProxy
                );
                var attached = attachRelationOrganization.attach(operatorId, cache, retriever, userProfileDto.getOrganizations().getValue());

                Map<String, OrganizationOnUserProfileDto> attachedMap = new HashMap<>();

                for (OrganizationOnUserProfileDto organizationDto : attached) {
                    attachedMap.put(organizationDto.getOrganizationId(), organizationDto);
                }

                this.internalAttachRelationToOrganizations(userProfileDto, attachedMap);
            }
        });
    }

    protected <T extends UserProfileDto> void attachRelationToOrganizations(
            String operatorId,
            RetrievedCacheContainer cache,
            List<T> userProfileDto,
            Map<String, OrganizationDto> organizationDtoMap,
            List<OrganizationRetriever> retrievers
    ) {
        for (T dto : userProfileDto) {
            this.internalAttachRelationToOrganizations(
                    dto,
                    organizationDtoMap
            );
        }
        retrievers.forEach(retriever -> {
            AttachRelationOrganization attachRelationOrganization = new AttachRelationOrganization(
                    userProfileQueryProxy,
                    teamQueryProxy,
                    userPreferenceQueryProxy,
                    organizationQueryProxy,
                    taskQueryProxy,
                    fileObjectQueryProxy
            );
            Set<String> seenIds = new HashSet<>();
            List<OrganizationOnUserProfileDto> organizationsRelationListFiltered = userProfileDto.stream()
                    .flatMap(org -> org.getOrganizations().getValue().stream())
                    .filter(org -> seenIds.add(org.getOrganizationId()))
                    .toList();
            var attached = attachRelationOrganization.attach(
                    operatorId,
                    cache,
                    retriever,
                    organizationsRelationListFiltered
            );

            Map<String, OrganizationOnUserProfileDto> attachedMap = new HashMap<>();

            for (OrganizationOnUserProfileDto organizationDto : attached) {
                attachedMap.put(organizationDto.getOrganizationId(), organizationDto);
            }

            for (T dto : userProfileDto) {
                this.internalAttachRelationToOrganizations(dto, attachedMap);
            }
        });
    }

    private <T extends UserProfileDto, U extends TeamDto> void internalAttachRelationToTeams(
            T userProfileDto,
            Map<String, U> teamDtoMap
    ) {
        if (userProfileDto.getTeams().isHasValue()) {
            List<TeamOnUserProfileDto> originAttachTeamIds = userProfileDto.getTeams().getValue();
            List<String> attachTeamIds = originAttachTeamIds
                    .stream()
                    .map(TeamDto::getTeamId)
                    .toList();
            List<U> attachTeams = attachTeamIds.stream()
                    .map(teamDtoMap::get)
                    .toList();
            userProfileDto.setTeams(ListRelation.<TeamOnUserProfileDto>builder()
                    .hasValue(true)
                    .value(
                            attachTeams.stream()
                                    .map(team -> new TeamOnUserProfileDto(team.merge(originAttachTeamIds.stream()
                                            .filter(origin -> origin.getTeamId().equals(team.getTeamId()))
                                            .findFirst()
                                            .orElse(null))))
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
            List<TeamRetriever> retrievers
    ) {
        this.internalAttachRelationToTeams(
                userProfileDto,
                teamDtoMap
        );
        retrievers.forEach(retriever -> {
            if (userProfileDto.getTeams().isHasValue() && userProfileDto.getTeams().getValue() != null) {
                AttachRelationTeam attachRelationTeam = new AttachRelationTeam(
                        userProfileQueryProxy,
                        teamQueryProxy,
                        userPreferenceQueryProxy,
                        organizationQueryProxy,
                        taskQueryProxy,
                        fileObjectQueryProxy
                );
                var attached = attachRelationTeam.attach(operatorId, cache, retriever, userProfileDto.getTeams().getValue());

                Map<String, TeamOnUserProfileDto> attachedMap = new HashMap<>();

                for (TeamOnUserProfileDto teamDto : attached) {
                    attachedMap.put(teamDto.getTeamId(), teamDto);
                }

                this.internalAttachRelationToTeams(userProfileDto, attachedMap);
            }
        });
    }

    protected <T extends UserProfileDto> void attachRelationToTeams(
            String operatorId,
            RetrievedCacheContainer cache,
            List<T> userProfileDto,
            Map<String, TeamDto> teamDtoMap,
            List<TeamRetriever> retrievers
    ) {
        for (T dto : userProfileDto) {
            this.internalAttachRelationToTeams(
                    dto,
                    teamDtoMap
            );
        }
        retrievers.forEach(retriever -> {
            AttachRelationTeam attachRelationTeam = new AttachRelationTeam(
                    userProfileQueryProxy,
                    teamQueryProxy,
                    userPreferenceQueryProxy,
                    organizationQueryProxy,
                    taskQueryProxy,
                    fileObjectQueryProxy
            );
            Set<String> seenIds = new HashSet<>();
            List<TeamOnUserProfileDto> teamsRelationListFiltered = userProfileDto.stream()
                    .flatMap(team -> team.getTeams().getValue().stream())
                    .filter(team -> seenIds.add(team.getTeamId()))
                    .toList();
            var attached = attachRelationTeam.attach(
                    operatorId,
                    cache,
                    retriever,
                    teamsRelationListFiltered
            );

            Map<String, TeamOnUserProfileDto> attachedMap = new HashMap<>();

            for (TeamOnUserProfileDto teamDto : attached) {
                attachedMap.put(teamDto.getTeamId(), teamDto);
            }

            for (T dto : userProfileDto) {
                this.internalAttachRelationToTeams(dto, attachedMap);
            }
        });
    }

    private <T extends UserProfileDto> void internalAttachRelationToChargeTasks(
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
                    .hasValue(true)
                    .value(attachTasks.stream()
                            .map(task -> task.merge(originAttachTasks.stream()
                                    .filter(origin -> origin.getTaskId().equals(task.getTaskId()))
                                    .findFirst()
                                    .orElse(null)))
                            .toList()
                    )
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
        this.internalAttachRelationToChargeTasks(
                userProfileDto,
                taskDtoMap
        );
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
                var attached = attachRelationTask.attach(operatorId, cache, retriever, chargeTasksRelation.getValue());

                Map<String, TaskDto> attachedMap = new HashMap<>();

                for (TaskDto taskDto : attached) {
                    attachedMap.put(taskDto.getTaskId(), taskDto);
                }

                this.internalAttachRelationToChargeTasks(userProfileDto, attachedMap);
            }
        });
    }

    protected <T extends UserProfileDto> void attachRelationToChargeTasks(
            String operatorId,
            RetrievedCacheContainer cache,
            List<T> userProfileDto,
            Map<String, TaskDto> taskDtoMap,
            List<TaskRetriever> retrievers
    ) {
        for (T dto : userProfileDto) {
            this.internalAttachRelationToChargeTasks(
                    dto,
                    taskDtoMap
            );
        }
        retrievers.forEach(retriever -> {
            if (retriever != null) {
                AttachRelationTask attachRelationTask = new AttachRelationTask(
                        userProfileQueryProxy,
                        teamQueryProxy,
                        userPreferenceQueryProxy,
                        organizationQueryProxy,
                        taskQueryProxy,
                        fileObjectQueryProxy
                );
                Set<String> seenIds = new HashSet<>();
                List<TaskDto> chargeTasksRelationListFiltered = userProfileDto.stream()
                        .flatMap(task -> task.getChargeTasks().getValue().stream())
                        .filter(task -> seenIds.add(task.getTaskId()))
                        .toList();
                var attached = attachRelationTask.attach(
                        operatorId,
                        cache,
                        retriever,
                        chargeTasksRelationListFiltered
                );

                Map<String, TaskDto> attachedMap = new HashMap<>();

                for (TaskDto taskDto : attached) {
                    attachedMap.put(taskDto.getTaskId(), taskDto);
                }

                for (T dto : userProfileDto) {
                    this.internalAttachRelationToChargeTasks(dto, attachedMap);
                }
            }
        });
    }

    private <T extends UserProfileDto> void internalAttachRelationToOwnedOrganizations(
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
                    .hasValue(true)
                    .value(attachOrganizations.stream()
                            .map(org -> org.merge(originAttachOrganizationIds.stream()
                                    .filter(origin -> origin.getOrganizationId().equals(org.getOrganizationId()))
                                    .findFirst()
                                    .orElse(null)))
                            .toList()
                    )
                    .build()
            );
        }
    }

    protected <T extends UserProfileDto> void attachRelationToOwnedOrganizations(
            String operatorId,
            RetrievedCacheContainer cache,
            T userProfileDto,
            Map<String, OrganizationDto> organizationDtoMap,
            List<OrganizationRetriever> retrievers
    ) {
        this.internalAttachRelationToOwnedOrganizations(
                userProfileDto,
                organizationDtoMap
        );
        retrievers.forEach(retriever -> {
            if (userProfileDto.getOwnedOrganizations().isHasValue() && userProfileDto.getOwnedOrganizations().getValue() != null) {
                AttachRelationOrganization attachRelationOrganization = new AttachRelationOrganization(
                        userProfileQueryProxy,
                        teamQueryProxy,
                        userPreferenceQueryProxy,
                        organizationQueryProxy,
                        taskQueryProxy,
                        fileObjectQueryProxy
                );
                var attached = attachRelationOrganization.attach(operatorId, cache, retriever, userProfileDto.getOwnedOrganizations().getValue());

                Map<String, OrganizationDto> attachedMap = new HashMap<>();

                for (OrganizationDto organizationDto : attached) {
                    attachedMap.put(organizationDto.getOrganizationId(), organizationDto);
                }

                this.internalAttachRelationToOwnedOrganizations(userProfileDto, attachedMap);
            }
        });
    }

    protected <T extends UserProfileDto> void attachRelationToOwnedOrganizations(
            String operatorId,
            RetrievedCacheContainer cache,
            List<T> userProfileDto,
            Map<String, OrganizationDto> organizationDtoMap,
            List<OrganizationRetriever> retrievers
    ) {
        for (T dto : userProfileDto) {
            this.internalAttachRelationToOwnedOrganizations(
                    dto,
                    organizationDtoMap
            );
        }
        retrievers.forEach(retriever -> {
            if (retriever != null) {
                AttachRelationOrganization attachRelationOrganization = new AttachRelationOrganization(
                        userProfileQueryProxy,
                        teamQueryProxy,
                        userPreferenceQueryProxy,
                        organizationQueryProxy,
                        taskQueryProxy,
                        fileObjectQueryProxy
                );
                Set<String> seenIds = new HashSet<>();
                List<OrganizationDto> ownedOrganizationsRelationListFiltered = userProfileDto.stream()
                        .flatMap(org -> org.getOwnedOrganizations().getValue().stream())
                        .filter(org -> seenIds.add(org.getOrganizationId()))
                        .toList();
                var attached = attachRelationOrganization.attach(
                        operatorId,
                        cache,
                        retriever,
                        ownedOrganizationsRelationListFiltered
                );

                Map<String, OrganizationDto> attachedMap = new HashMap<>();

                for (OrganizationDto organizationDto : attached) {
                    attachedMap.put(organizationDto.getOrganizationId(), organizationDto);
                }

                for (T dto : userProfileDto) {
                    this.internalAttachRelationToOwnedOrganizations(dto, attachedMap);
                }
            }
        });
    }

    private <T extends UserProfileDto> void internalAttachRelationToUserPreference(
            T userProfileDto,
            Map<String, UserPreferenceDto> userPreferenceDtoMap
    ) {
        UserPreferenceDto originAttachUserPreference = userProfileDto.getUserPreference().getValue();
        userProfileDto.setUserPreference(
                Relation.<UserPreferenceDto>builder()
                        .hasValue(true)
                        .value(userPreferenceDtoMap.get(userProfileDto.getUserId()).merge(originAttachUserPreference))
                        .build()
        );
    }

    protected <T extends UserProfileDto> void attachRelationToUserPreference(
            String operatorId,
            RetrievedCacheContainer cache,
            T userProfileDto,
            Map<String, UserPreferenceDto> userPreferenceDtoMap,
            List<UserPreferenceRetriever> retrievers
    ) {
        this.internalAttachRelationToUserPreference(userProfileDto, userPreferenceDtoMap);
        retrievers.forEach(retriever -> {
            if (retriever != null && userProfileDto.getUserPreference().isHasValue() && userProfileDto.getUserPreference().getValue() != null) {
                AttachRelationUserPreference attachRelationUserPreference = new AttachRelationUserPreference(
                        userProfileQueryProxy,
                        teamQueryProxy,
                        userPreferenceQueryProxy,
                        organizationQueryProxy,
                        taskQueryProxy,
                        fileObjectQueryProxy
                );
                var attached = attachRelationUserPreference.attach(operatorId, cache, retriever, userProfileDto.getUserPreference().getValue());

                Map<String, UserPreferenceDto> attachedMap = new HashMap<>();

                attachedMap.put(attached.getUserId(), attached);

                this.internalAttachRelationToUserPreference(userProfileDto, attachedMap);
            }
        });
    }

    protected <T extends UserProfileDto> void attachRelationToUserPreference(
            String operatorId,
            RetrievedCacheContainer cache,
            List<T> userProfileDto,
            Map<String, UserPreferenceDto> userPreferenceDtoMap,
            List<UserPreferenceRetriever> retrievers
    ) {
        for (T dto : userProfileDto) {
            this.internalAttachRelationToUserPreference(dto, userPreferenceDtoMap);
        }
        retrievers.forEach(retriever -> {
            if (retriever != null) {
                AttachRelationUserPreference attachRelationUserPreference = new AttachRelationUserPreference(
                        userProfileQueryProxy,
                        teamQueryProxy,
                        userPreferenceQueryProxy,
                        organizationQueryProxy,
                        taskQueryProxy,
                        fileObjectQueryProxy
                );
                var attached = attachRelationUserPreference.attach(
                        operatorId,
                        cache,
                        retriever,
                        userProfileDto.stream()
                                .map(UserProfileDto::getUserPreference)
                                .map(Relation::getValue)
                                .toList()
                );

                Map<String, UserPreferenceDto> attachedMap = new HashMap<>();

                for (UserPreferenceDto userPreferenceDto : attached) {
                    attachedMap.put(userPreferenceDto.getUserId(), userPreferenceDto);
                }

                for (T dto : userProfileDto) {
                    this.internalAttachRelationToUserPreference(dto, attachedMap);
                }
            }
        });
    }
}
