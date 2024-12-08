package org.cresplanex.api.state.webgateway.dto.domain.userprofile;

import lombok.*;
import org.cresplanex.api.state.webgateway.dto.domain.DeepCloneable;
import org.cresplanex.api.state.webgateway.dto.domain.DomainDto;
import org.cresplanex.api.state.webgateway.dto.domain.ListRelation;
import org.cresplanex.api.state.webgateway.dto.domain.Relation;
import org.cresplanex.api.state.webgateway.dto.domain.organization.OrganizationDto;
import org.cresplanex.api.state.webgateway.dto.domain.organization.OrganizationOnUserProfileDto;
import org.cresplanex.api.state.webgateway.dto.domain.plan.TaskDto;
import org.cresplanex.api.state.webgateway.dto.domain.team.TeamOnUserProfileDto;
import org.cresplanex.api.state.webgateway.dto.domain.userpreference.UserPreferenceDto;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserProfileDto extends DomainDto implements DeepCloneable {

    private String userProfileId;

    private String userId;

    private String name;

    private String email;

    private String givenName;

    private String familyName;

    private String middleName;

    private String nickname;

    private String profile;

    private String picture;

    private String website;

    private String phone;

    private String gender;

    private String birthdate;

    private String zoneinfo;

    private String locale;

    private Relation<UserPreferenceDto> userPreference;

    private ListRelation<OrganizationOnUserProfileDto> organizations;

    private ListRelation<TeamOnUserProfileDto> teams;

    private ListRelation<OrganizationDto> ownedOrganizations;

    private ListRelation<TaskDto> chargeTasks;

    public UserProfileDto merge(UserProfileDto userProfileDto) {
        if (userProfileDto == null) {
            return this;
        }

        if (userProfileDto.getUserPreference() != null && userProfileDto.getUserPreference().isHasValue()) {
            if (this.getUserPreference() == null || !this.getUserPreference().isHasValue()) {
                this.setUserPreference(userProfileDto.getUserPreference());
            } else {
                this.getUserPreference().getValue().merge(userProfileDto.getUserPreference().getValue());
            }
        }
        if (userProfileDto.getOrganizations() != null && userProfileDto.getOrganizations().isHasValue()) {
            if (this.getOrganizations() == null || !this.getOrganizations().isHasValue()) {
                this.setOrganizations(userProfileDto.getOrganizations());
            } else {
                for (OrganizationOnUserProfileDto organizationOnUserProfileDto : userProfileDto.getOrganizations().getValue()) {
                    boolean isExist = false;
                    for (OrganizationOnUserProfileDto thisOrganizationOnUserProfileDto : this.getOrganizations().getValue()) {
                        if (organizationOnUserProfileDto.getOrganizationId().equals(thisOrganizationOnUserProfileDto.getOrganizationId())) {
                            thisOrganizationOnUserProfileDto.merge(organizationOnUserProfileDto);
                            isExist = true;
                            break;
                        }
                    }
                    if (!isExist) {
                        this.getOrganizations().getValue().add(organizationOnUserProfileDto);
                    }
                }
            }
        }
        if (userProfileDto.getTeams() != null && userProfileDto.getTeams().isHasValue()) {
            if (this.getTeams() == null || !this.getTeams().isHasValue()) {
                this.setTeams(userProfileDto.getTeams());
            } else {
                for (TeamOnUserProfileDto teamOnUserProfileDto : userProfileDto.getTeams().getValue()) {
                    boolean isExist = false;
                    for (TeamOnUserProfileDto thisTeamOnUserProfileDto : this.getTeams().getValue()) {
                        if (teamOnUserProfileDto.getTeamId().equals(thisTeamOnUserProfileDto.getTeamId())) {
                            thisTeamOnUserProfileDto.merge(teamOnUserProfileDto);
                            isExist = true;
                            break;
                        }
                    }
                    if (!isExist) {
                        this.getTeams().getValue().add(teamOnUserProfileDto);
                    }
                }
            }
        }
        if (userProfileDto.getOwnedOrganizations() != null && userProfileDto.getOwnedOrganizations().isHasValue()) {
            if (this.getOwnedOrganizations() == null || !this.getOwnedOrganizations().isHasValue()) {
                this.setOwnedOrganizations(userProfileDto.getOwnedOrganizations());
            } else {
                for (OrganizationDto organizationDto : userProfileDto.getOwnedOrganizations().getValue()) {
                    boolean isExist = false;
                    for (OrganizationDto thisOrganizationDto : this.getOwnedOrganizations().getValue()) {
                        if (organizationDto.getOrganizationId().equals(thisOrganizationDto.getOrganizationId())) {
                            thisOrganizationDto.merge(organizationDto);
                            isExist = true;
                            break;
                        }
                    }
                    if (!isExist) {
                        this.getOwnedOrganizations().getValue().add(organizationDto);
                    }
                }
            }
        }
        if (userProfileDto.getChargeTasks() != null && userProfileDto.getChargeTasks().isHasValue()) {
            if (this.getChargeTasks() == null || !this.getChargeTasks().isHasValue()) {
                this.setChargeTasks(userProfileDto.getChargeTasks());
            } else {
                for (TaskDto taskDto : userProfileDto.getChargeTasks().getValue()) {
                    boolean isExist = false;
                    for (TaskDto thisTaskDto : this.getChargeTasks().getValue()) {
                        if (taskDto.getTaskId().equals(thisTaskDto.getTaskId())) {
                            thisTaskDto.merge(taskDto);
                            isExist = true;
                            break;
                        }
                    }
                    if (!isExist) {
                        this.getChargeTasks().getValue().add(taskDto);
                    }
                }
            }
        }

        return this;
    }

    @Override
    public UserProfileDto deepClone() {
        UserProfileDto cloned = (UserProfileDto) super.clone();
        if (userPreference != null){
            cloned.setUserPreference(userPreference.clone());
        }
        if (organizations != null){
            cloned.setOrganizations(organizations.clone());
        }
        if (teams != null){
            cloned.setTeams(teams.clone());
        }
        if (ownedOrganizations != null){
            cloned.setOwnedOrganizations(ownedOrganizations.clone());
        }
        if (chargeTasks != null){
            cloned.setChargeTasks(chargeTasks.clone());
        }
        return cloned;
    }
}
