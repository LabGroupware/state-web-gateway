package org.cresplanex.api.state.webgateway.composition;

import lombok.RequiredArgsConstructor;
import org.cresplanex.api.state.webgateway.dto.domain.ListRelation;
import org.cresplanex.api.state.webgateway.dto.domain.Relation;
import org.cresplanex.api.state.webgateway.dto.domain.organization.OrganizationDto;
import org.cresplanex.api.state.webgateway.dto.domain.plan.TaskDto;
import org.cresplanex.api.state.webgateway.dto.domain.team.TeamDto;
import org.cresplanex.api.state.webgateway.dto.domain.userprofile.UserProfileDto;
import org.cresplanex.api.state.webgateway.dto.domain.userprofile.UserProfileOnTeamDto;
import org.cresplanex.api.state.webgateway.proxy.query.OrganizationQueryProxy;
import org.cresplanex.api.state.webgateway.proxy.query.TaskQueryProxy;
import org.cresplanex.api.state.webgateway.proxy.query.UserProfileQueryProxy;
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
    private final OrganizationQueryProxy organizationQueryProxy;
    private final TaskQueryProxy taskQueryProxy;

    public void attach(String operatorId, RetrievedCacheContainer cache, TeamRetriever retriever, TeamDto teamDto) {

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
                    organizationId,
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

    public void attach(String operatorId, RetrievedCacheContainer cache, TeamRetriever retriever, List<TeamDto> teamDto) {

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

            for (TeamDto dto : teamDto) {
                this.attachRelationToOrganization(
                        operatorId,
                        cache,
                        dto,
                        organizationDtoMap,
                        retriever.getOrganizationRelationRetriever().getIdRetriever().apply(dto),
                        retriever.getOrganizationRelationRetriever().getRelationRetriever().apply(dto),
                        retriever.getOrganizationRelationRetriever().getChain()
                );
            }
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

            for (TeamDto dto : teamDto) {

                this.attachRelationToUsers(
                        operatorId,
                        cache,
                        dto,
                        userProfileDtoMap,
                        retriever.getUsersRelationRetriever().getRelationRetriever().apply(dto),
                        retriever.getUsersRelationRetriever().getChain()
                );
            }
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

            for (TeamDto dto : teamDto) {

                this.attachRelationToTasks(
                        operatorId,
                        cache,
                        dto,
                        taskDtoMap,
                        retriever.getTasksRelationRetriever().getRelationRetriever().apply(dto),
                        retriever.getTasksRelationRetriever().getChain()
                );
            }
        }
    }

    protected void attachRelationToOrganization(
            String operatorId,
            RetrievedCacheContainer cache,
            TeamDto teamDto,
            Map<String, OrganizationDto> organizationDtoMap,
            String organizationId,
            Relation<OrganizationDto> organizationRelation,
            List<OrganizationRetriever> retrievers
    ) {
        teamDto.setOrganization(Relation.<OrganizationDto>builder()
                .hasValue(true)
                .value(organizationDtoMap.get(organizationId))
                .build()
        );
        retrievers.forEach(retriever -> {
            if (retriever != null && organizationRelation.isHasValue() && organizationRelation.getValue() != null) {
                // TODO: Implement this
            }
        });
    }

    protected void attachRelationToUsers(
            String operatorId,
            RetrievedCacheContainer cache,
            TeamDto teamDto,
            Map<String, UserProfileDto> userProfileDtoMap,
            ListRelation<UserProfileOnTeamDto> usersRelation,
            List<UserProfileRetriever> retrievers
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
            retrievers.forEach(retriever -> {
                if (retriever != null && usersRelation.isHasValue() && usersRelation.getValue() != null) {
                    // TODO: Implement this
                }
            });
        }
    }

    protected void attachRelationToTasks(
            String operatorId,
            RetrievedCacheContainer cache,
            TeamDto teamDto,
            Map<String, TaskDto> taskDtoMap,
            ListRelation<TaskDto> tasksRelation,
            List<TaskRetriever> retrievers
    ) {
        if (teamDto.getTasks().isHasValue()) {
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
            retrievers.forEach(retriever -> {
                if (retriever != null && tasksRelation.isHasValue() && tasksRelation.getValue() != null) {
                    // TODO: Implement this
                }
            });
        }
    }
}
