package org.cresplanex.api.state.webgateway.dto.domain.team;

import lombok.*;
import org.cresplanex.api.state.webgateway.dto.domain.DeepCloneable;
import org.cresplanex.api.state.webgateway.dto.domain.DomainDto;
import org.cresplanex.api.state.webgateway.dto.domain.ListRelation;
import org.cresplanex.api.state.webgateway.dto.domain.Relation;
import org.cresplanex.api.state.webgateway.dto.domain.organization.OrganizationDto;
import org.cresplanex.api.state.webgateway.dto.domain.plan.TaskDto;
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
        try {
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
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
