package org.cresplanex.api.state.webgateway.mapper;

import build.buf.gen.organization.v1.OrganizationOnUser;
import build.buf.gen.team.v1.Team;
import build.buf.gen.team.v1.TeamOnUser;
import build.buf.gen.team.v1.TeamWithUsers;
import org.cresplanex.api.state.webgateway.dto.domain.ListRelation;
import org.cresplanex.api.state.webgateway.dto.domain.Relation;
import org.cresplanex.api.state.webgateway.dto.domain.organization.OrganizationDto;
import org.cresplanex.api.state.webgateway.dto.domain.team.TeamDto;
import org.cresplanex.api.state.webgateway.dto.domain.team.TeamOnUserProfileDto;
import org.cresplanex.api.state.webgateway.dto.domain.userprofile.UserProfileOnTeamDto;

import java.util.List;

public class TeamMapper {

    public static TeamDto convert(Team team) {
        return TeamDto.builder()
                .teamId(team.getTeamId())
                .name(team.getName())
                .description(team.getDescription().getHasValue() ? team.getDescription().getValue() : null)
                .isDefault(team.getIsDefault())
                .organization(Relation.<OrganizationDto>builder().hasValue(false).build())
                .users(ListRelation.<UserProfileOnTeamDto>builder().hasValue(false).build())
                .build();
    }

    public static TeamDto convertFromTeamId(String teamId) {
        return TeamDto.builder()
                .teamId(teamId)
                .build();
    }


    public static TeamDto convert(TeamWithUsers team) {
        return TeamDto.builder()
                .teamId(team.getTeam().getTeamId())
                .name(team.getTeam().getName())
                .description(team.getTeam().getDescription().getHasValue() ? team.getTeam().getDescription().getValue() : null)
                .isDefault(team.getTeam().getIsDefault())
                .organization(Relation.<OrganizationDto>builder().hasValue(false).build())
                .users(ListRelation.<UserProfileOnTeamDto>builder().hasValue(true).value(
                        team.getUsersList().stream()
                                .map(UserProfileMapper::convert)
                                .toList()
                ).build())
                .build();
    }

    public static TeamOnUserProfileDto convert(TeamOnUser teamOnUser) {
        return new TeamOnUserProfileDto(
                convert(teamOnUser.getTeam())
        );
    }
}
