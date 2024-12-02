package org.cresplanex.api.state.webgateway.composition.helper;

import org.cresplanex.api.state.webgateway.dto.domain.ListRelation;
import org.cresplanex.api.state.webgateway.dto.domain.plan.TaskDto;
import org.cresplanex.api.state.webgateway.dto.domain.plan.TaskOnFileObjectDto;
import org.cresplanex.api.state.webgateway.dto.domain.storage.FileObjectDto;
import org.cresplanex.api.state.webgateway.dto.domain.team.TeamDto;
import org.cresplanex.api.state.webgateway.dto.domain.userprofile.UserProfileDto;
import org.cresplanex.api.state.webgateway.hasher.TaskHasher;
import org.cresplanex.api.state.webgateway.proxy.query.TaskQueryProxy;
import org.cresplanex.api.state.webgateway.retriever.RetrievedCacheContainer;
import org.cresplanex.api.state.webgateway.retriever.domain.TaskRetriever;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TaskCompositionHelper {

    public static final int NEED_TASK_ATTACHED_FILE_OBJECTS = 1 << 0;

    public static final int GET_TASK_ATTACHED_FILE_OBJECTS = NEED_TASK_ATTACHED_FILE_OBJECTS;

    public static int calculateNeedQuery(List<TaskRetriever> retrievers) {
        int needQuery = 0;
        for (TaskRetriever retriever : retrievers) {
            if (retriever != null && retriever.getAttachmentsRelationRetriever() != null) {
                needQuery |= NEED_TASK_ATTACHED_FILE_OBJECTS;
            }
        }
        return needQuery;
    }

    public static Map<String, TaskDto> createTaskDtoMap(
            TaskQueryProxy taskQueryProxy,
            RetrievedCacheContainer cache,
            String operatorId,
            List<String> taskIds,
            List<TaskRetriever> retrievers
    ) {
        int need = calculateNeedQuery(retrievers);
        List<String> needRetrieveAttachedTaskIds = new ArrayList<>();
        Map<String, TaskDto> taskDtoMap = new HashMap<>();
        List<TaskDto> task;

        switch (need) {
            case GET_TASK_ATTACHED_FILE_OBJECTS:
                for (String taskId : taskIds) {
                    if (cache.getCache().containsKey(TaskHasher.hashTaskWithAttachments(taskId))) {
                        taskDtoMap.put(taskId, ((TaskDto) cache.getCache().get(TaskHasher.hashTaskWithAttachments(taskId))).deepClone());
                        break;
                    } else {
                        needRetrieveAttachedTaskIds.add(taskId);
                    }
                }
                if (!needRetrieveAttachedTaskIds.isEmpty()) {
                    task = taskQueryProxy.getPluralTasksWithAttachments(
                            operatorId,
                            needRetrieveAttachedTaskIds,
                            "none",
                            "asc"
                    ).getListData();

                    for (TaskDto dto : task) {
                        taskDtoMap.put(dto.getTaskId(), dto);
                        cache.getCache().put(TaskHasher.hashTaskWithAttachments(dto.getTaskId()), dto.deepClone());
                    }
                }
                break;
            default:
                for (String taskId : taskIds) {
                    if (cache.getCache().containsKey(TaskHasher.hashTask(taskId))) {
                        taskDtoMap.put(taskId, ((TaskDto) cache.getCache().get(TaskHasher.hashTask(taskId))).deepClone());
                        break;
                    } else {
                        needRetrieveAttachedTaskIds.add(taskId);
                    }
                }

                if (!needRetrieveAttachedTaskIds.isEmpty()) {
                    task = taskQueryProxy.getPluralTasks(
                            operatorId,
                            needRetrieveAttachedTaskIds,
                            "none",
                            "asc"
                    ).getListData();
                    for (TaskDto dto : task) {
                        taskDtoMap.put(dto.getTaskId(), dto);
                        cache.getCache().put(TaskHasher.hashTask(dto.getTaskId()), dto.deepClone());
                    }
                }
                break;
        }

        return taskDtoMap;
    }

    public static  <T extends FileObjectDto> void preAttachToFileObject(
            TaskQueryProxy taskQueryProxy,
            RetrievedCacheContainer cache,
            String operatorId,
            List<T> fileObjectDtos
    ) {
        List<TaskDto> relationTasks = taskQueryProxy.getTasksWithAttachments(
                operatorId,
                0,
                0,
                "",
                "none",
                "none",
                "asc",
                false,
                false,
                List.of(),
                false,
                List.of(),
                false,
                List.of(),
                "",
                "",
                "",
                "",
                true,
                fileObjectDtos.stream().map(FileObjectDto::getFileObjectId).toList(),
                "any"
        ).getListData();

        Map<String, FileObjectDto> fileObjectDtoMap = fileObjectDtos.stream()
                .collect(Collectors.toMap(FileObjectDto::getFileObjectId, Function.identity()));
        Map<String, List<TaskOnFileObjectDto>> taskOnFileObjectDtoMap = new HashMap<>();

        for (TaskDto dto : relationTasks) {
            cache.getCache().put(TaskHasher.hashTaskWithAttachments(dto.getTaskId()), dto.deepClone());
            if (dto.getAttachments().isHasValue()) {
                dto.getAttachments().getValue().forEach(fileObjectOnTask -> {
                    FileObjectDto targetFileObjectDto = fileObjectDtoMap.get(fileObjectOnTask.getFileObjectId());
                    if (targetFileObjectDto != null) {
                        taskOnFileObjectDtoMap.computeIfAbsent(targetFileObjectDto.getFileObjectId(), k -> new ArrayList<>()).add(new TaskOnFileObjectDto(dto));
                    }
                });
            }
        }

        for (Map.Entry<String, List<TaskOnFileObjectDto>> entry : taskOnFileObjectDtoMap.entrySet()) {
            FileObjectDto targetFileObjectDto = fileObjectDtoMap.get(entry.getKey());
            if (targetFileObjectDto == null) {
                continue;
            }
            targetFileObjectDto.setAttachedTasks(ListRelation.<TaskOnFileObjectDto>builder()
                    .hasValue(true)
                    .value(entry.getValue())
                    .build()
            );
        }
    }

    public static <T extends UserProfileDto> void preAttachToChargeUser(
            TaskQueryProxy taskQueryProxy,
            RetrievedCacheContainer cache,
            String operatorId,
            List<T> userProfileDtos
    ) {
        List<TaskDto> relationTasks = taskQueryProxy.getTasks(
                operatorId,
                0,
                0,
                "",
                "none",
                "none",
                "asc",
                false,
                false,
                List.of(),
                true,
                List.of(),
                true,
                userProfileDtos.stream().map(UserProfileDto::getUserId).toList(),
                "",
                "",
                "",
                "",
                false,
                List.of(),
                "none"
        ).getListData();

        Map<String, UserProfileDto> userProfileDtoMap = userProfileDtos.stream()
                .collect(Collectors.toMap(UserProfileDto::getUserId, Function.identity()));
        Map<String, List<TaskDto>> taskDtoMap = new HashMap<>();

        for (TaskDto dto : relationTasks) {
            cache.getCache().put(TaskHasher.hashTask(dto.getTaskId()), dto.deepClone());
            String chargeUserId = dto.getChargeUserId();
            if (chargeUserId != null) {
                taskDtoMap.computeIfAbsent(chargeUserId, k -> new ArrayList<>()).add(dto);
            }
        }

        for (Map.Entry<String, List<TaskDto>> entry : taskDtoMap.entrySet()) {
            UserProfileDto targetUserProfileDto = userProfileDtoMap.get(entry.getKey());
            if (targetUserProfileDto == null) {
                continue;
            }
            targetUserProfileDto.setChargeTasks(ListRelation.<TaskDto>builder()
                    .hasValue(true)
                    .value(entry.getValue())
                    .build()
            );
        }
    }

    public static  <T extends TeamDto> void preAttachToTeam(
            TaskQueryProxy taskQueryProxy,
            RetrievedCacheContainer cache,
            String operatorId,
            List<T> teamDtos
    ) {
        List<TaskDto> relationTasks = taskQueryProxy.getTasks(
                operatorId,
                0,
                0,
                "",
                "none",
                "none",
                "asc",
                false,
                true,
                teamDtos.stream().map(TeamDto::getTeamId).toList(),
                true,
                List.of(),
                false,
                List.of(),
                "",
                "",
                "",
                "",
                false,
                List.of(),
                "none"
        ).getListData();

        Map<String, TeamDto> teamDtoMap = teamDtos.stream()
                .collect(Collectors.toMap(TeamDto::getTeamId, Function.identity()));
        Map<String, List<TaskDto>> taskDtoMap = new HashMap<>();

        for (TaskDto dto : relationTasks) {
            cache.getCache().put(TaskHasher.hashTask(dto.getTaskId()), dto.deepClone());
            String teamId = dto.getTeamId();
            if (teamId != null) {
                taskDtoMap.computeIfAbsent(teamId, k -> new ArrayList<>()).add(dto);
            }
        }

        for (Map.Entry<String, List<TaskDto>> entry : taskDtoMap.entrySet()) {
            TeamDto targetTeamDto = teamDtoMap.get(entry.getKey());
            if (targetTeamDto == null) {
                continue;
            }
            targetTeamDto.setTasks(ListRelation.<TaskDto>builder()
                    .hasValue(true)
                    .value(entry.getValue())
                    .build()
            );
        }
    }
}
