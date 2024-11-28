package org.cresplanex.api.state.webgateway.composition;

import lombok.RequiredArgsConstructor;
import org.cresplanex.api.state.webgateway.dto.ListResponseDto;
import org.cresplanex.api.state.webgateway.dto.domain.team.TeamDto;
import org.cresplanex.api.state.webgateway.dto.domain.userprofile.UserProfileDto;
import org.cresplanex.api.state.webgateway.proxy.query.TeamQueryProxy;
import org.cresplanex.api.state.webgateway.proxy.query.UserPreferenceQueryProxy;
import org.cresplanex.api.state.webgateway.proxy.query.UserProfileQueryProxy;
import org.cresplanex.api.state.webgateway.retriever.RelationRetrieverBuilder;
import org.cresplanex.api.state.webgateway.retriever.RootRelationRetriever;
import org.cresplanex.api.state.webgateway.retriever.RootRelationRetrieverBuilder;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class TeamCompositionService {

    private final TeamQueryProxy teamQueryProxy;
    private final UserProfileQueryProxy userProfileQueryProxy;
    private final UserPreferenceQueryProxy userPreferenceQueryProxy;

    // 1
    public ListResponseDto.InternalData<TeamDto> getTeamsOnOrganization(
            String operatorId,
            String organizationId,
            int limit,
            int offset,
            String cursor,
            String pagination,
            String sortField,
            String sortOrder,
            boolean withCount,
            boolean hasIsDefaultFilter,
            boolean filterIsDefault,
            boolean hasUserFilter,
            List<String> filterUserIds,
            String userFilterType
    ) {
        return teamQueryProxy.getTeams(
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
                true,
                List.of(organizationId),
                hasUserFilter,
                filterUserIds,
                userFilterType
        );
    }

    // 2
    public ListResponseDto.InternalData<TeamDto> getTeamsWithUsers(
            String operatorId,
            String organizationId,
            int limit,
            int offset,
            String cursor,
            String pagination,
            String sortField,
            String sortOrder,
            boolean withCount,
            boolean hasIsDefaultFilter,
            boolean filterIsDefault,
            boolean hasUserFilter,
            List<String> filterUserIds,
            String userFilterType
    ) {
        ListResponseDto.InternalData<TeamDto> teams = teamQueryProxy.getTeamsWithUsers(
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
                true,
                List.of(organizationId),
                hasUserFilter,
                filterUserIds,
                userFilterType
        );

        ForUserProfileUtils.attachUserProfilesToTeam(
                userProfileQueryProxy,
                operatorId,
                teams
        );

//        ForUserPreferenceUtils.attachUserPreferenceToTeamUsers(
//                userPreferenceQueryProxy,
//                operatorId,
//                teams
//        );

        return teams;
    }
}
