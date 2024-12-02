package org.cresplanex.api.state.webgateway.dto.domain.userprofile;

import lombok.*;
import org.cresplanex.api.state.webgateway.dto.domain.DeepCloneable;
import org.cresplanex.api.state.webgateway.dto.domain.OverMerge;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
public class UserProfileOnTeamDto extends UserProfileDto implements OverMerge<UserProfileDto, UserProfileOnTeamDto>, DeepCloneable {

    public UserProfileOnTeamDto(UserProfileDto userProfileDto) {
        super(
                userProfileDto.getUserProfileId(),
                userProfileDto.getUserId(),
                userProfileDto.getName(),
                userProfileDto.getEmail(),
                userProfileDto.getGivenName(),
                userProfileDto.getFamilyName(),
                userProfileDto.getMiddleName(),
                userProfileDto.getNickname(),
                userProfileDto.getProfile(),
                userProfileDto.getPicture(),
                userProfileDto.getWebsite(),
                userProfileDto.getPhone(),
                userProfileDto.getGender(),
                userProfileDto.getBirthdate(),
                userProfileDto.getZoneinfo(),
                userProfileDto.getLocale(),
                userProfileDto.getUserPreference(),
                userProfileDto.getOrganizations(),
                userProfileDto.getTeams(),
                userProfileDto.getOwnedOrganizations(),
                userProfileDto.getChargeTasks()
        );
    }

    public UserProfileOnTeamDto(UserProfileOnTeamDto userProfileOnTeamDto) {
        super(
                userProfileOnTeamDto.getUserProfileId(),
                userProfileOnTeamDto.getUserId(),
                userProfileOnTeamDto.getName(),
                userProfileOnTeamDto.getEmail(),
                userProfileOnTeamDto.getGivenName(),
                userProfileOnTeamDto.getFamilyName(),
                userProfileOnTeamDto.getMiddleName(),
                userProfileOnTeamDto.getNickname(),
                userProfileOnTeamDto.getProfile(),
                userProfileOnTeamDto.getPicture(),
                userProfileOnTeamDto.getWebsite(),
                userProfileOnTeamDto.getPhone(),
                userProfileOnTeamDto.getGender(),
                userProfileOnTeamDto.getBirthdate(),
                userProfileOnTeamDto.getZoneinfo(),
                userProfileOnTeamDto.getLocale(),
                userProfileOnTeamDto.getUserPreference(),
                userProfileOnTeamDto.getOrganizations(),
                userProfileOnTeamDto.getTeams(),
                userProfileOnTeamDto.getOwnedOrganizations(),
                userProfileOnTeamDto.getChargeTasks()
        );
    }

    public UserProfileOnTeamDto(UserProfileDto userProfileDto, UserProfileOnTeamDto userProfileOnTeamDto) {
        this(userProfileDto);
    }

    @Override
    public UserProfileOnTeamDto overMerge(UserProfileDto userProfileDto) {
        return new UserProfileOnTeamDto(userProfileDto, this);
    }

    @Override
    public UserProfileOnTeamDto deepClone() {
        return (UserProfileOnTeamDto) super.clone();
    }
}
