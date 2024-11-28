package org.cresplanex.api.state.webgateway.composition;

import lombok.RequiredArgsConstructor;
import org.cresplanex.api.state.webgateway.composition.attach.AttachRelationTask;
import org.cresplanex.api.state.webgateway.composition.helper.TaskCompositionHelper;
import org.cresplanex.api.state.webgateway.dto.ListResponseDto;
import org.cresplanex.api.state.webgateway.dto.domain.plan.TaskDto;
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
                with.toArray(new String[0])
        );
        int need = TaskCompositionHelper.calculateNeedQuery(List.of(taskRetriever));
        switch (need) {
            case TaskCompositionHelper.GET_TASK_ATTACHED_FILE_OBJECTS:
                task = taskQueryProxy.findTaskWithAttachments(
                        operatorId,
                        taskId
                );
                break;
            default:
                task = taskQueryProxy.findTask(
                        operatorId,
                        taskId
                );
                break;
        }
        RetrievedCacheContainer cache = new RetrievedCacheContainer();
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
            String teamId,
            int limit,
            int offset,
            String cursor,
            String pagination,
            String sortField,
            String sortOrder,
            boolean withCount,
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
                with.toArray(new String[0])
        );
        int need = TaskCompositionHelper.calculateNeedQuery(List.of(taskRetriever));
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
                        true,
                        List.of(teamId),
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
                        true,
                        List.of(teamId),
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
                break;
        }
        RetrievedCacheContainer cache = new RetrievedCacheContainer();
        attachRelationTask.attach(
                operatorId,
                cache,
                taskRetriever,
                tasks.getListData()
        );

        return tasks;
    }
}
