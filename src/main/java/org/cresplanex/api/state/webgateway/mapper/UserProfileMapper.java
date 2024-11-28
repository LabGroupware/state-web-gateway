package org.cresplanex.api.state.webgateway.mapper;

import build.buf.gen.organization.v1.UserOnOrganization;
import build.buf.gen.team.v1.UserOnTeam;
import build.buf.gen.userprofile.v1.UserProfile;
import org.cresplanex.api.state.webgateway.dto.domain.Relation;
import org.cresplanex.api.state.webgateway.dto.domain.userpreference.UserPreferenceDto;
import org.cresplanex.api.state.webgateway.dto.domain.userprofile.UserProfileDto;
import org.cresplanex.api.state.webgateway.dto.domain.userprofile.UserProfileOnOrganizationDto;
import org.cresplanex.api.state.webgateway.dto.domain.userprofile.UserProfileOnTeamDto;

public class UserProfileMapper {

    public static UserProfileDto convert(UserProfile userprofile) {
        return UserProfileDto.builder()
                .userProfileId(userprofile.getUserProfileId())
                .name(userprofile.getName())
                .userId(userprofile.getUserId())
                .email(userprofile.getEmail())
                .nickname(userprofile.getNickname().getHasValue() ? userprofile.getNickname().getValue() : null)
                .birthdate(userprofile.getBirthdate().getHasValue() ? userprofile.getBirthdate().getValue() : null)
                .familyName(userprofile.getFamilyName().getHasValue() ? userprofile.getFamilyName().getValue() : null)
                .givenName(userprofile.getGivenName().getHasValue() ? userprofile.getGivenName().getValue() : null)
                .middleName(userprofile.getMiddleName().getHasValue() ? userprofile.getMiddleName().getValue() : null)
                .gender(userprofile.getGender().getHasValue() ? userprofile.getGender().getValue() : null)
                .locale(userprofile.getLocale().getHasValue() ? userprofile.getLocale().getValue() : null)
                .phone(userprofile.getPhone().getHasValue() ? userprofile.getPhone().getValue() : null)
                .profile(userprofile.getProfile().getHasValue() ? userprofile.getProfile().getValue() : null)
                .picture(userprofile.getPicture().getHasValue() ? userprofile.getPicture().getValue() : null)
                .website(userprofile.getWebsite().getHasValue() ? userprofile.getWebsite().getValue() : null)
                .zoneinfo(userprofile.getZoneinfo().getHasValue() ? userprofile.getZoneinfo().getValue() : null)
                .userPreference(Relation.<UserPreferenceDto>builder().hasValue(false).build())
                .build();
    }

    public static UserProfileDto convertFromUserProfileId(String userProfileId) {
        return UserProfileDto.builder()
                .userProfileId(userProfileId)
                .build();
    }

    public static UserProfileDto convertFromUserId(String userId) {
        return UserProfileDto.builder()
                .userId(userId)
                .build();
    }

    public static UserProfileOnOrganizationDto convert(UserOnOrganization userOnOrganization) {
        UserProfileOnOrganizationDto dto = new UserProfileOnOrganizationDto();
        dto.setUserId(userOnOrganization.getUserId());
        return dto;
    }

    public static UserProfileOnTeamDto convert(UserOnTeam userOnTeam) {
        UserProfileOnTeamDto dto = new UserProfileOnTeamDto();
        dto.setUserId(userOnTeam.getUserId());
        return dto;
    }
}
