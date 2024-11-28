package org.cresplanex.api.state.webgateway.mapper;

import build.buf.gen.plan.v1.Task;
import build.buf.gen.plan.v1.TaskOnFileObject;
import build.buf.gen.plan.v1.TaskWithAttachments;
import org.cresplanex.api.state.webgateway.dto.domain.ListRelation;
import org.cresplanex.api.state.webgateway.dto.domain.Relation;
import org.cresplanex.api.state.webgateway.dto.domain.plan.TaskDto;
import org.cresplanex.api.state.webgateway.dto.domain.plan.TaskOnFileObjectDto;
import org.cresplanex.api.state.webgateway.dto.domain.storage.FileObjectOnTaskDto;
import org.cresplanex.api.state.webgateway.dto.domain.team.TeamDto;
import org.cresplanex.api.state.webgateway.dto.domain.team.TeamOnUserProfileDto;
import org.cresplanex.api.state.webgateway.dto.domain.userprofile.UserProfileDto;

import java.util.List;

public class TaskMapper {

    public static TaskDto convert(Task task) {
        return TaskDto.builder()
                .taskId(task.getTaskId())
                .status(task.getStatus())
                .title(task.getTitle())
                .description(task.getDescription())
                .chargeUserId(task.getChargeUserId())
                .teamId(task.getTeamId())
                .startDateTime(task.getStartDatetime())
                .dueDateTime(task.getDueDatetime())
                .attachments(ListRelation.<FileObjectOnTaskDto>builder().hasValue(false).build())
                .team(Relation.<TeamDto>builder().hasValue(false).build())
                .chargeUser(Relation.<UserProfileDto>builder().hasValue(false).build())
                .build();
    }

    public static TaskDto convertFromTaskId(String taskId) {
        return TaskDto.builder()
                .taskId(taskId)
                .build();
    }

    public static TaskDto convert(TaskWithAttachments task) {
        return TaskDto.builder()
                .taskId(task.getTask().getTaskId())
                .status(task.getTask().getStatus())
                .title(task.getTask().getTitle())
                .description(task.getTask().getDescription())
                .chargeUserId(task.getTask().getChargeUserId())
                .teamId(task.getTask().getTeamId())
                .startDateTime(task.getTask().getStartDatetime())
                .dueDateTime(task.getTask().getDueDatetime())
                .attachments(ListRelation.<FileObjectOnTaskDto>builder().hasValue(true).value(
                        task.getAttachmentsList().stream()
                                .map(FileObjectMapper::convert)
                                .toList()
                ).build())
                .team(Relation.<TeamDto>builder().hasValue(false).build())
                .chargeUser(Relation.<UserProfileDto>builder().hasValue(false).build())
                .build();
    }

    public static TaskOnFileObjectDto convert(TaskOnFileObject taskOnFileObject) {
        return new TaskOnFileObjectDto(
                convert(taskOnFileObject.getTask())
        );
    }
}
