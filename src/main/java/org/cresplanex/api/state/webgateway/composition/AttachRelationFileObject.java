package org.cresplanex.api.state.webgateway.composition;

import lombok.RequiredArgsConstructor;
import org.cresplanex.api.state.webgateway.dto.domain.ListRelation;
import org.cresplanex.api.state.webgateway.dto.domain.plan.TaskDto;
import org.cresplanex.api.state.webgateway.dto.domain.plan.TaskOnFileObjectDto;
import org.cresplanex.api.state.webgateway.dto.domain.storage.FileObjectDto;
import org.cresplanex.api.state.webgateway.hasher.TaskHasher;
import org.cresplanex.api.state.webgateway.proxy.query.TaskQueryProxy;
import org.cresplanex.api.state.webgateway.retriever.RetrievedCacheContainer;
import org.cresplanex.api.state.webgateway.retriever.domain.FileObjectRetriever;
import org.cresplanex.api.state.webgateway.retriever.domain.TaskRetriever;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class AttachRelationFileObject {

    private final TaskQueryProxy taskQueryProxy;

    public static final int NEED_TASK_ATTACHED_FILE_OBJECTS = 1 << 0;

    public static final int GET_TASK_ATTACHED_FILE_OBJECTS = NEED_TASK_ATTACHED_FILE_OBJECTS;

    public void attach(String operatorId, RetrievedCacheContainer cache, FileObjectRetriever retriever, FileObjectDto fileObjectDto) {

        // attachedTasks
        if (retriever.getAttachedTasksRelationRetriever() != null) {
            int need = 0;
            for (TaskRetriever subRetriever : retriever.getAttachedTasksRelationRetriever().getChain()) {
                if (subRetriever != null && subRetriever.getAttachmentsRelationRetriever() != null) {
                    need |= NEED_TASK_ATTACHED_FILE_OBJECTS;
                }
            }

            // 取得が必要なIDの取得
            List<String> taskIds = retriever.getAttachedTasksRelationRetriever().getIdRetriever().apply(fileObjectDto);
            List<String> needRetrieveAttachedTaskIds = new ArrayList<>();
            Map<String, TaskDto> taskDtoMap = new HashMap<>();
            List<TaskDto> task = new ArrayList<>();

            switch (need) {
                case GET_TASK_ATTACHED_FILE_OBJECTS:
                    for (String taskId : taskIds) {
                        if (cache.getCache().containsKey(TaskHasher.hashTaskWithAttachments(taskId))) {
                            taskDtoMap.put(taskId, (TaskDto) cache.getCache().get(TaskHasher.hashTaskWithAttachments(taskId)));
                            break;
                        } else {
                            needRetrieveAttachedTaskIds.add(taskId);
                        }
                    }
                    if (!needRetrieveAttachedTaskIds.isEmpty()) {
                        task = taskQueryProxy.getPluralTasksWithAttachments(
                                operatorId,
                                needRetrieveAttachedTaskIds,
                                null,
                                null
                        ).getListData();

                        for (TaskDto dto : task) {
                            taskDtoMap.put(dto.getTaskId(), dto);
                            cache.getCache().put(TaskHasher.hashTaskWithAttachments(dto.getTaskId()), dto.deepClone());
                        }
                    }

                    if(fileObjectDto.getAttachedTasks().isHasValue()) {
                        List<TaskOnFileObjectDto> originAttachTasks = fileObjectDto.getAttachedTasks().getValue();
                        List<String> attachAttachedTaskIds = originAttachTasks
                                .stream()
                                .map(TaskDto::getTaskId)
                                .toList();
                        List<TaskDto> attachAttachedTasks = attachAttachedTaskIds.stream()
                                .map(taskDtoMap::get)
                                .toList();
                        fileObjectDto.setAttachedTasks(ListRelation.<TaskOnFileObjectDto>builder()
                                .value(
                                        attachAttachedTasks.stream()
                                                .map(newTask -> new TaskOnFileObjectDto(newTask, originAttachTasks.stream()
                                                        .filter(origin -> origin.getTaskId().equals(newTask.getTaskId()))
                                                        .findFirst()
                                                        .orElse(null)))
                                                .toList()
                                )
                                .build()
                        );
                        ListRelation<TaskOnFileObjectDto> tasksRelation = retriever.getAttachedTasksRelationRetriever().getRelationRetriever().apply(fileObjectDto);
                        retriever.getAttachedTasksRelationRetriever().getChain().forEach(subRetriever -> {
                            if (subRetriever != null && tasksRelation.isHasValue() && tasksRelation.getValue() != null) {
                                // TODO: Implement this
                            }
                        });
                    }
                    break;
                default:
                    for (String taskId : taskIds) {
                        if (cache.getCache().containsKey(TaskHasher.hashTask(taskId))) {
                            taskDtoMap.put(taskId, (TaskDto) cache.getCache().get(TaskHasher.hashTask(taskId)));
                            break;
                        } else {
                            needRetrieveAttachedTaskIds.add(taskId);
                        }
                    }

                    if (!needRetrieveAttachedTaskIds.isEmpty()) {
                        task = taskQueryProxy.getPluralTasks(
                                operatorId,
                                needRetrieveAttachedTaskIds,
                                null,
                                null
                        ).getListData();
                        for (TaskDto dto : task) {
                            taskDtoMap.put(dto.getTaskId(), dto);
                            cache.getCache().put(TaskHasher.hashTask(dto.getTaskId()), dto.deepClone());
                        }
                    }

                    if(fileObjectDto.getAttachedTasks().isHasValue()) {
                        List<TaskOnFileObjectDto> originAttachTasks = fileObjectDto.getAttachedTasks().getValue();
                        List<String> attachAttachedTaskIds = originAttachTasks
                                .stream()
                                .map(TaskDto::getTaskId)
                                .toList();
                        List<TaskDto> attachAttachedTasks = attachAttachedTaskIds.stream()
                                .map(taskDtoMap::get)
                                .toList();
                        fileObjectDto.setAttachedTasks(ListRelation.<TaskOnFileObjectDto>builder()
                                .value(
                                        attachAttachedTasks.stream()
                                                .map(newTask -> new TaskOnFileObjectDto(newTask, originAttachTasks.stream()
                                                        .filter(origin -> origin.getTaskId().equals(newTask.getTaskId()))
                                                        .findFirst()
                                                        .orElse(null)))
                                                .toList()
                                )
                                .build()
                        );
                        ListRelation<TaskOnFileObjectDto> tasksRelation = retriever.getAttachedTasksRelationRetriever().getRelationRetriever().apply(fileObjectDto);
                        retriever.getAttachedTasksRelationRetriever().getChain().forEach(subRetriever -> {
                            if (subRetriever != null && tasksRelation.isHasValue() && tasksRelation.getValue() != null) {
                                // TODO: Implement this
                            }
                        });
                    }
                    break;
            }
        }
    }

    public void attach(String operatorId, RetrievedCacheContainer cache, FileObjectRetriever retriever, List<FileObjectDto> fileObjectDto) {
        // AttachedTasks
        if (retriever.getAttachedTasksRelationRetriever() != null) {
            int need = 0;
            for (TaskRetriever subRetriever : retriever.getAttachedTasksRelationRetriever().getChain()) {
                if (subRetriever != null && subRetriever.getAttachmentsRelationRetriever() != null) {
                    need |= NEED_TASK_ATTACHED_FILE_OBJECTS;
                }
            }
            List<String> taskIds = new ArrayList<>();
            Map<String, TaskDto> taskDtoMap = new HashMap<>();
            List<TaskDto> task = new ArrayList<>();
            List<String> needRetrieveAttachedTaskIds = new ArrayList<>();

            switch (need) {
                case GET_TASK_ATTACHED_FILE_OBJECTS:
                    for (FileObjectDto dto : fileObjectDto) {
                        List<String> ids = retriever.getAttachedTasksRelationRetriever().getIdRetriever().apply(dto);
                        taskIds.addAll(ids);
                    }
                    for (String taskId : taskIds) {
                        if (cache.getCache().containsKey(TaskHasher.hashTaskWithAttachments(taskId))) {
                            taskDtoMap.put(taskId, (TaskDto) cache.getCache().get(TaskHasher.hashTaskWithAttachments(taskId)));
                            break;
                        } else {
                            needRetrieveAttachedTaskIds.add(taskId);
                        }
                    }

                    if (!needRetrieveAttachedTaskIds.isEmpty()) {
                        task = taskQueryProxy.getPluralTasksWithAttachments(
                                operatorId,
                                needRetrieveAttachedTaskIds,
                                null,
                                null
                        ).getListData();
                        for (TaskDto dto : task) {
                            taskDtoMap.put(dto.getTaskId(), dto);
                            cache.getCache().put(TaskHasher.hashTaskWithAttachments(dto.getTaskId()), dto.deepClone());
                        }
                    }

                    for (FileObjectDto dto : fileObjectDto) {
                        if (dto.getAttachedTasks().isHasValue()) {
                            List<TaskOnFileObjectDto> originAttachTasks = dto.getAttachedTasks().getValue();
                            List<String> attachAttachedTaskIds = originAttachTasks
                                    .stream()
                                    .map(TaskDto::getTaskId)
                                    .toList();
                            List<TaskDto> attachAttachedTasks = attachAttachedTaskIds.stream()
                                    .map(taskDtoMap::get)
                                    .toList();
                            dto.setAttachedTasks(ListRelation.<TaskOnFileObjectDto>builder()
                                    .value(
                                            attachAttachedTasks.stream()
                                                    .map(newTask -> new TaskOnFileObjectDto(newTask, originAttachTasks.stream()
                                                            .filter(origin -> origin.getTaskId().equals(newTask.getTaskId()))
                                                            .findFirst()
                                                            .orElse(null)))
                                                    .toList()
                                    )
                                    .build()
                            );
                            ListRelation<TaskOnFileObjectDto> tasksRelation = retriever.getAttachedTasksRelationRetriever().getRelationRetriever().apply(dto);
                            retriever.getAttachedTasksRelationRetriever().getChain().forEach(subRetriever -> {
                                if (subRetriever != null && tasksRelation.isHasValue() && tasksRelation.getValue() != null) {
                                    // TODO: Implement this
                                }
                            });
                        }
                    }
                    break;
                default:
                    for (FileObjectDto dto : fileObjectDto) {
                        List<String> ids = retriever.getAttachedTasksRelationRetriever().getIdRetriever().apply(dto);
                        taskIds.addAll(ids);
                    }
                    for (String taskId : taskIds) {
                        if (cache.getCache().containsKey(TaskHasher.hashTask(taskId))) {
                            taskDtoMap.put(taskId, (TaskDto) cache.getCache().get(TaskHasher.hashTask(taskId)));
                            break;
                        } else {
                            needRetrieveAttachedTaskIds.add(taskId);
                        }
                    }
                    task = taskQueryProxy.getPluralTasks(
                            operatorId,
                            needRetrieveAttachedTaskIds,
                            null,
                            null
                    ).getListData();
                    for (TaskDto dto : task) {
                        taskDtoMap.put(dto.getTaskId(), dto);
                        cache.getCache().put(TaskHasher.hashTask(dto.getTaskId()), dto.deepClone());
                    }
                    for (FileObjectDto dto : fileObjectDto) {
                        if (dto.getAttachedTasks().isHasValue()) {
                            List<TaskOnFileObjectDto> originAttachTasks = dto.getAttachedTasks().getValue();
                            List<String> attachAttachedTaskIds = originAttachTasks
                                    .stream()
                                    .map(TaskDto::getTaskId)
                                    .toList();
                            List<TaskDto> attachAttachedTasks = attachAttachedTaskIds.stream()
                                    .map(taskDtoMap::get)
                                    .toList();
                            dto.setAttachedTasks(ListRelation.<TaskOnFileObjectDto>builder()
                                    .value(
                                            attachAttachedTasks.stream()
                                                    .map(newTask -> new TaskOnFileObjectDto(newTask, originAttachTasks.stream()
                                                            .filter(origin -> origin.getTaskId().equals(newTask.getTaskId()))
                                                            .findFirst()
                                                            .orElse(null)))
                                                    .toList()
                                    )
                                    .build()
                            );
                            ListRelation<TaskOnFileObjectDto> tasksRelation = retriever.getAttachedTasksRelationRetriever().getRelationRetriever().apply(dto);
                            retriever.getAttachedTasksRelationRetriever().getChain().forEach(subRetriever -> {
                                if (subRetriever != null && tasksRelation.isHasValue() && tasksRelation.getValue() != null) {
                                    // TODO: Implement this
                                }
                            });
                        }
                    }
                    break;
            }
        }
    }
}
