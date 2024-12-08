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

import java.util.*;

@Component
@RequiredArgsConstructor
public class AttachRelationFileObject {

    private final UserProfileQueryProxy userProfileQueryProxy;
    private final TeamQueryProxy teamQueryProxy;
    private final UserPreferenceQueryProxy userPreferenceQueryProxy;
    private final OrganizationQueryProxy organizationQueryProxy;
    private final TaskQueryProxy taskQueryProxy;
    private final FileObjectQueryProxy fileObjectQueryProxy;

    public <T extends FileObjectDto> T attach(String operatorId, RetrievedCacheContainer cache, FileObjectRetriever retriever, T fileObjectDto) {

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
                    retriever.getAttachedTasksRelationRetriever().getChain()
            );
        }

        return fileObjectDto;
    }

    public <T extends FileObjectDto> List<T> attach(String operatorId, RetrievedCacheContainer cache, FileObjectRetriever retriever, List<T> fileObjectDto) {
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
                            .distinct()
                            .toList(),
                    retriever.getAttachedTasksRelationRetriever().getChain()
            );

            this.attachRelationToTasks(
                    operatorId,
                    cache,
                    fileObjectDto,
                    taskDtoMap,
                    retriever.getAttachedTasksRelationRetriever().getChain()
            );
        }

        return fileObjectDto;
    }

    private <T extends FileObjectDto, U extends TaskDto> void internalAttachRelationToTasks(
            T fileObjectDto,
            Map<String, U> taskDtoMap
    ) {
        if(fileObjectDto.getAttachedTasks().isHasValue()) {
            List<TaskOnFileObjectDto> originAttachTasks = fileObjectDto.getAttachedTasks().getValue();
            List<String> attachAttachedTaskIds = originAttachTasks
                    .stream()
                    .map(TaskDto::getTaskId)
                    .toList();
            List<U> attachAttachedTasks = attachAttachedTaskIds.stream()
                    .map(taskDtoMap::get)
                    .toList();
            fileObjectDto.setAttachedTasks(ListRelation.<TaskOnFileObjectDto>builder()
                    .hasValue(true)
                    .value(
                            attachAttachedTasks.stream()
                                    .map(task -> new TaskOnFileObjectDto(task.merge(originAttachTasks.stream()
                                            .filter(origin -> origin.getTaskId().equals(task.getTaskId()))
                                            .findFirst().orElse(null))))
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
            List<TaskRetriever> retrievers
    ) {
        this.internalAttachRelationToTasks(fileObjectDto, taskDtoMap);
        if(fileObjectDto.getAttachedTasks().isHasValue()) {
            retrievers.forEach(retriever -> {
                if (retriever != null && fileObjectDto.getAttachedTasks().isHasValue() && fileObjectDto.getAttachedTasks().getValue() != null) {
                    AttachRelationTask attachRelationTask = new AttachRelationTask(
                            userProfileQueryProxy,
                            teamQueryProxy,
                            userPreferenceQueryProxy,
                            organizationQueryProxy,
                            taskQueryProxy,
                            fileObjectQueryProxy
                    );
                    var attached = attachRelationTask.attach(operatorId, cache, retriever, fileObjectDto.getAttachedTasks().getValue());

                    Map<String, TaskOnFileObjectDto> attachedMap = new HashMap<>();

                    for (TaskOnFileObjectDto taskOnFileObjectDto : attached) {
                        attachedMap.put(taskOnFileObjectDto.getTaskId(), taskOnFileObjectDto);
                    }

                    this.internalAttachRelationToTasks(fileObjectDto, attachedMap);
                }
            });
        }
    }

    protected <T extends FileObjectDto> void attachRelationToTasks(
            String operatorId,
            RetrievedCacheContainer cache,
            List<T> fileObjectDto,
            Map<String, TaskDto> taskDtoMap,
            List<TaskRetriever> retrievers
    ) {
        for (FileObjectDto dto : fileObjectDto) {
            this.internalAttachRelationToTasks(dto, taskDtoMap);
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
                List<TaskOnFileObjectDto> tasks = fileObjectDto.stream()
                        .flatMap(fileObject -> fileObject.getAttachedTasks().getValue().stream())
                        .filter(task -> seenIds.add(task.getTaskId()))
                        .toList();

                var attached = attachRelationTask.attach(
                        operatorId,
                        cache,
                        retriever,
                        tasks
                );

                Map<String, TaskOnFileObjectDto> attachedMap = new HashMap<>();

                for (TaskOnFileObjectDto taskOnFileObjectDto : attached) {
                    attachedMap.put(taskOnFileObjectDto.getTaskId(), taskOnFileObjectDto);
                }

                for (T dto : fileObjectDto) {
                    this.internalAttachRelationToTasks(dto, attachedMap);
                }
            }
        });
    }
}
