package org.cresplanex.api.state.webgateway.composition;

import lombok.RequiredArgsConstructor;
import org.cresplanex.api.state.webgateway.dto.domain.ListRelation;
import org.cresplanex.api.state.webgateway.dto.domain.Relation;
import org.cresplanex.api.state.webgateway.dto.domain.organization.OrganizationDto;
import org.cresplanex.api.state.webgateway.dto.domain.team.TeamDto;
import org.cresplanex.api.state.webgateway.dto.domain.userprofile.UserProfileDto;
import org.cresplanex.api.state.webgateway.dto.domain.userprofile.UserProfileOnOrganizationDto;
import org.cresplanex.api.state.webgateway.proxy.query.TeamQueryProxy;
import org.cresplanex.api.state.webgateway.proxy.query.UserProfileQueryProxy;
import org.cresplanex.api.state.webgateway.retriever.RetrievedCacheContainer;
import org.cresplanex.api.state.webgateway.retriever.domain.OrganizationRetriever;
import org.cresplanex.api.state.webgateway.retriever.domain.TeamRetriever;
import org.cresplanex.api.state.webgateway.retriever.domain.UserProfileRetriever;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class AttachRelationOrganization {

    private final UserProfileQueryProxy userProfileQueryProxy;
    private final TeamQueryProxy teamQueryProxy;

    public void attach(String operatorId, RetrievedCacheContainer cache, OrganizationRetriever retriever, OrganizationDto organizationDto) {

        // Owner
        if (retriever.getOwnerRelationRetriever() != null) {
            // 取得が必要なIDの取得
            String ownerId = retriever.getOwnerRelationRetriever().getIdRetriever().apply(organizationDto);

            Map<String, UserProfileDto> userProfileDtoMap = UserProfileCompositionHelper.createUserProfileDtoMap(
                    userProfileQueryProxy,
                    cache,
                    operatorId,
                    List.of(ownerId),
                    retriever.getOwnerRelationRetriever().getChain()
            );

            this.attachRelationToOwner(
                    operatorId,
                    cache,
                    organizationDto,
                    userProfileDtoMap,
                    ownerId,
                    retriever.getOwnerRelationRetriever().getRelationRetriever().apply(organizationDto),
                    retriever.getOwnerRelationRetriever().getChain()
            );
        }

        // Users
        if (retriever.getUsersRelationRetriever() != null) {
            Map<String, UserProfileDto> userProfileDtoMap = UserProfileCompositionHelper.createUserProfileDtoMap(
                    userProfileQueryProxy,
                    cache,
                    operatorId,
                    retriever.getUsersRelationRetriever().getIdRetriever().apply(organizationDto),
                    retriever.getUsersRelationRetriever().getChain()
            );

            this.attachRelationToUsers(
                    operatorId,
                    cache,
                    organizationDto,
                    userProfileDtoMap,
                    retriever.getUsersRelationRetriever().getRelationRetriever().apply(organizationDto),
                    retriever.getUsersRelationRetriever().getChain()
            );
        }

        // teams
        if (retriever.getTeamsRelationRetriever() != null) {
            TeamCompositionHelper.preAttachToOrganization(
                    teamQueryProxy,
                    cache,
                    operatorId,
                    List.of(organizationDto)
            );

            List<String> teamIds = retriever.getTeamsRelationRetriever().getIdRetriever().apply(organizationDto);

            Map<String, TeamDto> teamDtoMap = TeamCompositionHelper.createTeamDtoMap(
                    teamQueryProxy,
                    cache,
                    operatorId,
                    teamIds,
                    retriever.getTeamsRelationRetriever().getChain()
            );

            this.attachRelationToTeams(
                    operatorId,
                    cache,
                    organizationDto,
                    teamDtoMap,
                    retriever.getTeamsRelationRetriever().getRelationRetriever().apply(organizationDto),
                    retriever.getTeamsRelationRetriever().getChain()
            );
        }
    }

    public void attach(String operatorId, RetrievedCacheContainer cache, OrganizationRetriever retriever, List<OrganizationDto> organizationDto) {

        // Owner
        if (retriever.getOwnerRelationRetriever() != null) {
            Map<String, UserProfileDto> userProfileDtoMap = UserProfileCompositionHelper.createUserProfileDtoMap(
                    userProfileQueryProxy,
                    cache,
                    operatorId,
                    organizationDto.stream()
                            .map(retriever.getOwnerRelationRetriever().getIdRetriever())
                            .toList(),
                    retriever.getOwnerRelationRetriever().getChain()
            );

            for (OrganizationDto dto : organizationDto) {
                this.attachRelationToOwner(
                        operatorId,
                        cache,
                        dto,
                        userProfileDtoMap,
                        retriever.getOwnerRelationRetriever().getIdRetriever().apply(dto),
                        retriever.getOwnerRelationRetriever().getRelationRetriever().apply(dto),
                        retriever.getOwnerRelationRetriever().getChain()
                );
            }
        }

        // Users
        if (retriever.getUsersRelationRetriever() != null) {
            Map<String, UserProfileDto> userProfileDtoMap = UserProfileCompositionHelper.createUserProfileDtoMap(
                    userProfileQueryProxy,
                    cache,
                    operatorId,
                    organizationDto.stream()
                            .map(retriever.getUsersRelationRetriever().getIdRetriever())
                            .flatMap(List::stream)
                            .toList(),
                    retriever.getUsersRelationRetriever().getChain()
            );

            for (OrganizationDto dto : organizationDto) {
                this.attachRelationToUsers(
                        operatorId,
                        cache,
                        dto,
                        userProfileDtoMap,
                        retriever.getUsersRelationRetriever().getRelationRetriever().apply(dto),
                        retriever.getUsersRelationRetriever().getChain()
                );
            }
        }

        // teams
        if (retriever.getTeamsRelationRetriever() != null) {
            TeamCompositionHelper.preAttachToOrganization(
                    teamQueryProxy,
                    cache,
                    operatorId,
                    organizationDto
            );

            Map<String, TeamDto> teamDtoMap = TeamCompositionHelper.createTeamDtoMap(
                    teamQueryProxy,
                    cache,
                    operatorId,
                    organizationDto.stream()
                            .map(retriever.getTeamsRelationRetriever().getIdRetriever())
                            .flatMap(List::stream)
                            .toList(),
                    retriever.getTeamsRelationRetriever().getChain()
            );

            for (OrganizationDto dto : organizationDto) {
                this.attachRelationToTeams(
                        operatorId,
                        cache,
                        dto,
                        teamDtoMap,
                        retriever.getTeamsRelationRetriever().getRelationRetriever().apply(dto),
                        retriever.getTeamsRelationRetriever().getChain()
                );
            }
        }
    }

    protected void attachRelationToOwner(
            String operatorId,
            RetrievedCacheContainer cache,
            OrganizationDto organizationDto,
            Map<String, UserProfileDto> userProfileDtoMap,
            String ownerId,
            Relation<UserProfileDto> ownerRelation,
            List<UserProfileRetriever> retrievers
    ) {
        organizationDto.setOwner(Relation.<UserProfileDto>builder()
                .hasValue(true)
                .value(userProfileDtoMap.get(ownerId))
                .build()
        );
        retrievers.forEach(retriever -> {
            if (retriever != null && ownerRelation.isHasValue() && ownerRelation.getValue() != null) {
                // TODO: Implement this
            }
        });
    }

    protected void attachRelationToUsers(
            String operatorId,
            RetrievedCacheContainer cache,
            OrganizationDto organizationDto,
            Map<String, UserProfileDto> userProfileDtoMap,
            ListRelation<UserProfileOnOrganizationDto> usersRelation,
            List<UserProfileRetriever> retrievers
    ) {
        if(organizationDto.getUsers().isHasValue()) {
            List<UserProfileOnOrganizationDto> originAttachUserProfiles = organizationDto.getUsers().getValue();
            List<String> attachUserIds = originAttachUserProfiles
                    .stream()
                    .map(UserProfileOnOrganizationDto::getUserId)
                    .toList();
            List<UserProfileDto> attachUserProfiles = attachUserIds.stream()
                    .map(userProfileDtoMap::get)
                    .toList();
            organizationDto.setUsers(ListRelation.<UserProfileOnOrganizationDto>builder()
                    .value(
                            attachUserProfiles.stream()
                                    .map(user -> new UserProfileOnOrganizationDto(user, originAttachUserProfiles.stream()
                                            .filter(origin -> origin.getUserId().equals(user.getUserId()))
                                            .findFirst()
                                            .orElse(null)))
                                    .toList()
                    )
                    .build()
            );
            retrievers.forEach(retriever -> {
                if (retriever != null && usersRelation.isHasValue() && usersRelation.getValue() != null) {
                    // TODO: Implement this
                }
            });
        }
    }

    protected void attachRelationToTeams(
            String operatorId,
            RetrievedCacheContainer cache,
            OrganizationDto organizationDto,
            Map<String, TeamDto> teamDtoMap,
            ListRelation<TeamDto> teamsRelation,
            List<TeamRetriever> retrievers
    ) {
        if(organizationDto.getTeams().isHasValue()) {
            List<TeamDto> originAttachTeams = organizationDto.getTeams().getValue();
            List<String> attachTeamIds = originAttachTeams
                    .stream()
                    .map(TeamDto::getTeamId)
                    .toList();
            List<TeamDto> attachTeams = attachTeamIds.stream()
                    .map(teamDtoMap::get)
                    .toList();
            organizationDto.setTeams(ListRelation.<TeamDto>builder()
                    .value(attachTeams)
                    .build()
            );
            retrievers.forEach(retriever -> {
                if (retriever != null && teamsRelation.isHasValue() && teamsRelation.getValue() != null) {
                    // TODO: Implement this
                }
            });
        }
    }
}
