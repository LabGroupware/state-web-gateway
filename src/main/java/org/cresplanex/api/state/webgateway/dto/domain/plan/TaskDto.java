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
        try {
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
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
