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
