package org.cresplanex.api.state.webgateway.composition;

import lombok.RequiredArgsConstructor;
import org.cresplanex.api.state.webgateway.composition.attach.AttachRelationTask;
import org.cresplanex.api.state.webgateway.composition.helper.TaskCompositionHelper;
import org.cresplanex.api.state.webgateway.dto.ListResponseDto;
import org.cresplanex.api.state.webgateway.dto.domain.plan.TaskDto;
import org.cresplanex.api.state.webgateway.hasher.TaskHasher;
import org.cresplanex.api.state.webgateway.proxy.query.TaskQueryProxy;
import org.cresplanex.api.state.webgateway.retriever.RetrievedCacheContainer;
import org.cresplanex.api.state.webgateway.retriever.domain.TaskRetriever;
import org.cresplanex.api.state.webgateway.retriever.resolver.TaskRetrieveResolver;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class TaskCompositionService {

    private final TaskQueryProxy taskQueryProxy;
    private final AttachRelationTask attachRelationTask;

    public TaskDto findTask(String operatorId, String taskId, List<String> with) {
        TaskDto task;
        TaskRetriever taskRetriever = TaskRetrieveResolver.resolve(
                with != null ? with.toArray(new String[0]) : new String[0]
        );
        int need = TaskCompositionHelper.calculateNeedQuery(List.of(taskRetriever));
        RetrievedCacheContainer cache = new RetrievedCacheContainer();
        switch (need) {
            case TaskCompositionHelper.GET_TASK_ATTACHED_FILE_OBJECTS:
                task = taskQueryProxy.findTaskWithAttachments(
                        operatorId,
                        taskId
                );
                cache.getCache().put(TaskHasher.hashTaskWithAttachments(taskId), task.deepClone());
                break;
            default:
                task = taskQueryProxy.findTask(
                        operatorId,
                        taskId
                );
                cache.getCache().put(TaskHasher.hashTask(taskId), task.deepClone());
                break;
        }

        attachRelationTask.attach(
                operatorId,
                cache,
                taskRetriever,
                task
        );

        return task;
    }

    public ListResponseDto.InternalData<TaskDto> getTasks(
            String operatorId,
            int limit,
            int offset,
            String cursor,
            String pagination,
            String sortField,
            String sortOrder,
            boolean withCount,
            boolean hasTeamFilter,
            List<String> filterTeamIds,
            boolean hasStatusFilter,
            List<String> filterStatuses,
            boolean hasChargeUserFilter,
            List<String> filterChargeUserIds,
            String filterStartDatetimeEarlierThan,
            String filterStartDatetimeLaterThan,
            String filterDueDatetimeEarlierThan,
            String filterDueDatetimeLaterThan,
            boolean hasFileObjectFilter,
            List<String> filterFileObjectIds,
            String fileObjectFilterType,
            List<String> with
    ) {
        ListResponseDto.InternalData<TaskDto> tasks;
        TaskRetriever taskRetriever = TaskRetrieveResolver.resolve(
                with != null ? with.toArray(new String[0]) : new String[0]
        );
        int need = TaskCompositionHelper.calculateNeedQuery(List.of(taskRetriever));
        RetrievedCacheContainer cache = new RetrievedCacheContainer();
        switch (need) {
            case TaskCompositionHelper.GET_TASK_ATTACHED_FILE_OBJECTS:
                tasks = taskQueryProxy.getTasksWithAttachments(
                        operatorId,
                        limit,
                        offset,
                        cursor,
                        pagination,
                        sortField,
                        sortOrder,
                        withCount,
                        hasTeamFilter,
                        filterTeamIds,
                        hasStatusFilter,
                        filterStatuses,
                        hasChargeUserFilter,
                        filterChargeUserIds,
                        filterStartDatetimeEarlierThan,
                        filterStartDatetimeLaterThan,
                        filterDueDatetimeEarlierThan,
                        filterDueDatetimeLaterThan,
                        hasFileObjectFilter,
                        filterFileObjectIds,
                        fileObjectFilterType
                );
                for (TaskDto dto : tasks.getListData()) {
                    cache.getCache().put(TaskHasher.hashTaskWithAttachments(dto.getTaskId()), dto.deepClone());
                }
                break;
            default:
                tasks = taskQueryProxy.getTasks(
                        operatorId,
                        limit,
                        offset,
                        cursor,
                        pagination,
                        sortField,
                        sortOrder,
                        withCount,
                        hasTeamFilter,
                        filterTeamIds,
                        hasStatusFilter,
                        filterStatuses,
                        hasChargeUserFilter,
                        filterChargeUserIds,
                        filterStartDatetimeEarlierThan,
                        filterStartDatetimeLaterThan,
                        filterDueDatetimeEarlierThan,
                        filterDueDatetimeLaterThan,
                        hasFileObjectFilter,
                        filterFileObjectIds,
                        fileObjectFilterType
                );
                for (TaskDto dto : tasks.getListData()) {
                    cache.getCache().put(TaskHasher.hashTask(dto.getTaskId()), dto.deepClone());
                }
                break;
        }
        attachRelationTask.attach(
                operatorId,
                cache,
                taskRetriever,
                tasks.getListData()
        );

        return tasks;
    }
}
