package org.cresplanex.api.state.webgateway.dto.domain.plan;

import lombok.*;
import org.cresplanex.api.state.webgateway.dto.domain.DeepCloneable;
import org.cresplanex.api.state.webgateway.dto.domain.DomainDto;
import org.cresplanex.api.state.webgateway.dto.domain.ListRelation;
import org.cresplanex.api.state.webgateway.dto.domain.Relation;
import org.cresplanex.api.state.webgateway.dto.domain.storage.FileObjectOnTaskDto;
import org.cresplanex.api.state.webgateway.dto.domain.team.TeamDto;
import org.cresplanex.api.state.webgateway.dto.domain.userprofile.UserProfileDto;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TaskDto extends DomainDto implements DeepCloneable {

    private String taskId;

    private String teamId;

    private String chargeUserId;

    private String title;

    private String description;

    private String status;

    private String startDateTime;

    private String dueDateTime;

    private ListRelation<FileObjectOnTaskDto> attachments;

    private Relation<TeamDto> team;

    private Relation<UserProfileDto> chargeUser;

    @Override
    public TaskDto deepClone() {
        TaskDto cloned = (TaskDto) super.clone();
        if (attachments != null){
            cloned.setAttachments(attachments.clone());
        }
        if (team != null){
            cloned.setTeam(team.clone());
        }
        if (chargeUser != null){
            cloned.setChargeUser(chargeUser.clone());
        }
        return cloned;
    }

    public TaskDto merge(TaskDto taskDto) {
        if (taskDto == null) {
            return this;
        }

        if (taskDto.getAttachments() != null && taskDto.getAttachments().isHasValue()) {
            if (this.getAttachments() == null || !this.getAttachments().isHasValue()) {
                this.setAttachments(taskDto.getAttachments());
            } else {
                for (FileObjectOnTaskDto fileObjectOnTaskDto : taskDto.getAttachments().getValue()) {
                    boolean isExist = false;
                    for (FileObjectOnTaskDto thisFileObjectOnTaskDto : this.getAttachments().getValue()) {
                        if (fileObjectOnTaskDto.getFileObjectId().equals(thisFileObjectOnTaskDto.getFileObjectId())) {
                            thisFileObjectOnTaskDto.merge(fileObjectOnTaskDto);
                            isExist = true;
                            break;
                        }
                    }
                    if (!isExist) {
                        this.getAttachments().getValue().add(fileObjectOnTaskDto);
                    }
                }
            }
        }

        if (taskDto.getTeam() != null && taskDto.getTeam().isHasValue()) {
            if (this.getTeam() == null || !this.getTeam().isHasValue()) {
                this.setTeam(taskDto.getTeam());
            } else {
                this.getTeam().getValue().merge(taskDto.getTeam().getValue());
            }
        }

        if (taskDto.getChargeUser() != null && taskDto.getChargeUser().isHasValue()) {
            if (this.getChargeUser() == null || !this.getChargeUser().isHasValue()) {
                this.setChargeUser(taskDto.getChargeUser());
            } else {
                this.getChargeUser().getValue().merge(taskDto.getChargeUser().getValue());
            }
        }

        return this;
    }
}
