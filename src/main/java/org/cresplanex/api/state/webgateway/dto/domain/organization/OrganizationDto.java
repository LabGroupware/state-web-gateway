package org.cresplanex.api.state.webgateway.dto.domain.organization;

import lombok.*;
import org.cresplanex.api.state.webgateway.dto.domain.DeepCloneable;
import org.cresplanex.api.state.webgateway.dto.domain.DomainDto;
import org.cresplanex.api.state.webgateway.dto.domain.ListRelation;
import org.cresplanex.api.state.webgateway.dto.domain.Relation;
import org.cresplanex.api.state.webgateway.dto.domain.plan.TaskOnFileObjectDto;
import org.cresplanex.api.state.webgateway.dto.domain.team.TeamDto;
import org.cresplanex.api.state.webgateway.dto.domain.userprofile.UserProfileDto;
import org.cresplanex.api.state.webgateway.dto.domain.userprofile.UserProfileOnOrganizationDto;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
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
    }

    public OrganizationDto merge(OrganizationDto organizationDto) {
        if (organizationDto == null) {
            return this;
        }

        if (organizationDto.getUsers() != null && organizationDto.getUsers().isHasValue()) {
            if (this.getUsers() == null || !this.getUsers().isHasValue()) {
                this.setUsers(organizationDto.getUsers());
            } else {
                for (UserProfileOnOrganizationDto userProfileOnOrganizationDto : organizationDto.getUsers().getValue()) {
                    boolean isExist = false;
                    for (UserProfileOnOrganizationDto thisUserProfileOnOrganizationDto : this.getUsers().getValue()) {
                        if (userProfileOnOrganizationDto.getUserId().equals(thisUserProfileOnOrganizationDto.getUserId())) {
                            thisUserProfileOnOrganizationDto.merge(userProfileOnOrganizationDto);
                            isExist = true;
                            break;
                        }
                    }
                    if (!isExist) {
                        this.getUsers().getValue().add(userProfileOnOrganizationDto);
                    }
                }
            }
        }

        if (organizationDto.getOwner() != null && organizationDto.getOwner().isHasValue()) {
            if (this.getOwner() == null || !this.getOwner().isHasValue()) {
                this.setOwner(organizationDto.getOwner());
            } else {
                this.getOwner().getValue().merge(organizationDto.getOwner().getValue());
            }
        }

        if (organizationDto.getTeams() != null && organizationDto.getTeams().isHasValue()) {
            if (this.getTeams() == null || !this.getTeams().isHasValue()) {
                this.setTeams(organizationDto.getTeams());
            } else {
                for (TeamDto teamDto : organizationDto.getTeams().getValue()) {
                    boolean isExist = false;
                    for (TeamDto thisTeamDto : this.getTeams().getValue()) {
                        if (teamDto.getTeamId().equals(thisTeamDto.getTeamId())) {
                            thisTeamDto.merge(teamDto);
                            isExist = true;
                            break;
                        }
                    }
                    if (!isExist) {
                        this.getTeams().getValue().add(teamDto);
                    }
                }
            }
        }

        return this;
    }
}
