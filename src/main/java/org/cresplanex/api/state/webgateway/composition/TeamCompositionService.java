package org.cresplanex.api.state.webgateway.composition;

import lombok.RequiredArgsConstructor;
import org.cresplanex.api.state.webgateway.composition.attach.AttachRelationTeam;
import org.cresplanex.api.state.webgateway.composition.helper.TeamCompositionHelper;
import org.cresplanex.api.state.webgateway.dto.ListResponseDto;
import org.cresplanex.api.state.webgateway.dto.domain.team.TeamDto;
import org.cresplanex.api.state.webgateway.proxy.query.TeamQueryProxy;
import org.cresplanex.api.state.webgateway.retriever.RetrievedCacheContainer;
import org.cresplanex.api.state.webgateway.retriever.domain.TeamRetriever;
import org.cresplanex.api.state.webgateway.retriever.resolver.TeamRetrieveResolver;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class TeamCompositionService {

    private final TeamQueryProxy teamQueryProxy;
    private final AttachRelationTeam attachRelationTeam;

    public TeamDto findTeam(String operatorId, String teamId, List<String> with) {
        TeamDto team;
        TeamRetriever teamRetriever = TeamRetrieveResolver.resolve(
                with.toArray(new String[0])
        );
        int need = TeamCompositionHelper.calculateNeedQuery(List.of(teamRetriever));
        switch (need) {
            case TeamCompositionHelper.GET_TEAM_WITH_USERS:
                team = teamQueryProxy.findTeamWithUsers(
                        operatorId,
                        teamId
                );
                break;
            default:
                team = teamQueryProxy.findTeam(
                        operatorId,
                        teamId
                );
                break;
        }
        RetrievedCacheContainer cache = new RetrievedCacheContainer();
        attachRelationTeam.attach(
                operatorId,
                cache,
                teamRetriever,
                team
        );

        return team;
    }

    public ListResponseDto.InternalData<TeamDto> getTeams(
            String operatorId,
            int limit,
            int offset,
            String cursor,
            String pagination,
            String sortField,
            String sortOrder,
            boolean withCount,
            boolean hasIsDefaultFilter,
            boolean filterIsDefault,
            boolean hasOrganizationFilter,
            List<String> filterOrganizationIds,
            boolean hasUserFilter,
            List<String> filterUserIds,
            String userFilterType,
            List<String> with
    ) {
        ListResponseDto.InternalData<TeamDto> teams;
        TeamRetriever teamRetriever = TeamRetrieveResolver.resolve(
                with.toArray(new String[0])
        );
        int need = TeamCompositionHelper.calculateNeedQuery(List.of(teamRetriever));
        switch (need) {
            case TeamCompositionHelper.GET_TEAM_WITH_USERS:
                teams = teamQueryProxy.getTeamsWithUsers(
                        operatorId,
                        limit,
                        offset,
                        cursor,
                        pagination,
                        sortField,
                        sortOrder,
                        withCount,
                        hasIsDefaultFilter,
                        filterIsDefault,
                        hasOrganizationFilter,
                        filterOrganizationIds,
                        hasUserFilter,
                        filterUserIds,
                        userFilterType
                );
                break;
            default:
                teams = teamQueryProxy.getTeams(
                        operatorId,
                        limit,
                        offset,
                        cursor,
                        pagination,
                        sortField,
                        sortOrder,
                        withCount,
                        hasIsDefaultFilter,
                        filterIsDefault,
                        hasOrganizationFilter,
                        filterOrganizationIds,
                        hasUserFilter,
                        filterUserIds,
                        userFilterType
                );
                break;
        }
        RetrievedCacheContainer cache = new RetrievedCacheContainer();
        attachRelationTeam.attach(
                operatorId,
                cache,
                teamRetriever,
                teams.getListData()
        );

        return teams;
    }
}
