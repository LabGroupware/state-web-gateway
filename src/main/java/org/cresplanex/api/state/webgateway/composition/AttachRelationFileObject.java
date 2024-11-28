package org.cresplanex.api.state.webgateway.composition;

import lombok.RequiredArgsConstructor;
import org.cresplanex.api.state.webgateway.dto.domain.ListRelation;
import org.cresplanex.api.state.webgateway.dto.domain.plan.TaskDto;
import org.cresplanex.api.state.webgateway.dto.domain.plan.TaskOnFileObjectDto;
import org.cresplanex.api.state.webgateway.dto.domain.storage.FileObjectDto;
import org.cresplanex.api.state.webgateway.proxy.query.TaskQueryProxy;
import org.cresplanex.api.state.webgateway.retriever.RetrievedCacheContainer;
import org.cresplanex.api.state.webgateway.retriever.domain.FileObjectRetriever;
import org.cresplanex.api.state.webgateway.retriever.domain.TaskRetriever;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class AttachRelationFileObject {

    private final TaskQueryProxy taskQueryProxy;

    public void attach(String operatorId, RetrievedCacheContainer cache, FileObjectRetriever retriever, FileObjectDto fileObjectDto) {

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

    public void attach(String operatorId, RetrievedCacheContainer cache, FileObjectRetriever retriever, List<FileObjectDto> fileObjectDto) {
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

            for (FileObjectDto dto : fileObjectDto) {
                this.attachRelationToTasks(
                        operatorId,
                        cache,
                        dto,
                        taskDtoMap,
                        retriever.getAttachedTasksRelationRetriever().getRelationRetriever().apply(dto),
                        retriever.getAttachedTasksRelationRetriever().getChain()
                );
            }
        }
    }

//    public void attachList(String operatorId, RetrievedCacheContainer cache, FileObjectRetriever retriever, List<List<FileObjectDto>> fileObjectDto) {
//        // AttachedTasks
//        if (retriever.getAttachedTasksRelationRetriever() != null) {
//            TaskCompositionHelper.preAttachToFileObject(
//                    taskQueryProxy,
//                    cache,
//                    operatorId,
//                    fileObjectDto.stream().flatMap(List::stream).toList()
//            );
//
//            Map<String, TaskDto> taskDtoMap = TaskCompositionHelper.createTaskDtoMap(
//                    taskQueryProxy,
//                    cache,
//                    operatorId,
//                    fileObjectDto.stream()
//                            .flatMap(List::stream)
//                            .map(retriever.getAttachedTasksRelationRetriever().getIdRetriever())
//                            .flatMap(List::stream)
//                            .toList(),
//                    retriever.getAttachedTasksRelationRetriever().getChain()
//            );
//
//            for (FileObjectDto dto : fileObjectDto.stream().flatMap(List::stream).toList()) {
//                this.attachRelationToTasks(
//                        operatorId,
//                        cache,
//                        dto,
//                        taskDtoMap,
//                        retriever.getAttachedTasksRelationRetriever().getRelationRetriever().apply(dto),
//                        retriever.getAttachedTasksRelationRetriever().getChain()
//                );
//            }
//        }
//    }

    private void internalAttachRelationToTasks(
            FileObjectDto fileObjectDto,
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

    protected void attachRelationToTasks(
            String operatorId,
            RetrievedCacheContainer cache,
            FileObjectDto fileObjectDto,
            Map<String, TaskDto> taskDtoMap,
            ListRelation<TaskOnFileObjectDto> tasksRelation,
            List<TaskRetriever> retrievers
    ) {
        this.internalAttachRelationToTasks(fileObjectDto, taskDtoMap);
        if(fileObjectDto.getAttachedTasks().isHasValue()) {
            retrievers.forEach(retriever -> {
                if (retriever != null && tasksRelation.isHasValue() && tasksRelation.getValue() != null) {
                    // TODO: Implement this
//                    var a = new AttachRelationFileObject(taskQueryProxy);
//                    a.attach(operatorId, cache, retriever, tasksRelation.getValue());
                }
            });
        }
    }

    protected void attachRelationToTasks(
            String operatorId,
            RetrievedCacheContainer cache,
            List<FileObjectDto> fileObjectDto,
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
//            var a = new AttachRelationFileObject(taskQueryProxy);
//            a.attach(operatorId, cache, retriever, tasksRelationList.stream().flatMap(List::stream).toList());
//            if (retriever != null && tasksRelation.isHasValue() && tasksRelation.getValue() != null) {
//                // TODO: Implement this
//            }
        });
    }
}
