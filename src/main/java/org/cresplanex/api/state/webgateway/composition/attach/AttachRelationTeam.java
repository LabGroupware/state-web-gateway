package org.cresplanex.api.state.webgateway.composition.attach;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cresplanex.api.state.webgateway.composition.helper.OrganizationCompositionHelper;
import org.cresplanex.api.state.webgateway.composition.helper.TaskCompositionHelper;
import org.cresplanex.api.state.webgateway.composition.helper.UserProfileCompositionHelper;
import org.cresplanex.api.state.webgateway.dto.domain.ListRelation;
import org.cresplanex.api.state.webgateway.dto.domain.Relation;
import org.cresplanex.api.state.webgateway.dto.domain.organization.OrganizationDto;
import org.cresplanex.api.state.webgateway.dto.domain.plan.TaskDto;
import org.cresplanex.api.state.webgateway.dto.domain.team.TeamDto;
import org.cresplanex.api.state.webgateway.dto.domain.userprofile.UserProfileDto;
import org.cresplanex.api.state.webgateway.dto.domain.userprofile.UserProfileOnTeamDto;
import org.cresplanex.api.state.webgateway.proxy.query.*;
import org.cresplanex.api.state.webgateway.retriever.RetrievedCacheContainer;
import org.cresplanex.api.state.webgateway.retriever.domain.OrganizationRetriever;
import org.cresplanex.api.state.webgateway.retriever.domain.TaskRetriever;
import org.cresplanex.api.state.webgateway.retriever.domain.TeamRetriever;
import org.cresplanex.api.state.webgateway.retriever.domain.UserProfileRetriever;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class AttachRelationTeam {

    private final UserProfileQueryProxy userProfileQueryProxy;
    private final TeamQueryProxy teamQueryProxy;
    private final UserPreferenceQueryProxy userPreferenceQueryProxy;
    private final OrganizationQueryProxy organizationQueryProxy;
    private final TaskQueryProxy taskQueryProxy;
    private final FileObjectQueryProxy fileObjectQueryProxy;

    public <T extends TeamDto> T attach(String operatorId, RetrievedCacheContainer cache, TeamRetriever retriever, T teamDto) {

        // tasks
        if (retriever.getTasksRelationRetriever() != null) {
            TaskCompositionHelper.preAttachToTeam(
                    taskQueryProxy,
                    cache,
                    operatorId,
                    List.of(teamDto)
            );

            Map<String, TaskDto> taskDtoMap = TaskCompositionHelper.createTaskDtoMap(
                    taskQueryProxy,
                    cache,
                    operatorId,
                    retriever.getTasksRelationRetriever().getIdRetriever().apply(teamDto),
                    retriever.getTasksRelationRetriever().getChain()
            );

            this.attachRelationToTasks(
                    operatorId,
                    cache,
                    teamDto,
                    taskDtoMap,
                    retriever.getTasksRelationRetriever().getChain()
            );
        }

        // Users
        if (retriever.getUsersRelationRetriever() != null) {
            Map<String, UserProfileDto> userProfileDtoMap = UserProfileCompositionHelper.createUserProfileDtoMap(
                    userProfileQueryProxy,
                    cache,
                    operatorId,
                    retriever.getUsersRelationRetriever().getIdRetriever().apply(teamDto),
                    retriever.getUsersRelationRetriever().getChain()
            );
            this.attachRelationToUsers(
                    operatorId,
                    cache,
                    teamDto,
                    userProfileDtoMap,
                    retriever.getUsersRelationRetriever().getChain()
            );
        }

        // Organization
        if (retriever.getOrganizationRelationRetriever() != null) {
            String organizationId = retriever.getOrganizationRelationRetriever().getIdRetriever().apply(teamDto);
            Map<String, OrganizationDto> organizationDtoMap = OrganizationCompositionHelper.createOrganizationDtoMap(
                    organizationQueryProxy,
                    cache,
                    operatorId,
                    List.of(organizationId),
                    retriever.getOrganizationRelationRetriever().getChain()
            );
            this.attachRelationToOrganization(
                    operatorId,
                    cache,
                    teamDto,
                    organizationDtoMap,
                    retriever.getOrganizationRelationRetriever().getChain()
            );
        }

        return teamDto;
    }

    public <T extends TeamDto> List<T> attach(String operatorId, RetrievedCacheContainer cache, TeamRetriever retriever, List<T> teamDto) {

        // tasks
        if (retriever.getTasksRelationRetriever() != null) {

            TaskCompositionHelper.preAttachToTeam(
                    taskQueryProxy,
                    cache,
                    operatorId,
                    teamDto
            );

            Map<String, TaskDto> taskDtoMap = TaskCompositionHelper.createTaskDtoMap(
                    taskQueryProxy,
                    cache,
                    operatorId,
                    teamDto.stream()
                            .map(retriever.getTasksRelationRetriever().getIdRetriever())
                            .flatMap(List::stream)
                            .distinct()
                            .toList(),
                    retriever.getTasksRelationRetriever().getChain()
            );

            this.attachRelationToTasks(
                    operatorId,
                    cache,
                    teamDto,
                    taskDtoMap,
                    retriever.getTasksRelationRetriever().getChain()
            );
        }

        // Users
        if (retriever.getUsersRelationRetriever() != null) {
            Map<String, UserProfileDto> userProfileDtoMap = UserProfileCompositionHelper.createUserProfileDtoMap(
                    userProfileQueryProxy,
                    cache,
                    operatorId,
                    teamDto.stream()
                            .map(retriever.getUsersRelationRetriever().getIdRetriever())
                            .flatMap(List::stream)
                            .distinct()
                            .toList(),
                    retriever.getUsersRelationRetriever().getChain()
            );

            this.attachRelationToUsers(
                    operatorId,
                    cache,
                    teamDto,
                    userProfileDtoMap,
                    retriever.getUsersRelationRetriever().getChain()
            );
        }

        // Organization
        if (retriever.getOrganizationRelationRetriever() != null) {

            Map<String, OrganizationDto> organizationDtoMap = OrganizationCompositionHelper.createOrganizationDtoMap(
                    organizationQueryProxy,
                    cache,
                    operatorId,
                    teamDto.stream()
                            .map(retriever.getOrganizationRelationRetriever().getIdRetriever())
                            .toList(),
                    retriever.getOrganizationRelationRetriever().getChain()
            );

            this.attachRelationToOrganization(
                    operatorId,
                    cache,
                    teamDto,
                    organizationDtoMap,
                    retriever.getOrganizationRelationRetriever().getChain()
            );
        }

        return teamDto;
    }

    private <T extends TeamDto> void internalAttachRelationToOrganization(
            T teamDto,
            Map<String, OrganizationDto> organizationDtoMap
    ) {
        OrganizationDto originOrganizationDto = teamDto.getOrganization().getValue();
        teamDto.setOrganization(Relation.<OrganizationDto>builder()
                .hasValue(true)
                .value(organizationDtoMap.get(teamDto.getOrganizationId()).merge(originOrganizationDto))
                .build()
        );
    }

    protected <T extends TeamDto> void attachRelationToOrganization(
            String operatorId,
            RetrievedCacheContainer cache,
            T teamDto,
            Map<String, OrganizationDto> organizationDtoMap,
            List<OrganizationRetriever> retrievers
    ) {
        this.internalAttachRelationToOrganization(teamDto, organizationDtoMap);
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
                var attached = attachRelationOrganization.attach(operatorId, cache, retriever, teamDto.getOrganization().getValue());

                Map<String, OrganizationDto> attachedMap = new HashMap<>();

                attachedMap.put(attached.getOrganizationId(), attached);

                this.internalAttachRelationToOrganization(teamDto, attachedMap);
                teamDto.setOrganization(Relation.<OrganizationDto>builder()
                        .hasValue(true)
                        .value(attached)
                        .build()
                );
            }
        });
    }

    protected <T extends TeamDto> void attachRelationToOrganization(
            String operatorId,
            RetrievedCacheContainer cache,
            List<T> teamDto,
            Map<String, OrganizationDto> organizationDtoMap,
            List<OrganizationRetriever> retrievers
    ) {
        for (T dto : teamDto) {
            this.internalAttachRelationToOrganization(dto, organizationDtoMap);
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
                List<OrganizationDto> organizationRelationList = teamDto.stream()
                        .map(TeamDto::getOrganizationId)
                        .map(organizationDtoMap::get)
                        .toList();
                var attached = attachRelationOrganization.attach(operatorId, cache, retriever, organizationRelationList);

                Map<String, OrganizationDto> attachedMap = new HashMap<>();

                for (OrganizationDto organizationDto : attached) {
                    attachedMap.put(organizationDto.getOrganizationId(), organizationDto);
                }

                for (T dto : teamDto) {
                    this.internalAttachRelationToOrganization(dto, attachedMap);
                }
            }
        });
    }

    private <T extends TeamDto> void internalAttachRelationToUsers(
            T teamDto,
            Map<String, UserProfileDto> userProfileDtoMap
    ) {
        if(teamDto.getUsers().isHasValue()) {
            List<UserProfileOnTeamDto> originAttachUserProfiles = teamDto.getUsers().getValue();
            List<String> attachUserIds = originAttachUserProfiles
                    .stream()
                    .map(UserProfileOnTeamDto::getUserId)
                    .toList();
            List<UserProfileDto> attachUserProfiles = attachUserIds.stream()
                    .map(userProfileDtoMap::get)
                    .toList();
            teamDto.setUsers(ListRelation.<UserProfileOnTeamDto>builder()
                    .hasValue(true)
                    .value(
                            attachUserProfiles.stream()
                                    .map(user -> new UserProfileOnTeamDto(user.merge(originAttachUserProfiles.stream()
                                            .filter(origin -> origin.getUserId().equals(user.getUserId()))
                                            .findFirst().orElse(null))))
                                    .toList()
                    )
                    .build()
            );
        }
    }

    protected <T extends TeamDto> void attachRelationToUsers(
            String operatorId,
            RetrievedCacheContainer cache,
            T teamDto,
            Map<String, UserProfileDto> userProfileDtoMap,
            List<UserProfileRetriever> retrievers
    ) {
        this.internalAttachRelationToUsers(teamDto, userProfileDtoMap);
        retrievers.forEach(retriever -> {
            if (retriever != null && teamDto.getUsers().isHasValue() && teamDto.getUsers().getValue() != null) {
                AttachRelationUserProfile attachRelationUserProfile = new AttachRelationUserProfile(
                        userProfileQueryProxy,
                        teamQueryProxy,
                        userPreferenceQueryProxy,
                        organizationQueryProxy,
                        taskQueryProxy,
                        fileObjectQueryProxy
                );
                var attached = attachRelationUserProfile.attach(operatorId, cache, retriever, teamDto.getUsers().getValue());

                Map<String, UserProfileDto> attachedMap = new HashMap<>();

                for (UserProfileDto userProfileDto : attached) {
                    attachedMap.put(userProfileDto.getUserId(), userProfileDto);
                }

                this.internalAttachRelationToUsers(teamDto, attachedMap);
            }
        });
    }

    protected <T extends TeamDto> void attachRelationToUsers(
            String operatorId,
            RetrievedCacheContainer cache,
            List<T> teamDto,
            Map<String, UserProfileDto> userProfileDtoMap,
            List<UserProfileRetriever> retrievers
    ) {
        for (T dto : teamDto) {
            this.internalAttachRelationToUsers(dto, userProfileDtoMap);
        }
        retrievers.forEach(retriever -> {
            if (retriever != null) {
                AttachRelationUserProfile attachRelationUserProfile = new AttachRelationUserProfile(
                        userProfileQueryProxy,
                        teamQueryProxy,
                        userPreferenceQueryProxy,
                        organizationQueryProxy,
                        taskQueryProxy,
                        fileObjectQueryProxy
                );
                Set<String> seenIds = new HashSet<>();
                List<UserProfileOnTeamDto> userProfileList = teamDto.stream()
                        .map(TeamDto::getUsers)
                        .filter(ListRelation::isHasValue)
                        .map(ListRelation::getValue)
                        .flatMap(List::stream)
                        .filter(userProfile -> seenIds.add(userProfile.getUserId()))
                        .toList();
                var attached = attachRelationUserProfile.attach(operatorId, cache, retriever, userProfileList);

                Map<String, UserProfileDto> attachedMap = new HashMap<>();

                for (UserProfileDto userProfileDto : attached) {
                    attachedMap.put(userProfileDto.getUserId(), userProfileDto);
                }

                for (T dto : teamDto) {
                    this.internalAttachRelationToUsers(dto, attachedMap);
                }
            }
        });
    }

    private <T extends TeamDto> void internalAttachRelationToTasks(
            T teamDto,
            Map<String, TaskDto> taskDtoMap
    ) {
        if(teamDto.getTasks().isHasValue()) {
            List<TaskDto> originAttachTasks = teamDto.getTasks().getValue();
            List<String> attachTaskIds = originAttachTasks
                    .stream()
                    .map(TaskDto::getTaskId)
                    .toList();
            List<TaskDto> attachTasks = attachTaskIds.stream()
                    .map(taskDtoMap::get)
                    .toList();
            teamDto.setTasks(ListRelation.<TaskDto>builder()
                    .hasValue(true)
                    .value(attachTasks.stream()
                            .map(task -> task.merge(originAttachTasks.stream()
                                    .filter(origin -> origin.getTaskId().equals(task.getTaskId()))
                                    .findFirst().orElse(null)))
                            .toList()
                    )
                    .build()
            );
        }
    }

    protected <T extends TeamDto> void attachRelationToTasks(
            String operatorId,
            RetrievedCacheContainer cache,
            T teamDto,
            Map<String, TaskDto> taskDtoMap,
            List<TaskRetriever> retrievers
    ) {
        this.internalAttachRelationToTasks(teamDto, taskDtoMap);
        retrievers.forEach(retriever -> {
            if (teamDto.getTasks().isHasValue() && teamDto.getTasks().getValue() != null) {
                AttachRelationTask attachRelationTask = new AttachRelationTask(
                        userProfileQueryProxy,
                        teamQueryProxy,
                        userPreferenceQueryProxy,
                        organizationQueryProxy,
                        taskQueryProxy,
                        fileObjectQueryProxy
                );
                var attached = attachRelationTask.attach(operatorId, cache, retriever, teamDto.getTasks().getValue());

                Map<String, TaskDto> attachedMap = new HashMap<>();

                for (TaskDto taskDto : attached) {
                    attachedMap.put(taskDto.getTaskId(), taskDto);
                }

                this.internalAttachRelationToTasks(teamDto, attachedMap);
            }
        });
    }

    protected <T extends TeamDto> void attachRelationToTasks(
            String operatorId,
            RetrievedCacheContainer cache,
            List<T> teamDto,
            Map<String, TaskDto> taskDtoMap,
            List<TaskRetriever> retrievers
    ) {
        for (T dto : teamDto) {
            this.internalAttachRelationToTasks(dto, taskDtoMap);
        }

        retrievers.forEach(retriever -> {
            AttachRelationTask attachRelationTask = new AttachRelationTask(
                    userProfileQueryProxy,
                    teamQueryProxy,
                    userPreferenceQueryProxy,
                    organizationQueryProxy,
                    taskQueryProxy,
                    fileObjectQueryProxy
            );
            Set<String> seenIds = new HashSet<>();
            List<TaskDto> tasksRelation = teamDto.stream()
                    .map(TeamDto::getTasks)
                    .filter(ListRelation::isHasValue)
                    .map(ListRelation::getValue)
                    .flatMap(List::stream)
                    .filter(task -> seenIds.add(task.getTaskId()))
                    .toList();
            var attached = attachRelationTask.attach(operatorId, cache, retriever, tasksRelation);

            Map<String, TaskDto> attachedMap = new HashMap<>();

            for (TaskDto taskDto : attached) {
                attachedMap.put(taskDto.getTaskId(), taskDto);
            }

            for (T dto : teamDto) {
                this.internalAttachRelationToTasks(dto, attachedMap);
            }
        });
    }
}
