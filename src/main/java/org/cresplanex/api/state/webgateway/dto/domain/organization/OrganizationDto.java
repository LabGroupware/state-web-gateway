package org.cresplanex.api.state.webgateway.dto.domain.organization;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.cresplanex.api.state.webgateway.dto.domain.DeepCloneable;
import org.cresplanex.api.state.webgateway.dto.domain.DomainDto;
import org.cresplanex.api.state.webgateway.dto.domain.ListRelation;
import org.cresplanex.api.state.webgateway.dto.domain.Relation;
import org.cresplanex.api.state.webgateway.dto.domain.team.TeamDto;
import org.cresplanex.api.state.webgateway.dto.domain.userprofile.UserProfileDto;
import org.cresplanex.api.state.webgateway.dto.domain.userprofile.UserProfileOnOrganizationDto;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OrganizationDto extends DomainDto implements DeepCloneable {

    private String organizationId;

    private String ownerId;

    private String name;

    private String plan;

    private String siteUrl;

    private ListRelation<UserProfileOnOrganizationDto> users;

    private Relation<UserProfileDto> owner;

    private ListRelation<TeamDto> teams;

    @Override
    public OrganizationDto deepClone() {
        try {
            OrganizationDto cloned = (OrganizationDto) super.clone();
            if (users != null){
                cloned.setUsers(users.clone());
            }
            if (owner != null){
                cloned.setOwner(owner.clone());
            }
            if (teams != null){
                cloned.setTeams(teams.clone());
            }
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
