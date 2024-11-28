package org.cresplanex.api.state.webgateway.dto.domain.plan;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.cresplanex.api.state.webgateway.dto.domain.DeepCloneable;
import org.cresplanex.api.state.webgateway.dto.domain.OverMerge;
import org.cresplanex.api.state.webgateway.dto.domain.Relation;
import org.cresplanex.api.state.webgateway.dto.domain.storage.FileObjectOnTaskDto;
import org.cresplanex.api.state.webgateway.dto.domain.team.TeamDto;
import org.cresplanex.api.state.webgateway.dto.domain.userprofile.UserProfileDto;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TaskOnFileObjectDto extends TaskDto implements OverMerge<TaskDto, TaskOnFileObjectDto>, DeepCloneable {

    public TaskOnFileObjectDto(TaskDto taskDto) {
        super(
                taskDto.getTaskId(),
                taskDto.getTeamId(),
                taskDto.getChargeUserId(),
                taskDto.getTitle(),
                taskDto.getDescription(),
                taskDto.getStatus(),
                taskDto.getStartDateTime(),
                taskDto.getDueDateTime(),
                taskDto.getAttachments(),
                taskDto.getTeam(),
                taskDto.getChargeUser()
        );
    }

    public TaskOnFileObjectDto(TaskOnFileObjectDto taskOnFileObjectDto) {
        super(
                taskOnFileObjectDto.getTaskId(),
                taskOnFileObjectDto.getTeamId(),
                taskOnFileObjectDto.getChargeUserId(),
                taskOnFileObjectDto.getTitle(),
                taskOnFileObjectDto.getDescription(),
                taskOnFileObjectDto.getStatus(),
                taskOnFileObjectDto.getStartDateTime(),
                taskOnFileObjectDto.getDueDateTime(),
                taskOnFileObjectDto.getAttachments(),
                taskOnFileObjectDto.getTeam(),
                taskOnFileObjectDto.getChargeUser()
        );
    }

    public TaskOnFileObjectDto(TaskDto taskDto, TaskOnFileObjectDto taskOnFileObjectDto) {
        this(taskDto);
    }

    @Override
    public TaskOnFileObjectDto overMerge(TaskDto taskDto) {
        return new TaskOnFileObjectDto(taskDto, this);
    }

    @Override
    public TaskOnFileObjectDto deepClone() {
        try {
            return (TaskOnFileObjectDto) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
