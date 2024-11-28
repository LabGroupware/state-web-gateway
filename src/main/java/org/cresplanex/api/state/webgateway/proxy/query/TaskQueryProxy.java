package org.cresplanex.api.state.webgateway.proxy.query;

import build.buf.gen.cresplanex.nova.v1.SortOrder;
import build.buf.gen.plan.v1.*;
import build.buf.gen.plan.v1.GetTasksOnFileObjectRequest;
import build.buf.gen.plan.v1.GetTasksOnFileObjectResponse;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.cresplanex.api.state.webgateway.composition.CompositionUtils;
import org.cresplanex.api.state.webgateway.dto.ListResponseDto;
import org.cresplanex.api.state.webgateway.dto.domain.plan.TaskDto;
import org.cresplanex.api.state.webgateway.dto.domain.plan.TaskOnFileObjectDto;
import org.cresplanex.api.state.webgateway.dto.domain.storage.FileObjectOnTaskDto;
import org.cresplanex.api.state.webgateway.mapper.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskQueryProxy {

    @GrpcClient("planService")
    private PlanServiceGrpc.PlanServiceBlockingStub planServiceBlockingStub;

    public TaskDto findTask(
            String operatorId,
            String taskId
    ) {
        FindTaskResponse response = planServiceBlockingStub.findTask(
                FindTaskRequest.newBuilder()
                        .setOperatorId(operatorId)
                        .setTaskId(taskId)
                        .build()
        );
        return TaskMapper.convert(response.getTask());
    }

    public TaskDto findTaskWithAttachments(
            String operatorId,
            String taskId
    ) {
        FindTaskWithAttachmentsResponse response = planServiceBlockingStub.findTaskWithAttachments(
                FindTaskWithAttachmentsRequest.newBuilder()
                        .setOperatorId(operatorId)
                        .setTaskId(taskId)
                        .build()
        );
        return TaskMapper.convert(response.getTask());
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
            String fileObjectFilterType
    ) {
        GetTasksResponse response = planServiceBlockingStub.getTasks(
                GetTasksRequest.newBuilder()
                        .setOperatorId(operatorId)
                        .setPagination(CompositionUtils.createPagination(limit, offset, cursor, pagination))
                        .setSort(createTaskSort(sortField, sortOrder))
                        .setWithCount(withCount)
                        .setFilterTeam(createTaskFilterTeam(hasTeamFilter, filterTeamIds))
                        .setFilterStatus(createTaskFilterStatus(hasStatusFilter, filterStatuses))
                        .setFilterChargeUser(createTaskFilterChargeUser(hasChargeUserFilter, filterChargeUserIds))
                        .setFilterStartDatetime(createTaskFilterStartDatetime(filterStartDatetimeEarlierThan, filterStartDatetimeLaterThan))
                        .setFilterDueDatetime(createTaskFilterDueDatetime(filterDueDatetimeEarlierThan, filterDueDatetimeLaterThan))
                        .setFilterFileObject(createTaskFilterFileObject(hasFileObjectFilter, filterFileObjectIds, fileObjectFilterType))
                        .build()
        );
        return new ListResponseDto.InternalData<>(
                response.getTasksList().stream()
                        .map(TaskMapper::convert)
                        .toList(),
                CommonMapper.countConvert(response.getCount())
        );
    }

    public ListResponseDto.InternalData<TaskDto> getTasksWithAttachments(
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
            String fileObjectFilterType
    ) {
        GetTasksWithAttachmentsResponse response = planServiceBlockingStub.getTasksWithAttachments(
                GetTasksWithAttachmentsRequest.newBuilder()
                        .setOperatorId(operatorId)
                        .setPagination(CompositionUtils.createPagination(limit, offset, cursor, pagination))
                        .setSort(createTaskWithAttachmentsSort(sortField, sortOrder))
                        .setWithCount(withCount)
                        .setFilterTeam(createTaskFilterTeam(hasTeamFilter, filterTeamIds))
                        .setFilterStatus(createTaskFilterStatus(hasStatusFilter, filterStatuses))
                        .setFilterChargeUser(createTaskFilterChargeUser(hasChargeUserFilter, filterChargeUserIds))
                        .setFilterStartDatetime(createTaskFilterStartDatetime(filterStartDatetimeEarlierThan, filterStartDatetimeLaterThan))
                        .setFilterDueDatetime(createTaskFilterDueDatetime(filterDueDatetimeEarlierThan, filterDueDatetimeLaterThan))
                        .setFilterFileObject(createTaskFilterFileObject(hasFileObjectFilter, filterFileObjectIds, fileObjectFilterType))
                        .build()
        );
        return new ListResponseDto.InternalData<>(
                response.getTasksList().stream()
                        .map(TaskMapper::convert)
                        .toList(),
                CommonMapper.countConvert(response.getCount())
        );
    }

    public ListResponseDto.InternalData<TaskDto> getPluralTasks(
            String operatorId,
            List<String> taskIds,
            String sortField,
            String sortOrder
    ) {
        GetPluralTasksResponse response = planServiceBlockingStub.getPluralTasks(
                GetPluralTasksRequest.newBuilder()
                        .setOperatorId(operatorId)
                        .addAllTaskIds(taskIds)
                        .setSort(createTaskSort(sortField, sortOrder))
                        .build()
        );

        return new ListResponseDto.InternalData<>(
                response.getTasksList().stream()
                        .map(TaskMapper::convert)
                        .toList(),
                CommonMapper.invalidCountGenerate()
        );
    }

    public ListResponseDto.InternalData<TaskDto> getPluralTasksWithAttachments(
            String operatorId,
            List<String> taskIds,
            String sortField,
            String sortOrder
    ) {
        GetPluralTasksWithAttachmentsResponse response = planServiceBlockingStub.getPluralTasksWithAttachments(
                GetPluralTasksWithAttachmentsRequest.newBuilder()
                        .setOperatorId(operatorId)
                        .addAllTaskIds(taskIds)
                        .setSort(createTaskWithAttachmentsSort(sortField, sortOrder))
                        .build()
        );

        return new ListResponseDto.InternalData<>(
                response.getTasksList().stream()
                        .map(TaskMapper::convert)
                        .toList(),
                CommonMapper.invalidCountGenerate()
        );
    }

    public ListResponseDto.InternalData<FileObjectOnTaskDto> getFileObjectsOnTask(
            String operatorId,
            String taskId,
            int limit,
            int offset,
            String cursor,
            String pagination,
            String sortField,
            String sortOrder,
            boolean withCount
    ) {
        GetFileObjectsOnTaskResponse response = planServiceBlockingStub.getFileObjectsOnTask(
                GetFileObjectsOnTaskRequest.newBuilder()
                        .setOperatorId(operatorId)
                        .setTaskId(taskId)
                        .setPagination(CompositionUtils.createPagination(limit, offset, cursor, pagination))
                        .setSort(createFileObjectOnTaskSort(sortField, sortOrder))
                        .setWithCount(withCount)
                        .build()
        );

        return new ListResponseDto.InternalData<>(
                response.getFileObjectsList().stream()
                        .map(FileObjectMapper::convert)
                        .toList(),
                CommonMapper.countConvert(response.getCount())
        );
    }

    public ListResponseDto.InternalData<TaskOnFileObjectDto> getTasksOnFileObject(
            String operatorId,
            String fileObjectId,
            int limit,
            int offset,
            String cursor,
            String pagination,
            String sortField,
            String sortOrder,
            boolean withCount
    ) {
        GetTasksOnFileObjectResponse response = planServiceBlockingStub.getTasksOnFileObject(
                GetTasksOnFileObjectRequest.newBuilder()
                        .setOperatorId(operatorId)
                        .setFileObjectId(fileObjectId)
                        .setPagination(CompositionUtils.createPagination(limit, offset, cursor, pagination))
                        .setSort(createTaskOnFileObjectSort(sortField, sortOrder))
                        .setWithCount(withCount)
                        .build()
        );

        return new ListResponseDto.InternalData<>(
                response.getTasksList().stream()
                        .map(TaskMapper::convert)
                        .toList(),
                CommonMapper.countConvert(response.getCount())
        );
    }

    public static TaskSort createTaskSort(
            String sortField,
            String sortOrder
    ) {
        TaskOrderField orderField = switch (sortField) {
            case "title" -> TaskOrderField.TASK_ORDER_FIELD_TITLE;
            case "start" -> TaskOrderField.TASK_ORDER_FIELD_START_DATETIME;
            case "due" -> TaskOrderField.TASK_ORDER_FIELD_DUE_DATETIME;
            case "create" -> TaskOrderField.TASK_ORDER_FIELD_CREATE;
            default -> TaskOrderField.TASK_ORDER_FIELD_CREATE;
        };
        SortOrder order = switch (sortOrder) {
            case "asc" -> SortOrder.SORT_ORDER_ASC;
            case "desc" -> SortOrder.SORT_ORDER_DESC;
            default -> SortOrder.SORT_ORDER_ASC;
        };
        return TaskSort.newBuilder()
                .setOrderField(orderField)
                .setOrder(order)
                .build();
    }

    public static TaskWithAttachmentsSort createTaskWithAttachmentsSort(
            String sortField,
            String sortOrder
    ) {
        TaskWithAttachmentsOrderField orderField = switch (sortField) {
            case "title" -> TaskWithAttachmentsOrderField.TASK_WITH_ATTACHMENTS_ORDER_FIELD_TITLE;
            case "start" -> TaskWithAttachmentsOrderField.TASK_WITH_ATTACHMENTS_ORDER_FIELD_START_DATETIME;
            case "due" -> TaskWithAttachmentsOrderField.TASK_WITH_ATTACHMENTS_ORDER_FIELD_DUE_DATETIME;
            case "create" -> TaskWithAttachmentsOrderField.TASK_WITH_ATTACHMENTS_ORDER_FIELD_CREATE;
            default -> TaskWithAttachmentsOrderField.TASK_WITH_ATTACHMENTS_ORDER_FIELD_CREATE;
        };
        SortOrder order = switch (sortOrder) {
            case "asc" -> SortOrder.SORT_ORDER_ASC;
            case "desc" -> SortOrder.SORT_ORDER_DESC;
            default -> SortOrder.SORT_ORDER_ASC;
        };
        return TaskWithAttachmentsSort.newBuilder()
                .setOrderField(orderField)
                .setOrder(order)
                .build();
    }

    public static TaskOnFileObjectSort createTaskOnFileObjectSort(
            String sortField,
            String sortOrder
    ) {
        TaskOnFileObjectOrderField orderField = switch (sortField) {
            case "add" -> TaskOnFileObjectOrderField.TASK_ON_FILE_OBJECT_ORDER_FIELD_ADD;
            case "title" -> TaskOnFileObjectOrderField.TASK_ON_FILE_OBJECT_ORDER_FIELD_TITLE;
            case "start" -> TaskOnFileObjectOrderField.TASK_ON_FILE_OBJECT_ORDER_FIELD_START_DATETIME;
            case "due" -> TaskOnFileObjectOrderField.TASK_ON_FILE_OBJECT_ORDER_FIELD_DUE_DATETIME;
            case "create" -> TaskOnFileObjectOrderField.TASK_ON_FILE_OBJECT_ORDER_FIELD_CREATE;
            default -> TaskOnFileObjectOrderField.TASK_ON_FILE_OBJECT_ORDER_FIELD_ADD;
        };
        SortOrder order = switch (sortOrder) {
            case "asc" -> SortOrder.SORT_ORDER_ASC;
            case "desc" -> SortOrder.SORT_ORDER_DESC;
            default -> SortOrder.SORT_ORDER_ASC;
        };
        return TaskOnFileObjectSort.newBuilder()
                .setOrderField(orderField)
                .setOrder(order)
                .build();
    }

    public static FileObjectOnTaskSort createFileObjectOnTaskSort(
            String sortField,
            String sortOrder
    ) {
        FileObjectOnTaskOrderField orderField = switch (sortField) {
            case "add" -> FileObjectOnTaskOrderField.FILE_OBJECT_ON_TASK_ORDER_FIELD_ADD;
            default -> FileObjectOnTaskOrderField.FILE_OBJECT_ON_TASK_ORDER_FIELD_ADD;
        };
        SortOrder order = switch (sortOrder) {
            case "asc" -> SortOrder.SORT_ORDER_ASC;
            case "desc" -> SortOrder.SORT_ORDER_DESC;
            default -> SortOrder.SORT_ORDER_ASC;
        };
        return FileObjectOnTaskSort.newBuilder()
                .setOrderField(orderField)
                .setOrder(order)
                .build();
    }

    public static TaskFilterTeam createTaskFilterTeam(
            boolean hasTeamFilter,
            List<String> filterTeamIds
    ) {
        return TaskFilterTeam.newBuilder()
                .setHasValue(hasTeamFilter)
                .addAllTeamIds(filterTeamIds)
                .build();
    }

    public static TaskFilterStatus createTaskFilterStatus(
            boolean hasStatusFilter,
            List<String> filterStatuses
    ) {
        return TaskFilterStatus.newBuilder()
                .setHasValue(hasStatusFilter)
                .addAllStatuses(filterStatuses)
                .build();
    }

    public static TaskFilterChargeUser createTaskFilterChargeUser(
            boolean hasChargeUserFilter,
            List<String> filterChargeUserIds
    ) {
        return TaskFilterChargeUser.newBuilder()
                .setHasValue(hasChargeUserFilter)
                .addAllChargeUserIds(filterChargeUserIds)
                .build();
    }

    public static TaskFilterStartDatetime createTaskFilterStartDatetime(
            String filterStartDatetimeEarlierThan,
            String filterStartDatetimeLaterThan
    ) {
        return TaskFilterStartDatetime.newBuilder()
                .setEarlierInfinite(filterStartDatetimeEarlierThan.isBlank())
                .setEarlierThan(filterStartDatetimeEarlierThan)
                .setLaterInfinite(filterStartDatetimeLaterThan.isBlank())
                .setLaterThan(filterStartDatetimeLaterThan)
                .build();
    }

    public static TaskFilterDueDatetime createTaskFilterDueDatetime(
            String filterDueDatetimeEarlierThan,
            String filterDueDatetimeLaterThan
    ) {
        return TaskFilterDueDatetime.newBuilder()
                .setEarlierInfinite(filterDueDatetimeEarlierThan.isBlank())
                .setEarlierThan(filterDueDatetimeEarlierThan)
                .setLaterInfinite(filterDueDatetimeLaterThan.isBlank())
                .setLaterThan(filterDueDatetimeLaterThan)
                .build();
    }

    public static TaskFilterFileObject createTaskFilterFileObject(
            boolean hasFileObjectFilter,
            List<String> filterFileObjectIds,
            String fileObjectFilterType
    ) {
        boolean allFileObjectFilter = fileObjectFilterType != null && fileObjectFilterType.equals("all");
        return TaskFilterFileObject.newBuilder()
                .setHasValue(hasFileObjectFilter)
                .addAllFileObjectIds(filterFileObjectIds)
                .setAny(!allFileObjectFilter)
                .build();
    }
}
