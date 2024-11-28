package org.cresplanex.api.state.webgateway.composition.attach;

import lombok.RequiredArgsConstructor;
import org.cresplanex.api.state.webgateway.composition.helper.OrganizationCompositionHelper;
import org.cresplanex.api.state.webgateway.composition.helper.TaskCompositionHelper;
import org.cresplanex.api.state.webgateway.composition.helper.UserProfileCompositionHelper;
import org.cresplanex.api.state.webgateway.dto.domain.ListRelation;
import org.cresplanex.api.state.webgateway.dto.domain.Relation;
import org.cresplanex.api.state.webgateway.dto.domain.organization.OrganizationDto;
import org.cresplanex.api.state.webgateway.dto.domain.plan.TaskDto;
import org.cresplanex.api.state.webgateway.dto.domain.team.TeamDto;
import org.cresplanex.api.state.webgateway.dto.domain.userprofile.UserProfileDto;
import org.cresplanex.api.state.webgateway.dto.domain.userprofile.UserProfileOnOrganizationDto;
import org.cresplanex.api.state.webgateway.dto.domain.userprofile.UserProfileOnTeamDto;
import org.cresplanex.api.state.webgateway.proxy.query.*;
import org.cresplanex.api.state.webgateway.retriever.RetrievedCacheContainer;
import org.cresplanex.api.state.webgateway.retriever.domain.OrganizationRetriever;
import org.cresplanex.api.state.webgateway.retriever.domain.TaskRetriever;
import org.cresplanex.api.state.webgateway.retriever.domain.TeamRetriever;
import org.cresplanex.api.state.webgateway.retriever.domain.UserProfileRetriever;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class AttachRelationTeam {

    private final UserProfileQueryProxy userProfileQueryProxy;
    private final TeamQueryProxy teamQueryProxy;
    private final UserPreferenceQueryProxy userPreferenceQueryProxy;
    private final OrganizationQueryProxy organizationQueryProxy;
    private final TaskQueryProxy taskQueryProxy;
    private final FileObjectQueryProxy fileObjectQueryProxy;

    public <T extends TeamDto> void attach(String operatorId, RetrievedCacheContainer cache, TeamRetriever retriever, T teamDto) {

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
                    retriever.getOrganizationRelationRetriever().getRelationRetriever().apply(teamDto),
                    retriever.getOrganizationRelationRetriever().getChain()
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
                    retriever.getUsersRelationRetriever().getRelationRetriever().apply(teamDto),
                    retriever.getUsersRelationRetriever().getChain()
            );
        }

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
                    retriever.getTasksRelationRetriever().getRelationRetriever().apply(teamDto),
                    retriever.getTasksRelationRetriever().getChain()
            );
        }
    }

    public <T extends TeamDto> void attach(String operatorId, RetrievedCacheContainer cache, TeamRetriever retriever, List<T> teamDto) {

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
                    teamDto.stream()
                            .map(retriever.getOrganizationRelationRetriever().getRelationRetriever())
                            .toList(),
                    retriever.getOrganizationRelationRetriever().getChain()
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
                            .toList(),
                    retriever.getUsersRelationRetriever().getChain()
            );

            this.attachRelationToUsers(
                    operatorId,
                    cache,
                    teamDto,
                    userProfileDtoMap,
                    teamDto.stream()
                            .map(retriever.getUsersRelationRetriever().getRelationRetriever())
                            .toList(),
                    retriever.getUsersRelationRetriever().getChain()
            );
        }

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
                            .toList(),
                    retriever.getTasksRelationRetriever().getChain()
            );

            this.attachRelationToTasks(
                    operatorId,
                    cache,
                    teamDto,
                    taskDtoMap,
                    teamDto.stream()
                            .map(retriever.getTasksRelationRetriever().getRelationRetriever())
                            .toList(),
                    retriever.getTasksRelationRetriever().getChain()
            );
        }
    }

    private <T extends TeamDto> void internalAttachRelationToOrganization(
            T teamDto,
            Map<String, OrganizationDto> organizationDtoMap,
            String organizationId
    ) {
        teamDto.setOrganization(Relation.<OrganizationDto>builder()
                .hasValue(true)
                .value(organizationDtoMap.get(organizationId))
                .build()
        );
    }

    protected <T extends TeamDto> void attachRelationToOrganization(
            String operatorId,
            RetrievedCacheContainer cache,
            T teamDto,
            Map<String, OrganizationDto> organizationDtoMap,
            Relation<OrganizationDto> organizationRelation,
            List<OrganizationRetriever> retrievers
    ) {
        this.internalAttachRelationToOrganization(teamDto, organizationDtoMap, teamDto.getOrganizationId());
        retrievers.forEach(retriever -> {
            if (retriever != null && organizationRelation.isHasValue() && organizationRelation.getValue() != null) {
                AttachRelationOrganization attachRelationOrganization = new AttachRelationOrganization(
                        userProfileQueryProxy,
                        teamQueryProxy,
                        userPreferenceQueryProxy,
                        organizationQueryProxy,
                        taskQueryProxy,
                        fileObjectQueryProxy
                );
                attachRelationOrganization.attach(operatorId, cache, retriever, organizationRelation.getValue());
            }
        });
    }

    protected <T extends TeamDto> void attachRelationToOrganization(
            String operatorId,
            RetrievedCacheContainer cache,
            List<T> teamDto,
            Map<String, OrganizationDto> organizationDtoMap,
            List<Relation<OrganizationDto>> organizationRelation,
            List<OrganizationRetriever> retrievers
    ) {
        for (T dto : teamDto) {
            this.internalAttachRelationToOrganization(dto, organizationDtoMap, dto.getOrganizationId());
        }
        retrievers.forEach(retriever -> {
            List<OrganizationDto> organizationRelationList = organizationRelation.stream()
                    .filter(Relation::isHasValue)
                    .map(Relation::getValue)
                    .toList();
            if (retriever != null && !organizationRelationList.isEmpty()) {
                AttachRelationOrganization attachRelationOrganization = new AttachRelationOrganization(
                        userProfileQueryProxy,
                        teamQueryProxy,
                        userPreferenceQueryProxy,
                        organizationQueryProxy,
                        taskQueryProxy,
                        fileObjectQueryProxy
                );
                attachRelationOrganization.attach(operatorId, cache, retriever, organizationRelationList);
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
                    .value(
                            attachUserProfiles.stream()
                                    .map(user -> new UserProfileOnTeamDto(user, originAttachUserProfiles.stream()
                                            .filter(origin -> origin.getUserId().equals(user.getUserId()))
                                            .findFirst()
                                            .orElse(null)))
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
            ListRelation<UserProfileOnTeamDto> usersRelation,
            List<UserProfileRetriever> retrievers
    ) {
        this.internalAttachRelationToUsers(teamDto, userProfileDtoMap);
        retrievers.forEach(retriever -> {
            if (retriever != null && usersRelation.isHasValue() && usersRelation.getValue() != null) {
                AttachRelationUserProfile attachRelationUserProfile = new AttachRelationUserProfile(
                        userProfileQueryProxy,
                        teamQueryProxy,
                        userPreferenceQueryProxy,
                        organizationQueryProxy,
                        taskQueryProxy,
                        fileObjectQueryProxy
                );
                attachRelationUserProfile.attach(operatorId, cache, retriever, usersRelation.getValue());
            }
        });
    }

    protected <T extends TeamDto> void attachRelationToUsers(
            String operatorId,
            RetrievedCacheContainer cache,
            List<T> teamDto,
            Map<String, UserProfileDto> userProfileDtoMap,
            List<ListRelation<UserProfileOnTeamDto>> usersRelation,
            List<UserProfileRetriever> retrievers
    ) {
        for (T dto : teamDto) {
            this.internalAttachRelationToUsers(dto, userProfileDtoMap);
        }
        retrievers.forEach(retriever -> {
            List<List<UserProfileOnTeamDto>> usersRelationList = usersRelation.stream()
                    .filter(ListRelation::isHasValue)
                    .map(ListRelation::getValue)
                    .toList();
            if (retriever != null && !usersRelationList.isEmpty()) {
                AttachRelationUserProfile attachRelationUserProfile = new AttachRelationUserProfile(
                        userProfileQueryProxy,
                        teamQueryProxy,
                        userPreferenceQueryProxy,
                        organizationQueryProxy,
                        taskQueryProxy,
                        fileObjectQueryProxy
                );
                attachRelationUserProfile.attach(operatorId, cache, retriever, usersRelationList.stream()
                        .flatMap(List::stream)
                        .toList());
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
                    .value(attachTasks)
                    .build()
            );
        }
    }

    protected <T extends TeamDto> void attachRelationToTasks(
            String operatorId,
            RetrievedCacheContainer cache,
            T teamDto,
            Map<String, TaskDto> taskDtoMap,
            ListRelation<TaskDto> tasksRelation,
            List<TaskRetriever> retrievers
    ) {
        this.internalAttachRelationToTasks(teamDto, taskDtoMap);
        if(teamDto.getTasks().isHasValue()) {
            retrievers.forEach(retriever -> {
                if (retriever != null && tasksRelation.isHasValue() && tasksRelation.getValue() != null) {
                    AttachRelationTask attachRelationTask = new AttachRelationTask(
                            userProfileQueryProxy,
                            teamQueryProxy,
                            userPreferenceQueryProxy,
                            organizationQueryProxy,
                            taskQueryProxy,
                            fileObjectQueryProxy
                    );
                    attachRelationTask.attach(operatorId, cache, retriever, tasksRelation.getValue());
                }
            });
        }
    }

    protected <T extends TeamDto> void attachRelationToTasks(
            String operatorId,
            RetrievedCacheContainer cache,
            List<T> teamDto,
            Map<String, TaskDto> taskDtoMap,
            List<ListRelation<TaskDto>> tasksRelation,
            List<TaskRetriever> retrievers
    ) {
        for (T dto : teamDto) {
            this.internalAttachRelationToTasks(dto, taskDtoMap);
        }

        retrievers.forEach(retriever -> {
            List<List<TaskDto>> tasksRelationList = tasksRelation.stream()
                    .filter(ListRelation::isHasValue)
                    .map(ListRelation::getValue)
                    .toList();
            if (retriever != null && !tasksRelationList.isEmpty()) {
                AttachRelationTask attachRelationTask = new AttachRelationTask(
                        userProfileQueryProxy,
                        teamQueryProxy,
                        userPreferenceQueryProxy,
                        organizationQueryProxy,
                        taskQueryProxy,
                        fileObjectQueryProxy
                );
                attachRelationTask.attach(operatorId, cache, retriever, tasksRelationList.stream().flatMap(List::stream).toList());
            }
        });
    }
}
