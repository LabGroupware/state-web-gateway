package org.cresplanex.api.state.webgateway.dto.domain.team;

import lombok.*;
import org.cresplanex.api.state.webgateway.dto.domain.DeepCloneable;
import org.cresplanex.api.state.webgateway.dto.domain.DomainDto;
import org.cresplanex.api.state.webgateway.dto.domain.ListRelation;
import org.cresplanex.api.state.webgateway.dto.domain.Relation;
import org.cresplanex.api.state.webgateway.dto.domain.organization.OrganizationDto;
import org.cresplanex.api.state.webgateway.dto.domain.plan.TaskDto;
import org.cresplanex.api.state.webgateway.dto.domain.plan.TaskOnFileObjectDto;
import org.cresplanex.api.state.webgateway.dto.domain.storage.FileObjectDto;
import org.cresplanex.api.state.webgateway.dto.domain.userprofile.UserProfileOnTeamDto;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TeamDto extends DomainDto implements DeepCloneable {

    private String teamId;

    private String organizationId;

    private String name;

    private String description;

    private boolean isDefault;

    private ListRelation<UserProfileOnTeamDto> users;

    private Relation<OrganizationDto> organization;

    private ListRelation<TaskDto> tasks;

    @Override
    public TeamDto deepClone() {
        TeamDto cloned = (TeamDto) super.clone();
        if (users != null){
            cloned.setUsers(users.clone());
        }
        if (organization != null){
            cloned.setOrganization(organization.clone());
        }
        if (tasks != null){
            cloned.setTasks(tasks.clone());
        }
        return cloned;
    }

    public TeamDto merge(TeamDto teamDto) {
        if (teamDto == null) {
            return this;
        }

        if (teamDto.getUsers() != null && teamDto.getUsers().isHasValue()) {
            if (this.getUsers() == null || !this.getUsers().isHasValue()) {
                this.setUsers(teamDto.getUsers());
            } else {
                for (UserProfileOnTeamDto userProfileOnTeamDto : teamDto.getUsers().getValue()) {
                    boolean isExist = false;
                    for (UserProfileOnTeamDto thisUserProfileOnTeamDto : this.getUsers().getValue()) {
                        if (userProfileOnTeamDto.getUserProfileId().equals(thisUserProfileOnTeamDto.getUserProfileId())) {
                            thisUserProfileOnTeamDto.merge(userProfileOnTeamDto);
                            isExist = true;
                            break;
                        }
                    }
                    if (!isExist) {
                        this.getUsers().getValue().add(userProfileOnTeamDto);
                    }
                }
            }
        }

        if (teamDto.getOrganization() != null && teamDto.getOrganization().isHasValue()) {
            if (this.getOrganization() == null || !this.getOrganization().isHasValue()) {
                this.setOrganization(teamDto.getOrganization());
            } else {
                this.getOrganization().getValue().merge(teamDto.getOrganization().getValue());
            }
        }

        if (teamDto.getTasks() != null && teamDto.getTasks().isHasValue()) {
            if (this.getTasks() == null || !this.getTasks().isHasValue()) {
                this.setTasks(teamDto.getTasks());
            } else {
                for (TaskDto taskDto : teamDto.getTasks().getValue()) {
                    boolean isExist = false;
                    for (TaskDto thisTaskDto : this.getTasks().getValue()) {
                        if (taskDto.getTaskId().equals(thisTaskDto.getTaskId())) {
                            thisTaskDto.merge(taskDto);
                            isExist = true;
                            break;
                        }
                    }
                    if (!isExist) {
                        this.getTasks().getValue().add(taskDto);
                    }
                }
            }
        }

        return this;
    }
}
