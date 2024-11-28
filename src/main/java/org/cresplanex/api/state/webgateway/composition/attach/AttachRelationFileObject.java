package org.cresplanex.api.state.webgateway.composition.attach;

import lombok.RequiredArgsConstructor;
import org.cresplanex.api.state.webgateway.composition.helper.TaskCompositionHelper;
import org.cresplanex.api.state.webgateway.dto.domain.ListRelation;
import org.cresplanex.api.state.webgateway.dto.domain.plan.TaskDto;
import org.cresplanex.api.state.webgateway.dto.domain.plan.TaskOnFileObjectDto;
import org.cresplanex.api.state.webgateway.dto.domain.storage.FileObjectDto;
import org.cresplanex.api.state.webgateway.proxy.query.*;
import org.cresplanex.api.state.webgateway.retriever.RetrievedCacheContainer;
import org.cresplanex.api.state.webgateway.retriever.domain.FileObjectRetriever;
import org.cresplanex.api.state.webgateway.retriever.domain.TaskRetriever;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AttachRelationFileObject {

    private final UserProfileQueryProxy userProfileQueryProxy;
    private final TeamQueryProxy teamQueryProxy;
    private final UserPreferenceQueryProxy userPreferenceQueryProxy;
    private final OrganizationQueryProxy organizationQueryProxy;
    private final TaskQueryProxy taskQueryProxy;
    private final FileObjectQueryProxy fileObjectQueryProxy;

    public <T extends FileObjectDto> void attach(String operatorId, RetrievedCacheContainer cache, FileObjectRetriever retriever, T fileObjectDto) {

        // attachedTasks
        if (retriever.getAttachedTasksRelationRetriever() != null) {
            TaskCompositionHelper.preAttachToFileObject(
                    taskQueryProxy,
                    cache,
                    operatorId,
                    List.of(fileObjectDto)
            );
            Map<String, TaskDto> taskDtoMap = TaskCompositionHelper.createTaskDtoMap(
                    taskQueryProxy,
                    cache,
                    operatorId,
                    retriever.getAttachedTasksRelationRetriever().getIdRetriever().apply(fileObjectDto),
                    retriever.getAttachedTasksRelationRetriever().getChain()
            );
            this.attachRelationToTasks(
                    operatorId,
                    cache,
                    fileObjectDto,
                    taskDtoMap,
                    retriever.getAttachedTasksRelationRetriever().getRelationRetriever().apply(fileObjectDto),
                    retriever.getAttachedTasksRelationRetriever().getChain()
            );
        }
    }

    public <T extends FileObjectDto> void attach(String operatorId, RetrievedCacheContainer cache, FileObjectRetriever retriever, List<T> fileObjectDto) {
        // AttachedTasks
        if (retriever.getAttachedTasksRelationRetriever() != null) {
            TaskCompositionHelper.preAttachToFileObject(
                    taskQueryProxy,
                    cache,
                    operatorId,
                    fileObjectDto
            );

            Map<String, TaskDto> taskDtoMap = TaskCompositionHelper.createTaskDtoMap(
                    taskQueryProxy,
                    cache,
                    operatorId,
                    fileObjectDto.stream()
                            .map(retriever.getAttachedTasksRelationRetriever().getIdRetriever())
                            .flatMap(List::stream)
                            .toList(),
                    retriever.getAttachedTasksRelationRetriever().getChain()
            );

            this.attachRelationToTasks(
                    operatorId,
                    cache,
                    fileObjectDto,
                    taskDtoMap,
                    fileObjectDto.stream()
                            .map(retriever.getAttachedTasksRelationRetriever().getRelationRetriever())
                            .toList(),
                    retriever.getAttachedTasksRelationRetriever().getChain()
            );
        }
    }

    private <T extends FileObjectDto> void internalAttachRelationToTasks(
            T fileObjectDto,
            Map<String, TaskDto> taskDtoMap
    ) {
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
        }
    }

    protected <T extends FileObjectDto> void attachRelationToTasks(
            String operatorId,
            RetrievedCacheContainer cache,
            T fileObjectDto,
            Map<String, TaskDto> taskDtoMap,
            ListRelation<TaskOnFileObjectDto> tasksRelation,
            List<TaskRetriever> retrievers
    ) {
        this.internalAttachRelationToTasks(fileObjectDto, taskDtoMap);
        if(fileObjectDto.getAttachedTasks().isHasValue()) {
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

    protected <T extends FileObjectDto> void attachRelationToTasks(
            String operatorId,
            RetrievedCacheContainer cache,
            List<T> fileObjectDto,
            Map<String, TaskDto> taskDtoMap,
            List<ListRelation<TaskOnFileObjectDto>> tasksRelation,
            List<TaskRetriever> retrievers
    ) {
        for (FileObjectDto dto : fileObjectDto) {
            this.internalAttachRelationToTasks(dto, taskDtoMap);
        }
        retrievers.forEach(retriever -> {
            List<List<TaskOnFileObjectDto>> tasksRelationList = tasksRelation.stream()
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