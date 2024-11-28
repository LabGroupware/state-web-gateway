package org.cresplanex.api.state.webgateway.mapper;

import build.buf.gen.organization.v1.Organization;
import build.buf.gen.organization.v1.OrganizationOnUser;
import build.buf.gen.organization.v1.OrganizationWithUsers;
import org.cresplanex.api.state.webgateway.dto.domain.ListRelation;
import org.cresplanex.api.state.webgateway.dto.domain.Relation;
import org.cresplanex.api.state.webgateway.dto.domain.organization.OrganizationDto;
import org.cresplanex.api.state.webgateway.dto.domain.organization.OrganizationOnUserProfileDto;
import org.cresplanex.api.state.webgateway.dto.domain.team.TeamDto;
import org.cresplanex.api.state.webgateway.dto.domain.userprofile.UserProfileDto;
import org.cresplanex.api.state.webgateway.dto.domain.userprofile.UserProfileOnOrganizationDto;

import java.util.List;

public class OrganizationMapper {

    public static OrganizationDto convert(Organization organization) {
        return OrganizationDto.builder()
                .organizationId(organization.getOrganizationId())
                .name(organization.getName())
                .ownerId(organization.getOwnerId())
                .plan(organization.getPlan())
                .siteUrl(organization.getSiteUrl().getHasValue() ? organization.getSiteUrl().getValue() : null)
                .users(ListRelation.<UserProfileOnOrganizationDto>builder().hasValue(false).build())
                .owner(Relation.<UserProfileDto>builder().hasValue(false).build())
                .teams(ListRelation.<TeamDto>builder().hasValue(false).build())
                .build();
    }


    public static OrganizationDto convertFromOrganizationId(String organizationId) {
        return OrganizationDto.builder()
                .organizationId(organizationId)
                .build();
    }

    public static OrganizationDto convert(OrganizationWithUsers organization) {
        return OrganizationDto.builder()
                .organizationId(organization.getOrganization().getOrganizationId())
                .name(organization.getOrganization().getName())
                .ownerId(organization.getOrganization().getOwnerId())
                .plan(organization.getOrganization().getPlan())
                .siteUrl(organization.getOrganization().getSiteUrl().getHasValue() ? organization.getOrganization().getSiteUrl().getValue() : null)
                .users(ListRelation.<UserProfileOnOrganizationDto>builder().hasValue(true).value(
                        organization.getUsersList().stream()
                                .map(UserProfileMapper::convert)
                                .toList()
                ).build())
                .owner(Relation.<UserProfileDto>builder().hasValue(false).build())
                .teams(ListRelation.<TeamDto>builder().hasValue(false).build())
                .build();
    }

    public static OrganizationOnUserProfileDto convert(OrganizationOnUser organizationOnUser) {
        return new OrganizationOnUserProfileDto(
                convert(organizationOnUser.getOrganization())
        );
    }
}
