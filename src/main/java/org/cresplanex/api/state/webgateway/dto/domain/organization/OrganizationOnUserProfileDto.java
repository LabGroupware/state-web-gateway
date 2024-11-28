package org.cresplanex.api.state.webgateway.dto.domain.organization;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.cresplanex.api.state.webgateway.dto.domain.DeepCloneable;
import org.cresplanex.api.state.webgateway.dto.domain.OverMerge;
import org.cresplanex.api.state.webgateway.dto.domain.Relation;
import org.cresplanex.api.state.webgateway.dto.domain.userpreference.UserPreferenceDto;
import org.cresplanex.api.state.webgateway.dto.domain.userprofile.UserProfileDto;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OrganizationOnUserProfileDto extends OrganizationDto implements OverMerge<OrganizationDto, OrganizationOnUserProfileDto>, DeepCloneable {

    public OrganizationOnUserProfileDto(OrganizationDto organizationDto) {
        super(
                organizationDto.getOrganizationId(),
                organizationDto.getOwnerId(),
                organizationDto.getName(),
                organizationDto.getPlan(),
                organizationDto.getSiteUrl(),
                organizationDto.getUsers(),
                organizationDto.getOwner(),
                organizationDto.getTeams()
        );
    }

    public OrganizationOnUserProfileDto(OrganizationOnUserProfileDto organizationOnUserProfileDto) {
        super(
                organizationOnUserProfileDto.getOrganizationId(),
                organizationOnUserProfileDto.getOwnerId(),
                organizationOnUserProfileDto.getName(),
                organizationOnUserProfileDto.getPlan(),
                organizationOnUserProfileDto.getSiteUrl(),
                organizationOnUserProfileDto.getUsers(),
                organizationOnUserProfileDto.getOwner(),
                organizationOnUserProfileDto.getTeams()
        );
    }

    public OrganizationOnUserProfileDto(OrganizationDto organizationDto, OrganizationOnUserProfileDto organizationOnUserProfileDto) {
        this(organizationDto);
    }

    @Override
    public OrganizationOnUserProfileDto overMerge(OrganizationDto organizationDto) {
        return new OrganizationOnUserProfileDto(organizationDto, this);
    }

    @Override
    public OrganizationOnUserProfileDto deepClone() {
        try {
            return (OrganizationOnUserProfileDto) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
