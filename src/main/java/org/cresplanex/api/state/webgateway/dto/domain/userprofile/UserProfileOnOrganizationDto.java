package org.cresplanex.api.state.webgateway.dto.domain.userprofile;

import lombok.*;
import org.cresplanex.api.state.webgateway.dto.domain.DeepCloneable;
import org.cresplanex.api.state.webgateway.dto.domain.OverMerge;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
public class UserProfileOnOrganizationDto extends UserProfileDto implements OverMerge<UserProfileDto, UserProfileOnOrganizationDto>, DeepCloneable {

    public UserProfileOnOrganizationDto(UserProfileDto userProfileDto) {
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

    public UserProfileOnOrganizationDto(UserProfileOnOrganizationDto userProfileOnOrganizationDto) {
        super(
                userProfileOnOrganizationDto.getUserProfileId(),
                userProfileOnOrganizationDto.getUserId(),
                userProfileOnOrganizationDto.getName(),
                userProfileOnOrganizationDto.getEmail(),
                userProfileOnOrganizationDto.getGivenName(),
                userProfileOnOrganizationDto.getFamilyName(),
                userProfileOnOrganizationDto.getMiddleName(),
                userProfileOnOrganizationDto.getNickname(),
                userProfileOnOrganizationDto.getProfile(),
                userProfileOnOrganizationDto.getPicture(),
                userProfileOnOrganizationDto.getWebsite(),
                userProfileOnOrganizationDto.getPhone(),
                userProfileOnOrganizationDto.getGender(),
                userProfileOnOrganizationDto.getBirthdate(),
                userProfileOnOrganizationDto.getZoneinfo(),
                userProfileOnOrganizationDto.getLocale(),
                userProfileOnOrganizationDto.getUserPreference(),
                userProfileOnOrganizationDto.getOrganizations(),
                userProfileOnOrganizationDto.getTeams(),
                userProfileOnOrganizationDto.getOwnedOrganizations(),
                userProfileOnOrganizationDto.getChargeTasks()
        );
    }

    public UserProfileOnOrganizationDto(UserProfileDto userProfileDto, UserProfileOnOrganizationDto userProfileOnOrganizationDto) {
        this(userProfileDto);
    }

    @Override
    public UserProfileOnOrganizationDto overMerge(UserProfileDto userProfileDto) {
        return new UserProfileOnOrganizationDto(userProfileDto, this);
    }

    @Override
    public UserProfileOnOrganizationDto deepClone() {
        try {
            return (UserProfileOnOrganizationDto) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
