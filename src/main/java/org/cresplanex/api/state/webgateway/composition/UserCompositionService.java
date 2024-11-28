package org.cresplanex.api.state.webgateway.composition;

import lombok.RequiredArgsConstructor;
import org.cresplanex.api.state.webgateway.dto.ListResponseDto;
import org.cresplanex.api.state.webgateway.dto.domain.DomainDto;
import org.cresplanex.api.state.webgateway.dto.domain.Relation;
import org.cresplanex.api.state.webgateway.dto.domain.team.TeamDto;
import org.cresplanex.api.state.webgateway.dto.domain.userpreference.UserPreferenceDto;
import org.cresplanex.api.state.webgateway.dto.domain.userprofile.UserProfileDto;
import org.cresplanex.api.state.webgateway.dto.domain.userprofile.UserProfileOnTeamDto;
import org.cresplanex.api.state.webgateway.mapper.UserProfileMapper;
import org.cresplanex.api.state.webgateway.proxy.query.TeamQueryProxy;
import org.cresplanex.api.state.webgateway.proxy.query.UserPreferenceQueryProxy;
import org.cresplanex.api.state.webgateway.proxy.query.UserProfileQueryProxy;
import org.cresplanex.api.state.webgateway.retriever.RelationRetrieverBuilder;
import org.cresplanex.api.state.webgateway.retriever.RootRelationRetriever;
import org.cresplanex.api.state.webgateway.retriever.RootRelationRetrieverBuilder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserCompositionService {

    private final TeamQueryProxy teamQueryProxy;
    private final UserProfileQueryProxy userProfileQueryProxy;
    private final UserPreferenceQueryProxy userPreferenceQueryProxy;

    // 1
    public ListResponseDto.InternalData<UserProfileDto> getUsers(
            String operatorId,
            int limit,
            int offset,
            String cursor,
            String pagination,
            String sortField,
            String sortOrder,
            boolean withCount
    ) {
        return userProfileQueryProxy.getUserProfiles(
                operatorId,
                limit,
                offset,
                cursor,
                pagination,
                sortField,
                sortOrder,
                withCount
        );
    }

    // 2
    public ListResponseDto.InternalData<UserProfileDto> getUsersWithPreference(
            String operatorId,
            int limit,
            int offset,
            String cursor,
            String pagination,
            String sortField,
            String sortOrder,
            boolean withCount
    ) {
        ListResponseDto.InternalData<UserProfileDto> userProfileDto = userProfileQueryProxy.getUserProfiles(
                operatorId,
                limit,
                offset,
                cursor,
                pagination,
                sortField,
                sortOrder,
                withCount
        );


        RootRelationRetriever<UserProfileDto> userProfileRetriever = RootRelationRetrieverBuilder.<UserProfileDto>builder()
                .idRetriever(UserProfileDto::getUserProfileId)
                .chain(
                        RelationRetrieverBuilder.<UserProfileDto>builder()
                                .idRetriever(UserProfileDto::getUserId)
                                .relationRetriever(userProfileDto.getListData().
                                .chain(null)
                                .build()
                )
                .build();

        ForUserPreferenceUtils.attachUserPreferenceToUserProfile(
                userPreferenceQueryProxy,
                operatorId,
                userProfileDto
        );

        return userProfileDto;
    }

    // 2
    public ListResponseDto.InternalData<UserProfileOnTeamDto> getUsersOnTeam(
            String operatorId,
            String teamId,
            int limit,
            int offset,
            String cursor,
            String pagination,
            String sortField,
            String sortOrder,
            boolean withCount
    ) {
        ListResponseDto.InternalData<UserProfileOnTeamDto> users = teamQueryProxy.getUsersOnTeam(
                operatorId,
                teamId,
                limit,
                offset,
                cursor,
                pagination,
                sortField,
                sortOrder,
                withCount
        );

        ForUserProfileUtils.attachUserProfileToUserProfile(
                userProfileQueryProxy,
                operatorId,
                users
        );

        return users;
    }
}
