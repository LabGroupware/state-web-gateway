package org.cresplanex.api.state.webgateway.dto.domain.team;

import lombok.*;
import org.cresplanex.api.state.webgateway.dto.domain.DeepCloneable;
import org.cresplanex.api.state.webgateway.dto.domain.OverMerge;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
public class TeamOnUserProfileDto extends TeamDto implements OverMerge<TeamDto, TeamOnUserProfileDto>, DeepCloneable {

    public TeamOnUserProfileDto(TeamDto teamDto) {
        super(
                teamDto.getTeamId(),
                teamDto.getOrganizationId(),
                teamDto.getName(),
                teamDto.getDescription(),
                teamDto.isDefault(),
                teamDto.getUsers(),
                teamDto.getOrganization(),
                teamDto.getTasks()
        );
    }

    public TeamOnUserProfileDto(TeamOnUserProfileDto teamOnUserProfileDto) {
        super(
                teamOnUserProfileDto.getTeamId(),
                teamOnUserProfileDto.getOrganizationId(),
                teamOnUserProfileDto.getName(),
                teamOnUserProfileDto.getDescription(),
                teamOnUserProfileDto.isDefault(),
                teamOnUserProfileDto.getUsers(),
                teamOnUserProfileDto.getOrganization(),
                teamOnUserProfileDto.getTasks()
        );
    }

    public TeamOnUserProfileDto(TeamDto teamDto, TeamOnUserProfileDto teamOnUserProfileDto) {
        this(teamDto);
    }

    @Override
    public TeamOnUserProfileDto overMerge(TeamDto teamDto) {
        return new TeamOnUserProfileDto(teamDto, this);
    }

    @Override
    public TeamOnUserProfileDto deepClone() {
        return (TeamOnUserProfileDto) super.clone();
    }
}
