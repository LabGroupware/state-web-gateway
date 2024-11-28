package org.cresplanex.api.state.webgateway.composition;

import lombok.RequiredArgsConstructor;
import org.cresplanex.api.state.webgateway.dto.ListResponseDto;
import org.cresplanex.api.state.webgateway.dto.domain.ListRelation;
import org.cresplanex.api.state.webgateway.dto.domain.Relation;
import org.cresplanex.api.state.webgateway.dto.domain.organization.OrganizationDto;
import org.cresplanex.api.state.webgateway.dto.domain.plan.TaskDto;
import org.cresplanex.api.state.webgateway.dto.domain.team.TeamDto;
import org.cresplanex.api.state.webgateway.dto.domain.userprofile.UserProfileDto;
import org.cresplanex.api.state.webgateway.dto.domain.userprofile.UserProfileOnTeamDto;
import org.cresplanex.api.state.webgateway.hasher.OrganizationHasher;
import org.cresplanex.api.state.webgateway.hasher.TaskHasher;
import org.cresplanex.api.state.webgateway.hasher.UserProfileHasher;
import org.cresplanex.api.state.webgateway.proxy.query.OrganizationQueryProxy;
import org.cresplanex.api.state.webgateway.proxy.query.TaskQueryProxy;
import org.cresplanex.api.state.webgateway.proxy.query.UserProfileQueryProxy;
import org.cresplanex.api.state.webgateway.retriever.RetrievedCacheContainer;
import org.cresplanex.api.state.webgateway.retriever.domain.OrganizationRetriever;
import org.cresplanex.api.state.webgateway.retriever.domain.TaskRetriever;
import org.cresplanex.api.state.webgateway.retriever.domain.TeamRetriever;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class AttachRelationTeam {

    private final UserProfileQueryProxy userProfileQueryProxy;
    private final OrganizationQueryProxy organizationQueryProxy;
    private final TaskQueryProxy taskQueryProxy;

    public static final int NEED_ORGANIZATION_USERS = 1 << 0;
    public static final int NEED_TASK_ATTACHED_FILE_OBJECTS = 1 << 0;

    public static final int GET_ORGANIZATION_WITH_USERS = NEED_ORGANIZATION_USERS;
    public static final int GET_TASK_ATTACHED_FILE_OBJECTS = NEED_TASK_ATTACHED_FILE_OBJECTS;

    public void attach(String operatorId, RetrievedCacheContainer cache, TeamRetriever retriever, TeamDto teamDto) {

        // Organization
        if (retriever.getOrganizationRelationRetriever() != null) {
            // 取得が必要なIDの取得
            String organizationId = retriever.getOrganizationRelationRetriever().getIdRetriever().apply(teamDto);
            // 取得を行う
            // サービス内でのリレーション取得が必要な場合は, ここで取得する必要がある.
            int need = 0;
            for (OrganizationRetriever subRetriever : retriever.getOrganizationRelationRetriever().getChain()) {
                if (subRetriever != null && subRetriever.getUsersRelationRetriever() != null) {
                    need |= NEED_ORGANIZATION_USERS;
                }
            }
            OrganizationDto organizationDto;
            switch (need) {
                case GET_ORGANIZATION_WITH_USERS:
                    if (cache.getCache().containsKey(OrganizationHasher.hashOrganizationWithUsers(organizationId))) {
                        organizationDto = (OrganizationDto) cache.getCache().get(OrganizationHasher.hashOrganizationWithUsers(organizationId));
                        break;
                    }
                    List<OrganizationDto> organizations = organizationQueryProxy.getPluralOrganizationsWithUsers(
                            operatorId,
                            List.of(organizationId),
                            null,
                            null
                    ).getListData();
                    if (organizations.isEmpty()) {
                        organizationDto = null;
                        break;
                    }
                    organizationDto = organizations.getFirst();
                    cache.getCache().put(OrganizationHasher.hashOrganizationWithUsers(organizationId), organizationDto.deepClone());
                    break;
                default:
                    if (cache.getCache().containsKey(OrganizationHasher.hashOrganization(organizationId))) {
                        organizationDto = (OrganizationDto) cache.getCache().get(OrganizationHasher.hashOrganization(organizationId));
                        break;
                    }
                    ListResponseDto.InternalData<OrganizationDto> organization = organizationQueryProxy.getPluralOrganizations(
                            operatorId,
                            List.of(organizationId),
                            null,
                            null
                    );
                    organizationDto = organization.getListData().getFirst();
                    cache.getCache().put(OrganizationHasher.hashOrganization(organizationId), organizationDto.deepClone());
                    break;
            }
            teamDto.setOrganization(Relation.<OrganizationDto>builder()
                    .hasValue(true)
                    .value(organizationDto)
                    .build()
            );
            Relation<OrganizationDto> organizationRelation = retriever.getOrganizationRelationRetriever().getRelationRetriever().apply(teamDto);
            retriever.getOrganizationRelationRetriever().getChain().forEach(subRetriever -> {
                if (subRetriever != null && organizationRelation.isHasValue() && organizationRelation.getValue() != null) {
                    // TODO: Implement this
                }
            });
        }

        // Users
        if (retriever.getUsersRelationRetriever() != null) {
            // 取得が必要なIDの取得
            List<String> userIds = retriever.getUsersRelationRetriever().getIdRetriever().apply(teamDto);
            List<String> needRetrieveUserIds = new ArrayList<>();
            Map<String, UserProfileDto> userProfileDtoMap = new HashMap<>();
            for (String userId : userIds) {
                if (cache.getCache().containsKey(UserProfileHasher.hashUserProfileByUserId(userId))) {
                    userProfileDtoMap.put(userId, (UserProfileDto) cache.getCache().get(UserProfileHasher.hashUserProfileByUserId(userId)));
                    break;
                }else {
                    needRetrieveUserIds.add(userId);
                }
            }

            if (!needRetrieveUserIds.isEmpty()) {
                List<UserProfileDto> userProfile = userProfileQueryProxy.getPluralUserProfilesByUserIds(
                        operatorId,
                        needRetrieveUserIds,
                        null,
                        null
                ).getListData();
                for (UserProfileDto dto : userProfile) {
                    userProfileDtoMap.put(dto.getUserId(), dto);
                    cache.getCache().put(UserProfileHasher.hashUserProfileByUserId(dto.getUserId()), dto.deepClone());
                }
            }

            if(teamDto.getUsers().isHasValue()) {
                List<UserProfileOnTeamDto> originAttachUserProfiles = teamDto.getUsers().getValue();
                List<String> attachUserIds = originAttachUserProfiles
                        .stream()
                        .map(UserProfileDto::getUserId)
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
                ListRelation<UserProfileOnTeamDto> usersRelation = retriever.getUsersRelationRetriever().getRelationRetriever().apply(teamDto);
                retriever.getUsersRelationRetriever().getChain().forEach(subRetriever -> {
                    if (subRetriever != null && usersRelation.isHasValue() && usersRelation.getValue() != null) {
                        // TODO: Implement this
                    }
                });
            }
        }

        // tasks
        if (retriever.getTasksRelationRetriever() != null) {
            int need = 0;
            for (TaskRetriever subRetriever : retriever.getTasksRelationRetriever().getChain()) {
                if (subRetriever != null && subRetriever.getAttachmentsRelationRetriever() != null) {
                    need |= NEED_TASK_ATTACHED_FILE_OBJECTS;
                }
            }

            // 取得が必要なIDの取得
            List<String> taskIds = retriever.getTasksRelationRetriever().getIdRetriever().apply(teamDto);
            List<String> needRetrieveTaskIds = new ArrayList<>();
            Map<String, TaskDto> taskDtoMap = new HashMap<>();
            List<TaskDto> tasks;

            switch (need) {
                case GET_TASK_ATTACHED_FILE_OBJECTS:
                    for (String taskId : taskIds) {
                        if (cache.getCache().containsKey(TaskHasher.hashTaskWithAttachments(taskId))) {
                            taskDtoMap.put(taskId, (TaskDto) cache.getCache().get(TaskHasher.hashTaskWithAttachments(taskId)));
                            break;
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
                            break;
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

            teamDto.setTasks(ListRelation.<TaskDto>builder()
                    .value(
                            taskIds.stream()
                                    .map(taskDtoMap::get)
                                    .toList()
                    )
                    .build()
            );
            ListRelation<TaskDto> tasksRelation = retriever.getTasksRelationRetriever().getRelationRetriever().apply(teamDto);
            retriever.getTasksRelationRetriever().getChain().forEach(subRetriever -> {
                if (subRetriever != null && tasksRelation.isHasValue() && tasksRelation.getValue() != null) {
                    // TODO: Implement this
                }
            });
        }
    }

    public void attach(String operatorId, RetrievedCacheContainer cache, TeamRetriever retriever, List<TeamDto> teamDto) {

        // Organization
        if (retriever.getOrganizationRelationRetriever() != null) {
            int need = 0;
            for (OrganizationRetriever subRetriever : retriever.getOrganizationRelationRetriever().getChain()) {
                if (subRetriever != null && subRetriever.getUsersRelationRetriever() != null) {
                    need |= NEED_ORGANIZATION_USERS;
                }
            }
            Map<String, OrganizationDto> organizationDtoMap = new HashMap<>();
            List<String> needRetrieveOrganizationIds = new ArrayList<>();
            List<OrganizationDto> organizations;
            switch (need) {
                case GET_ORGANIZATION_WITH_USERS:
                    for (TeamDto dto : teamDto) {
                        String organizationId = retriever.getOrganizationRelationRetriever().getIdRetriever().apply(dto);
                        if (cache.getCache().containsKey(OrganizationHasher.hashOrganizationWithUsers(organizationId))) {
                            organizationDtoMap.put(organizationId, (OrganizationDto) cache.getCache().get(OrganizationHasher.hashOrganizationWithUsers(organizationId)));
                        } else {
                            needRetrieveOrganizationIds.add(organizationId);
                        }
                    }
                    if (!needRetrieveOrganizationIds.isEmpty()) {
                        organizations = organizationQueryProxy.getPluralOrganizationsWithUsers(
                                operatorId,
                                needRetrieveOrganizationIds,
                                null,
                                null
                        ).getListData();
                        for (OrganizationDto dto : organizations) {
                            organizationDtoMap.put(dto.getOrganizationId(), dto);
                            cache.getCache().put(OrganizationHasher.hashOrganizationWithUsers(dto.getOrganizationId()), dto.deepClone());
                        }
                    }
                    break;
                default:
                    for (TeamDto dto : teamDto) {
                        String organizationId = retriever.getOrganizationRelationRetriever().getIdRetriever().apply(dto);
                        if (cache.getCache().containsKey(OrganizationHasher.hashOrganization(organizationId))) {
                            organizationDtoMap.put(organizationId, (OrganizationDto) cache.getCache().get(OrganizationHasher.hashOrganization(organizationId)));
                        } else {
                            needRetrieveOrganizationIds.add(organizationId);
                        }
                    }
                    if (!needRetrieveOrganizationIds.isEmpty()) {
                        organizations = organizationQueryProxy.getPluralOrganizations(
                                operatorId,
                                needRetrieveOrganizationIds,
                                null,
                                null
                        ).getListData();
                        for (OrganizationDto dto : organizations) {
                            organizationDtoMap.put(dto.getOrganizationId(), dto);
                            cache.getCache().put(OrganizationHasher.hashOrganization(dto.getOrganizationId()), dto.deepClone());
                        }
                    }
                    break;
            }

            for (TeamDto dto : teamDto) {
                String organizationId = retriever.getOrganizationRelationRetriever().getIdRetriever().apply(dto);
                OrganizationDto organizationDto = organizationDtoMap.get(organizationId);
                dto.setOrganization(Relation.<OrganizationDto>builder()
                        .hasValue(true)
                        .value(organizationDto)
                        .build()
                );
                Relation<OrganizationDto> organizationRelation = retriever.getOrganizationRelationRetriever().getRelationRetriever().apply(dto);
                retriever.getOrganizationRelationRetriever().getChain().forEach(subRetriever -> {
                    if (subRetriever != null && organizationRelation.isHasValue() && organizationRelation.getValue() != null) {
                        // TODO: Implement this
                    }
                });
            }
        }

        // Users
        if (retriever.getUsersRelationRetriever() != null) {
            List<String> userIds = new ArrayList<>();
            Map<String, UserProfileDto> userProfileDtoMap = new HashMap<>();
            for (TeamDto dto : teamDto) {
                List<String> ids = retriever.getUsersRelationRetriever().getIdRetriever().apply(dto);
                userIds.addAll(ids);
            }
            List<String> needRetrieveUserIds = new ArrayList<>();
            for (String userId : userIds) {
                if (cache.getCache().containsKey(UserProfileHasher.hashUserProfileByUserId(userId))) {
                    userProfileDtoMap.put(userId, (UserProfileDto) cache.getCache().get(UserProfileHasher.hashUserProfileByUserId(userId)));
                    break;
                } else {
                    needRetrieveUserIds.add(userId);
                }
            }

            if (!needRetrieveUserIds.isEmpty()) {
                List<UserProfileDto> userProfile = userProfileQueryProxy.getPluralUserProfilesByUserIds(
                        operatorId,
                        needRetrieveUserIds,
                        null,
                        null
                ).getListData();
                for (UserProfileDto dto : userProfile) {
                    userProfileDtoMap.put(dto.getUserId(), dto);
                    cache.getCache().put(UserProfileHasher.hashUserProfileByUserId(dto.getUserId()), dto.deepClone());
                }
            }

            for (TeamDto dto : teamDto) {
                List<UserProfileOnTeamDto> originAttachUserProfiles = dto.getUsers().getValue();
                List<String> attachUserIds = originAttachUserProfiles
                        .stream()
                        .map(UserProfileDto::getUserId)
                        .toList();
                List<UserProfileDto> attachUserProfiles = attachUserIds.stream()
                        .map(userProfileDtoMap::get)
                        .toList();
                dto.setUsers(ListRelation.<UserProfileOnTeamDto>builder()
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
                ListRelation<UserProfileOnTeamDto> usersRelation = retriever.getUsersRelationRetriever().getRelationRetriever().apply(dto);
                retriever.getUsersRelationRetriever().getChain().forEach(subRetriever -> {
                    if (subRetriever != null && usersRelation.isHasValue() && usersRelation.getValue() != null) {
                        // TODO: Implement this
                    }
                });
            }
        }

        // tasks
        if (retriever.getTasksRelationRetriever() != null) {
            int need = 0;
            for (TaskRetriever subRetriever : retriever.getTasksRelationRetriever().getChain()) {
                if (subRetriever != null && subRetriever.getAttachmentsRelationRetriever() != null) {
                    need |= NEED_TASK_ATTACHED_FILE_OBJECTS;
                }
            }

            List<String> taskIds = new ArrayList<>();
            Map<String, TaskDto> taskDtoMap = new HashMap<>();
            for (TeamDto dto : teamDto) {
                List<String> ids = retriever.getTasksRelationRetriever().getIdRetriever().apply(dto);
                taskIds.addAll(ids);
            }
            List<String> needRetrieveTaskIds = new ArrayList<>();
            List<TaskDto> tasks;

            switch (need) {
                case GET_TASK_ATTACHED_FILE_OBJECTS:
                    for (String taskId : taskIds) {
                        if (cache.getCache().containsKey(TaskHasher.hashTaskWithAttachments(taskId))) {
                            taskDtoMap.put(taskId, (TaskDto) cache.getCache().get(TaskHasher.hashTaskWithAttachments(taskId)));
                            break;
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
                            break;
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

            for (TeamDto dto : teamDto) {
                dto.setTasks(ListRelation.<TaskDto>builder()
                        .value(
                                taskIds.stream()
                                        .map(taskDtoMap::get)
                                        .toList()
                        )
                        .build()
                );
                ListRelation<TaskDto> tasksRelation = retriever.getTasksRelationRetriever().getRelationRetriever().apply(dto);
                retriever.getTasksRelationRetriever().getChain().forEach(subRetriever -> {
                    if (subRetriever != null && tasksRelation.isHasValue() && tasksRelation.getValue() != null) {
                        // TODO: Implement this
                    }
                });
            }
        }
    }
}
