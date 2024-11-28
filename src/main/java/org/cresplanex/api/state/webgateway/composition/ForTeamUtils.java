package org.cresplanex.api.state.webgateway.composition;

import org.cresplanex.api.state.webgateway.dto.ListResponseDto;
import org.cresplanex.api.state.webgateway.dto.domain.team.TeamDto;
import org.cresplanex.api.state.webgateway.proxy.query.TeamQueryProxy;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ForTeamUtils {

    public static Map<String, TeamDto> getStringTeamDtoMap(TeamQueryProxy teamQueryProxy, String operatorId, Set<String> teamIds) {

        ListResponseDto.InternalData<TeamDto> teams = teamQueryProxy.getPluralTeams(
                operatorId,
                List.copyOf(teamIds),
                null,
                null
        );

        return teams.getListData().stream()
                .collect(Collectors.toMap(TeamDto::getTeamId, team -> team));
    }
}
