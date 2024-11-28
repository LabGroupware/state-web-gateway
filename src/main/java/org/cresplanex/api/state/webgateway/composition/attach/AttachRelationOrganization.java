package org.cresplanex.api.state.webgateway.composition.attach;

import lombok.RequiredArgsConstructor;
import org.cresplanex.api.state.webgateway.composition.helper.TeamCompositionHelper;
import org.cresplanex.api.state.webgateway.composition.helper.UserProfileCompositionHelper;
import org.cresplanex.api.state.webgateway.dto.domain.ListRelation;
import org.cresplanex.api.state.webgateway.dto.domain.Relation;
import org.cresplanex.api.state.webgateway.dto.domain.organization.OrganizationDto;
import org.cresplanex.api.state.webgateway.dto.domain.team.TeamDto;
import org.cresplanex.api.state.webgateway.dto.domain.userprofile.UserProfileDto;
import org.cresplanex.api.state.webgateway.dto.domain.userprofile.UserProfileOnOrganizationDto;
import org.cresplanex.api.state.webgateway.proxy.query.*;
import org.cresplanex.api.state.webgateway.retriever.RetrievedCacheContainer;
import org.cresplanex.api.state.webgateway.retriever.domain.OrganizationRetriever;
import org.cresplanex.api.state.webgateway.retriever.domain.TeamRetriever;
import org.cresplanex.api.state.webgateway.retriever.domain.UserProfileRetriever;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AttachRelationOrganization {

    private final UserProfileQueryProxy userProfileQueryProxy;
    private final TeamQueryProxy teamQueryProxy;
    private final UserPreferenceQueryProxy userPreferenceQueryProxy;
    private final OrganizationQueryProxy organizationQueryProxy;
    private final TaskQueryProxy taskQueryProxy;
    private final FileObjectQueryProxy fileObjectQueryProxy;

    public <T extends OrganizationDto> void attach(String operatorId, RetrievedCacheContainer cache, OrganizationRetriever retriever, T organizationDto) {

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

    public <T extends OrganizationDto> void attach(String operatorId, RetrievedCacheContainer cache, OrganizationRetriever retriever, List<T> organizationDto) {

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

            this.attachRelationToOwner(
                    operatorId,
                    cache,
                    organizationDto,
                    userProfileDtoMap,
                    organizationDto.stream()
                            .map(retriever.getOwnerRelationRetriever().getRelationRetriever())
                            .toList(),
                    retriever.getOwnerRelationRetriever().getChain()
            );
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

            this.attachRelationToUsers(
                    operatorId,
                    cache,
                    organizationDto,
                    userProfileDtoMap,
                    organizationDto.stream()
                            .map(retriever.getUsersRelationRetriever().getRelationRetriever())
                            .toList(),
                    retriever.getUsersRelationRetriever().getChain()
            );
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

            this.attachRelationToTeams(
                    operatorId,
                    cache,
                    organizationDto,
                    teamDtoMap,
                    organizationDto.stream()
                            .map(retriever.getTeamsRelationRetriever().getRelationRetriever())
                            .toList(),
                    retriever.getTeamsRelationRetriever().getChain()
            );
        }
    }

    private <T extends OrganizationDto> void internalAttachRelationToOwner(
            T organizationDto,
            Map<String, UserProfileDto> userProfileDtoMap,
            String ownerId
    ) {
        organizationDto.setOwner(Relation.<UserProfileDto>builder()
                .hasValue(true)
                .value(userProfileDtoMap.get(ownerId))
                .build()
        );
    }

    protected <T extends OrganizationDto> void attachRelationToOwner(
            String operatorId,
            RetrievedCacheContainer cache,
            T organizationDto,
            Map<String, UserProfileDto> userProfileDtoMap,
            Relation<UserProfileDto> ownerRelation,
            List<UserProfileRetriever> retrievers
    ) {
        this.internalAttachRelationToOwner(organizationDto, userProfileDtoMap, organizationDto.getOwnerId());
        retrievers.forEach(retriever -> {
            if (retriever != null && ownerRelation.isHasValue() && ownerRelation.getValue() != null) {
                AttachRelationUserProfile attachRelationUserProfile = new AttachRelationUserProfile(
                        userProfileQueryProxy,
                        teamQueryProxy,
                        userPreferenceQueryProxy,
                        organizationQueryProxy,
                        taskQueryProxy,
                        fileObjectQueryProxy
                );
                attachRelationUserProfile.attach(operatorId, cache, retriever, ownerRelation.getValue());
            }
        });
    }

    protected <T extends OrganizationDto> void attachRelationToOwner(
            String operatorId,
            RetrievedCacheContainer cache,
            List<T> organizationDto,
            Map<String, UserProfileDto> userProfileDtoMap,
            List<Relation<UserProfileDto>> ownerRelation,
            List<UserProfileRetriever> retrievers
    ) {
        for (T dto : organizationDto) {
            this.internalAttachRelationToOwner(dto, userProfileDtoMap, dto.getOwnerId());
        }
        retrievers.forEach(retriever -> {
            List<UserProfileDto> ownerRelationList = ownerRelation.stream()
                    .filter(Relation::isHasValue)
                    .map(Relation::getValue)
                    .toList();
            if (retriever != null && !ownerRelationList.isEmpty()) {
                AttachRelationUserProfile attachRelationUserProfile = new AttachRelationUserProfile(
                        userProfileQueryProxy,
                        teamQueryProxy,
                        userPreferenceQueryProxy,
                        organizationQueryProxy,
                        taskQueryProxy,
                        fileObjectQueryProxy
                );
                attachRelationUserProfile.attach(operatorId, cache, retriever, ownerRelationList);
            }
        });
    }

    private <T extends OrganizationDto> void internalAttachRelationToUsers(
            T organizationDto,
            Map<String, UserProfileDto> userProfileDtoMap
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
        }
    }

    protected <T extends OrganizationDto> void attachRelationToUsers(
            String operatorId,
            RetrievedCacheContainer cache,
            T organizationDto,
            Map<String, UserProfileDto> userProfileDtoMap,
            ListRelation<UserProfileOnOrganizationDto> usersRelation,
            List<UserProfileRetriever> retrievers
    ) {
        this.internalAttachRelationToUsers(organizationDto, userProfileDtoMap);
        retrievers.forEach(retriever -> {
            if (retriever != null && usersRelation.isHasValue() && usersRelation.getValue() != null) {
                AttachRelationUserProfile attachRelationUserProfile = new AttachRelationUserProfile(
                        userProfileQueryProxy,
                        teamQueryProxy,
                        userPreferenceQueryProxy,
                        organizationQueryProxy,
                        taskQueryProxy,
                        fileObjectQueryProxy
                );
                attachRelationUserProfile.attach(operatorId, cache, retriever, usersRelation.getValue());
            }
        });
    }

    protected <T extends OrganizationDto> void attachRelationToUsers(
            String operatorId,
            RetrievedCacheContainer cache,
            List<T> organizationDto,
            Map<String, UserProfileDto> userProfileDtoMap,
            List<ListRelation<UserProfileOnOrganizationDto>> usersRelation,
            List<UserProfileRetriever> retrievers
    ) {
        for (T dto : organizationDto) {
            this.internalAttachRelationToUsers(dto, userProfileDtoMap);
        }
        retrievers.forEach(retriever -> {
            List<List<UserProfileOnOrganizationDto>> usersRelationList = usersRelation.stream()
                    .filter(ListRelation::isHasValue)
                    .map(ListRelation::getValue)
                    .toList();
            if (retriever != null && !usersRelationList.isEmpty()) {
                AttachRelationUserProfile attachRelationUserProfile = new AttachRelationUserProfile(
                        userProfileQueryProxy,
                        teamQueryProxy,
                        userPreferenceQueryProxy,
                        organizationQueryProxy,
                        taskQueryProxy,
                        fileObjectQueryProxy
                );
                attachRelationUserProfile.attach(operatorId, cache, retriever, usersRelationList.stream()
                        .flatMap(List::stream)
                        .toList());
            }
        });
    }

    private <T extends OrganizationDto> void internalAttachRelationToTeams(
            T organizationDto,
            Map<String, TeamDto> teamDtoMap
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
        }
    }

    protected <T extends OrganizationDto> void attachRelationToTeams(
            String operatorId,
            RetrievedCacheContainer cache,
            T organizationDto,
            Map<String, TeamDto> teamDtoMap,
            ListRelation<TeamDto> teamsRelation,
            List<TeamRetriever> retrievers
    ) {
        this.internalAttachRelationToTeams(organizationDto, teamDtoMap);
        retrievers.forEach(retriever -> {
            if (retriever != null && teamsRelation.isHasValue() && teamsRelation.getValue() != null) {
                AttachRelationTeam attachRelationTeam = new AttachRelationTeam(
                        userProfileQueryProxy,
                        teamQueryProxy,
                        userPreferenceQueryProxy,
                        organizationQueryProxy,
                        taskQueryProxy,
                        fileObjectQueryProxy
                );
                attachRelationTeam.attach(operatorId, cache, retriever, teamsRelation.getValue());
            }
        });
    }

    protected <T extends OrganizationDto> void attachRelationToTeams(
            String operatorId,
            RetrievedCacheContainer cache,
            List<T> organizationDto,
            Map<String, TeamDto> teamDtoMap,
            List<ListRelation<TeamDto>> teamsRelation,
            List<TeamRetriever> retrievers
    ) {
        for (T dto : organizationDto) {
            this.internalAttachRelationToTeams(dto, teamDtoMap);
        }
        retrievers.forEach(retriever -> {
            List<List<TeamDto>> teamsRelationList = teamsRelation.stream()
                    .filter(ListRelation::isHasValue)
                    .map(ListRelation::getValue)
                    .toList();
            if (retriever != null && !teamsRelationList.isEmpty()) {
                AttachRelationTeam attachRelationTeam = new AttachRelationTeam(
                        userProfileQueryProxy,
                        teamQueryProxy,
                        userPreferenceQueryProxy,
                        organizationQueryProxy,
                        taskQueryProxy,
                        fileObjectQueryProxy
                );
                attachRelationTeam.attach(operatorId, cache, retriever, teamsRelationList.stream().flatMap(List::stream).toList());
            }
        });
    }
}
