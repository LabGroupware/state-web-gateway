package org.cresplanex.api.state.webgateway.composition.attach;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cresplanex.api.state.webgateway.composition.helper.TeamCompositionHelper;
import org.cresplanex.api.state.webgateway.composition.helper.UserProfileCompositionHelper;
import org.cresplanex.api.state.webgateway.dto.domain.ListRelation;
import org.cresplanex.api.state.webgateway.dto.domain.Relation;
import org.cresplanex.api.state.webgateway.dto.domain.organization.OrganizationDto;
import org.cresplanex.api.state.webgateway.dto.domain.plan.TaskOnFileObjectDto;
import org.cresplanex.api.state.webgateway.dto.domain.team.TeamDto;
import org.cresplanex.api.state.webgateway.dto.domain.userprofile.UserProfileDto;
import org.cresplanex.api.state.webgateway.dto.domain.userprofile.UserProfileOnOrganizationDto;
import org.cresplanex.api.state.webgateway.proxy.query.*;
import org.cresplanex.api.state.webgateway.retriever.RetrievedCacheContainer;
import org.cresplanex.api.state.webgateway.retriever.domain.OrganizationRetriever;
import org.cresplanex.api.state.webgateway.retriever.domain.TeamRetriever;
import org.cresplanex.api.state.webgateway.retriever.domain.UserProfileRetriever;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class AttachRelationOrganization {

    private final UserProfileQueryProxy userProfileQueryProxy;
    private final TeamQueryProxy teamQueryProxy;
    private final UserPreferenceQueryProxy userPreferenceQueryProxy;
    private final OrganizationQueryProxy organizationQueryProxy;
    private final TaskQueryProxy taskQueryProxy;
    private final FileObjectQueryProxy fileObjectQueryProxy;

    public <T extends OrganizationDto> T attach(String operatorId, RetrievedCacheContainer cache, OrganizationRetriever retriever, T organizationDto) {

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
                    retriever.getTeamsRelationRetriever().getChain()
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
                    retriever.getUsersRelationRetriever().getChain()
            );
        }

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
                    retriever.getOwnerRelationRetriever().getChain()
            );
        }

        return organizationDto;
    }

    public <T extends OrganizationDto> List<T> attach(String operatorId, RetrievedCacheContainer cache, OrganizationRetriever retriever, List<T> organizationDto) {

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
                            .distinct()
                            .toList(),
                    retriever.getTeamsRelationRetriever().getChain()
            );

            this.attachRelationToTeams(
                    operatorId,
                    cache,
                    organizationDto,
                    teamDtoMap,
                    retriever.getTeamsRelationRetriever().getChain()
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
                            .distinct()
                            .toList(),
                    retriever.getUsersRelationRetriever().getChain()
            );

            this.attachRelationToUsers(
                    operatorId,
                    cache,
                    organizationDto,
                    userProfileDtoMap,
                    retriever.getUsersRelationRetriever().getChain()
            );
        }

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
                    retriever.getOwnerRelationRetriever().getChain()
            );
        }

        return organizationDto;
    }

    private <T extends OrganizationDto, U extends UserProfileDto> void internalAttachRelationToOwner(
            T organizationDto,
            Map<String, U> userProfileDtoMap
    ) {
        UserProfileDto originUserProfile = organizationDto.getOwner().getValue();
        organizationDto.setOwner(Relation.<UserProfileDto>builder()
                .hasValue(true)
                .value(userProfileDtoMap.get(organizationDto.getOwnerId()).merge(originUserProfile))
                .build()
        );
    }

    protected <T extends OrganizationDto> void attachRelationToOwner(
            String operatorId,
            RetrievedCacheContainer cache,
            T organizationDto,
            Map<String, UserProfileDto> userProfileDtoMap,
            List<UserProfileRetriever> retrievers
    ) {
        this.internalAttachRelationToOwner(organizationDto, userProfileDtoMap);
        retrievers.forEach(retriever -> {
            if (retriever != null && organizationDto.getOwner().isHasValue() && organizationDto.getOwner().getValue() != null) {
                AttachRelationUserProfile attachRelationUserProfile = new AttachRelationUserProfile(
                        userProfileQueryProxy,
                        teamQueryProxy,
                        userPreferenceQueryProxy,
                        organizationQueryProxy,
                        taskQueryProxy,
                        fileObjectQueryProxy
                );
                var attached = attachRelationUserProfile.attach(operatorId, cache, retriever, organizationDto.getOwner().getValue());

                Map<String, UserProfileDto> attachedMap = new HashMap<>();
                attachedMap.put(attached.getUserId(), attached);

                this.internalAttachRelationToOwner(organizationDto, attachedMap);
            }
        });
    }

    protected <T extends OrganizationDto> void attachRelationToOwner(
            String operatorId,
            RetrievedCacheContainer cache,
            List<T> organizationDto,
            Map<String, UserProfileDto> userProfileDtoMap,
            List<UserProfileRetriever> retrievers
    ) {
        for (T dto : organizationDto) {
            this.internalAttachRelationToOwner(dto, userProfileDtoMap);
        }
        retrievers.forEach(retriever -> {
            if (retriever != null) {
                AttachRelationUserProfile attachRelationUserProfile = new AttachRelationUserProfile(
                        userProfileQueryProxy,
                        teamQueryProxy,
                        userPreferenceQueryProxy,
                        organizationQueryProxy,
                        taskQueryProxy,
                        fileObjectQueryProxy
                );
                var attached = attachRelationUserProfile.attach(operatorId, cache, retriever, organizationDto.stream()
                        .map(T::getOwner)
                        .map(Relation::getValue)
                        .toList());

                Map<String, UserProfileDto> attachedMap = new HashMap<>();
                for (UserProfileDto userProfileDto : attached) {
                    attachedMap.put(userProfileDto.getUserId(), userProfileDto);
                }

                for (T dto : organizationDto) {
                    this.internalAttachRelationToOwner(dto, attachedMap);
                }
            }
        });
    }

    private <T extends OrganizationDto, U extends UserProfileDto> void internalAttachRelationToUsers(
            T organizationDto,
            Map<String, U> userProfileDtoMap
    ) {
        if(organizationDto.getUsers().isHasValue()) {
            List<UserProfileOnOrganizationDto> originAttachUserProfiles = organizationDto.getUsers().getValue();
            List<String> attachUserIds = originAttachUserProfiles
                    .stream()
                    .map(UserProfileOnOrganizationDto::getUserId)
                    .toList();
            List<U> attachUserProfiles = attachUserIds.stream()
                    .map(userProfileDtoMap::get)
                    .toList();
            organizationDto.setUsers(ListRelation.<UserProfileOnOrganizationDto>builder()
                    .hasValue(true)
                    .value(
                            attachUserProfiles.stream()
                                    .map(user -> new UserProfileOnOrganizationDto(user.merge(originAttachUserProfiles.stream()
                                            .filter(origin -> origin.getUserId().equals(user.getUserId()))
                                            .findFirst().orElse(null))))
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
            List<UserProfileRetriever> retrievers
    ) {
        this.internalAttachRelationToUsers(organizationDto, userProfileDtoMap);
        retrievers.forEach(retriever -> {
            if (retriever != null && organizationDto.getUsers().isHasValue() && organizationDto.getUsers().getValue() != null) {
                AttachRelationUserProfile attachRelationUserProfile = new AttachRelationUserProfile(
                        userProfileQueryProxy,
                        teamQueryProxy,
                        userPreferenceQueryProxy,
                        organizationQueryProxy,
                        taskQueryProxy,
                        fileObjectQueryProxy
                );
                var attached = attachRelationUserProfile.attach(operatorId, cache, retriever, organizationDto.getUsers().getValue());

                Map<String, UserProfileDto> attachedMap = new HashMap<>();

                for (UserProfileDto userProfileDto : attached) {
                    attachedMap.put(userProfileDto.getUserId(), userProfileDto);
                }

                this.internalAttachRelationToUsers(organizationDto, attachedMap);
            }
        });
    }

    protected <T extends OrganizationDto> void attachRelationToUsers(
            String operatorId,
            RetrievedCacheContainer cache,
            List<T> organizationDto,
            Map<String, UserProfileDto> userProfileDtoMap,
            List<UserProfileRetriever> retrievers
    ) {
        for (T dto : organizationDto) {
            this.internalAttachRelationToUsers(dto, userProfileDtoMap);
        }
        AttachRelationUserProfile attachRelationUserProfile = new AttachRelationUserProfile(
                userProfileQueryProxy,
                teamQueryProxy,
                userPreferenceQueryProxy,
                organizationQueryProxy,
                taskQueryProxy,
                fileObjectQueryProxy
        );
        retrievers.forEach(retriever -> {
            if (retriever != null) {
                Set<String> seenIds = new HashSet<>();
                List<UserProfileOnOrganizationDto> flatUniqueUsers = organizationDto.stream()
                        .map(T::getUsers)
                        .map(ListRelation::getValue)
                        .flatMap(List::stream)
                        .filter(user -> seenIds.add(user.getUserId()))
                        .toList();

                var attached = attachRelationUserProfile.attach(
                        operatorId,
                        cache,
                        retriever,
                        flatUniqueUsers
                );

                Map<String, UserProfileDto> attachedMap = new HashMap<>();
                for (UserProfileDto userProfileDto : attached) {
                    attachedMap.put(userProfileDto.getUserId(), userProfileDto);
                }

                for (T dto : organizationDto) {
                    this.internalAttachRelationToUsers(dto, attachedMap);
                }
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
                    .hasValue(true)
                    .value(attachTeams.stream()
                            .map(team -> team.merge(originAttachTeams.stream()
                                    .filter(origin -> origin.getTeamId().equals(team.getTeamId()))
                                    .findFirst().orElse(null)))
                            .toList()
                    )
                    .build()
            );
        }
    }

    protected <T extends OrganizationDto> void attachRelationToTeams(
            String operatorId,
            RetrievedCacheContainer cache,
            T organizationDto,
            Map<String, TeamDto> teamDtoMap,
            List<TeamRetriever> retrievers
    ) {
        this.internalAttachRelationToTeams(organizationDto, teamDtoMap);

        retrievers.forEach(retriever -> {
            if (retriever != null && organizationDto.getTeams().isHasValue() && organizationDto.getTeams().getValue() != null) {
                AttachRelationTeam attachRelationTeam = new AttachRelationTeam(
                        userProfileQueryProxy,
                        teamQueryProxy,
                        userPreferenceQueryProxy,
                        organizationQueryProxy,
                        taskQueryProxy,
                        fileObjectQueryProxy
                );
                var attached = attachRelationTeam.attach(operatorId, cache, retriever, organizationDto.getTeams().getValue());

                Map<String, TeamDto> attachedMap = new HashMap<>();
                for (TeamDto teamDto : attached) {
                    attachedMap.put(teamDto.getTeamId(), teamDto);
                }

                this.internalAttachRelationToTeams(organizationDto, attachedMap);
            }
        });
    }

    protected <T extends OrganizationDto> void attachRelationToTeams(
            String operatorId,
            RetrievedCacheContainer cache,
            List<T> organizationDto,
            Map<String, TeamDto> teamDtoMap,
            List<TeamRetriever> retrievers
    ) {
        for (T dto : organizationDto) {
            this.internalAttachRelationToTeams(dto, teamDtoMap);
        }
        retrievers.forEach(retriever -> {
            if (retriever != null) {
                AttachRelationTeam attachRelationTeam = new AttachRelationTeam(
                        userProfileQueryProxy,
                        teamQueryProxy,
                        userPreferenceQueryProxy,
                        organizationQueryProxy,
                        taskQueryProxy,
                        fileObjectQueryProxy
                );
                Set<String> seenIds = new HashSet<>();
                List<TeamDto> flatUniqueTeams = organizationDto.stream()
                        .map(T::getTeams)
                        .map(ListRelation::getValue)
                        .flatMap(List::stream)
                        .filter(team -> seenIds.add(team.getTeamId()))
                        .toList();

                var attached = attachRelationTeam.attach(
                        operatorId,
                        cache,
                        retriever,
                        flatUniqueTeams
                );

                Map<String, TeamDto> attachedMap = new HashMap<>();
                for (TeamDto teamDto : attached) {
                    attachedMap.put(teamDto.getTeamId(), teamDto);
                }

                for (T dto : organizationDto) {
                    this.internalAttachRelationToTeams(dto, attachedMap);
                }
            }
        });
    }
}
