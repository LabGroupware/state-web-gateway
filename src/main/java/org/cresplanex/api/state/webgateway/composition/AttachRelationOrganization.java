package org.cresplanex.api.state.webgateway.composition;

import lombok.RequiredArgsConstructor;
import org.cresplanex.api.state.webgateway.dto.domain.ListRelation;
import org.cresplanex.api.state.webgateway.dto.domain.Relation;
import org.cresplanex.api.state.webgateway.dto.domain.organization.OrganizationDto;
import org.cresplanex.api.state.webgateway.dto.domain.team.TeamDto;
import org.cresplanex.api.state.webgateway.dto.domain.userprofile.UserProfileDto;
import org.cresplanex.api.state.webgateway.dto.domain.userprofile.UserProfileOnOrganizationDto;
import org.cresplanex.api.state.webgateway.hasher.TeamHasher;
import org.cresplanex.api.state.webgateway.hasher.UserProfileHasher;
import org.cresplanex.api.state.webgateway.proxy.query.TeamQueryProxy;
import org.cresplanex.api.state.webgateway.proxy.query.UserProfileQueryProxy;
import org.cresplanex.api.state.webgateway.retriever.RetrievedCacheContainer;
import org.cresplanex.api.state.webgateway.retriever.domain.OrganizationRetriever;
import org.cresplanex.api.state.webgateway.retriever.domain.TeamRetriever;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class AttachRelationOrganization {

    private final UserProfileQueryProxy userProfileQueryProxy;
    private final TeamQueryProxy teamQueryProxy;

    public static final int NEED_TEAM_USERS = 1 << 0;

    public static final int GET_TEAM_WITH_USERS = NEED_TEAM_USERS;

    public void attach(String operatorId, RetrievedCacheContainer cache, OrganizationRetriever retriever, OrganizationDto organizationDto) {

        // Owner
        if (retriever.getOwnerRelationRetriever() != null) {
            // 取得が必要なIDの取得
            String ownerId = retriever.getOwnerRelationRetriever().getIdRetriever().apply(organizationDto);
            List<String> needRetrieveUserIds = new ArrayList<>();
            Map<String, UserProfileDto> userProfileDtoMap = new HashMap<>();
            if (cache.getCache().containsKey(UserProfileHasher.hashUserProfileByUserId(ownerId))) {
                userProfileDtoMap.put(ownerId, (UserProfileDto) cache.getCache().get(UserProfileHasher.hashUserProfileByUserId(ownerId)));
            } else {
                needRetrieveUserIds.add(ownerId);
            }

            if (!needRetrieveUserIds.isEmpty()) {
                List<UserProfileDto> userProfile = userProfileQueryProxy.getPluralUserProfilesByUserIds(
                        operatorId,
                        needRetrieveUserIds,
                        null,
                        null
                ).getListData();
                for (UserProfileDto dto : userProfile) {
                    userProfileDtoMap.put(dto.getUserId(), dto);
                    cache.getCache().put(UserProfileHasher.hashUserProfileByUserId(dto.getUserId()), dto.deepClone());
                }
            }

            organizationDto.setOwner(Relation.<UserProfileDto>builder()
                    .hasValue(true)
                    .value(userProfileDtoMap.get(ownerId))
                    .build()
            );
            Relation<UserProfileDto> ownerRelation = retriever.getOwnerRelationRetriever().getRelationRetriever().apply(organizationDto);
            retriever.getOwnerRelationRetriever().getChain().forEach(subRetriever -> {
                if (subRetriever != null && ownerRelation.isHasValue() && ownerRelation.getValue() != null) {
                    // TODO: Implement this
                }
            });
        }

        // Users
        if (retriever.getUsersRelationRetriever() != null) {
            // 取得が必要なIDの取得
            List<String> userIds = retriever.getUsersRelationRetriever().getIdRetriever().apply(organizationDto);
            List<String> needRetrieveUserIds = new ArrayList<>();
            Map<String, UserProfileDto> userProfileDtoMap = new HashMap<>();
            for (String userId : userIds) {
                if (cache.getCache().containsKey(UserProfileHasher.hashUserProfileByUserId(userId))) {
                    userProfileDtoMap.put(userId, (UserProfileDto) cache.getCache().get(UserProfileHasher.hashUserProfileByUserId(userId)));
                    break;
                }else {
                    needRetrieveUserIds.add(userId);
                }
            }

            if (!needRetrieveUserIds.isEmpty()) {
                List<UserProfileDto> userProfile = userProfileQueryProxy.getPluralUserProfilesByUserIds(
                        operatorId,
                        needRetrieveUserIds,
                        null,
                        null
                ).getListData();
                for (UserProfileDto dto : userProfile) {
                    userProfileDtoMap.put(dto.getUserId(), dto);
                    cache.getCache().put(UserProfileHasher.hashUserProfileByUserId(dto.getUserId()), dto.deepClone());
                }
            }

            if(organizationDto.getUsers().isHasValue()) {
                List<UserProfileOnOrganizationDto> originAttachUserProfiles = organizationDto.getUsers().getValue();
                List<String> attachUserIds = originAttachUserProfiles
                        .stream()
                        .map(UserProfileDto::getUserId)
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
                ListRelation<UserProfileOnOrganizationDto> usersRelation = retriever.getUsersRelationRetriever().getRelationRetriever().apply(organizationDto);
                retriever.getUsersRelationRetriever().getChain().forEach(subRetriever -> {
                    if (subRetriever != null && usersRelation.isHasValue() && usersRelation.getValue() != null) {
                        // TODO: Implement this
                    }
                });
            }
        }

        // teams
        if (retriever.getTeamsRelationRetriever() != null) {
            int need = 0;
            for (TeamRetriever subRetriever : retriever.getTeamsRelationRetriever().getChain()) {
                if (subRetriever != null && subRetriever.getUsersRelationRetriever() != null) {
                    need |= NEED_TEAM_USERS;
                }
            }
            Map<String, TeamDto> teamDtoMap = new HashMap<>();
            List<String> needRetrieveTeamIds = new ArrayList<>();
            List<TeamDto> teams = new ArrayList<>();
            List<String> teamIds = retriever.getTeamsRelationRetriever().getIdRetriever().apply(organizationDto);
            switch (need) {
                case GET_TEAM_WITH_USERS:
                    for (String teamId : teamIds) {
                        if (cache.getCache().containsKey(TeamHasher.hashTeamWithUsers(teamId))) {
                            teamDtoMap.put(teamId, (TeamDto) cache.getCache().get(TeamHasher.hashTeamWithUsers(teamId)));
                            break;
                        } else {
                            needRetrieveTeamIds.add(teamId);
                        }
                    }

                    if (!needRetrieveTeamIds.isEmpty()) {
                        teams = teamQueryProxy.getPluralTeamsWithUsers(
                                operatorId,
                                needRetrieveTeamIds,
                                null,
                                null
                        ).getListData();
                        for (TeamDto dto : teams) {
                            teamDtoMap.put(dto.getTeamId(), dto);
                            cache.getCache().put(TeamHasher.hashTeamWithUsers(dto.getTeamId()), dto.deepClone());
                        }
                    }
                    break;
                default:
                    for (String teamId : teamIds) {
                        if (cache.getCache().containsKey(TeamHasher.hashTeam(teamId))) {
                            teamDtoMap.put(teamId, (TeamDto) cache.getCache().get(TeamHasher.hashTeam(teamId)));
                            break;
                        } else {
                            needRetrieveTeamIds.add(teamId);
                        }
                    }

                    if (!needRetrieveTeamIds.isEmpty()) {
                        teams = teamQueryProxy.getPluralTeams(
                                operatorId,
                                needRetrieveTeamIds,
                                null,
                                null
                        ).getListData();
                        for (TeamDto dto : teams) {
                            teamDtoMap.put(dto.getTeamId(), dto);
                            cache.getCache().put(TeamHasher.hashTeam(dto.getTeamId()), dto.deepClone());
                        }
                    }
                    break;
            }

            organizationDto.setTeams(ListRelation.<TeamDto>builder()
                    .value(
                            teamIds.stream()
                                    .map(teamDtoMap::get)
                                    .toList()
                    )
                    .build()
            );
            ListRelation<TeamDto> teamsRelation = retriever.getTeamsRelationRetriever().getRelationRetriever().apply(organizationDto);
            retriever.getTeamsRelationRetriever().getChain().forEach(subRetriever -> {
                if (subRetriever != null && teamsRelation.isHasValue() && teamsRelation.getValue() != null) {
                    // TODO: Implement this
                }
            });
        }
    }

    public void attach(String operatorId, RetrievedCacheContainer cache, OrganizationRetriever retriever, List<OrganizationDto> organizationDto) {

        // Owner
        if (retriever.getOwnerRelationRetriever() != null) {
            List<String> userIds = new ArrayList<>();
            Map<String, UserProfileDto> userProfileDtoMap = new HashMap<>();
            for (OrganizationDto dto : organizationDto) {
                String userId = retriever.getOwnerRelationRetriever().getIdRetriever().apply(dto);
                userIds.add(userId);
            }
            List<String> needRetrieveUserIds = new ArrayList<>();
            for (String userId : userIds) {
                if (cache.getCache().containsKey(UserProfileHasher.hashUserProfileByUserId(userId))) {
                    userProfileDtoMap.put(userId, (UserProfileDto) cache.getCache().get(UserProfileHasher.hashUserProfileByUserId(userId)));
                    break;
                } else {
                    needRetrieveUserIds.add(userId);
                }
            }

            if (!needRetrieveUserIds.isEmpty()) {
                List<UserProfileDto> userProfile = userProfileQueryProxy.getPluralUserProfilesByUserIds(
                        operatorId,
                        needRetrieveUserIds,
                        null,
                        null
                ).getListData();
                for (UserProfileDto dto : userProfile) {
                    userProfileDtoMap.put(dto.getUserId(), dto);
                    cache.getCache().put(UserProfileHasher.hashUserProfileByUserId(dto.getUserId()), dto.deepClone());
                }
            }

            for (OrganizationDto dto : organizationDto) {
                dto.setOwner(Relation.<UserProfileDto>builder()
                        .hasValue(true)
                        .value(userProfileDtoMap.get(retriever.getOwnerRelationRetriever().getIdRetriever().apply(dto)))
                        .build()
                );
                Relation<UserProfileDto> ownerRelation = retriever.getOwnerRelationRetriever().getRelationRetriever().apply(dto);
                retriever.getOwnerRelationRetriever().getChain().forEach(subRetriever -> {
                    if (subRetriever != null && ownerRelation.isHasValue() && ownerRelation.getValue() != null) {
                        // TODO: Implement this
                    }
                });
            }
        }

        // Users
        if (retriever.getUsersRelationRetriever() != null) {
            List<String> userIds = new ArrayList<>();
            Map<String, UserProfileDto> userProfileDtoMap = new HashMap<>();
            for (OrganizationDto dto : organizationDto) {
                List<String> ids = retriever.getUsersRelationRetriever().getIdRetriever().apply(dto);
                userIds.addAll(ids);
            }
            List<String> needRetrieveUserIds = new ArrayList<>();
            for (String userId : userIds) {
                if (cache.getCache().containsKey(UserProfileHasher.hashUserProfileByUserId(userId))) {
                    userProfileDtoMap.put(userId, (UserProfileDto) cache.getCache().get(UserProfileHasher.hashUserProfileByUserId(userId)));
                    break;
                } else {
                    needRetrieveUserIds.add(userId);
                }
            }

            if (!needRetrieveUserIds.isEmpty()) {
                List<UserProfileDto> userProfile = userProfileQueryProxy.getPluralUserProfilesByUserIds(
                        operatorId,
                        needRetrieveUserIds,
                        null,
                        null
                ).getListData();
                for (UserProfileDto dto : userProfile) {
                    userProfileDtoMap.put(dto.getUserId(), dto);
                    cache.getCache().put(UserProfileHasher.hashUserProfileByUserId(dto.getUserId()), dto.deepClone());
                }
            }

            for (OrganizationDto dto : organizationDto) {
                List<UserProfileOnOrganizationDto> originAttachUserProfiles = dto.getUsers().getValue();
                List<String> attachUserIds = originAttachUserProfiles
                        .stream()
                        .map(UserProfileDto::getUserId)
                        .toList();
                List<UserProfileDto> attachUserProfiles = attachUserIds.stream()
                        .map(userProfileDtoMap::get)
                        .toList();
                dto.setUsers(ListRelation.<UserProfileOnOrganizationDto>builder()
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
                ListRelation<UserProfileOnOrganizationDto> usersRelation = retriever.getUsersRelationRetriever().getRelationRetriever().apply(dto);
                retriever.getUsersRelationRetriever().getChain().forEach(subRetriever -> {
                    if (subRetriever != null && usersRelation.isHasValue() && usersRelation.getValue() != null) {
                        // TODO: Implement this
                    }
                });
            }
        }

        // teams
        if (retriever.getTeamsRelationRetriever() != null) {
            int need = 0;
            for (TeamRetriever subRetriever : retriever.getTeamsRelationRetriever().getChain()) {
                if (subRetriever != null && subRetriever.getUsersRelationRetriever() != null) {
                    need |= NEED_TEAM_USERS;
                }
            }
            List<TeamDto> teams = new ArrayList<>();
            List<String> teamIds = new ArrayList<>();
            Map<String, TeamDto> teamDtoMap = new HashMap<>();
            for (OrganizationDto dto : organizationDto) {
                List<String> ids = retriever.getTeamsRelationRetriever().getIdRetriever().apply(dto);
                teamIds.addAll(ids);
            }
            List<String> needRetrieveTeamIds = new ArrayList<>();

            switch (need) {
                case GET_TEAM_WITH_USERS:
                    needRetrieveTeamIds = new ArrayList<>();
                    for (String teamId : teamIds) {
                        if (cache.getCache().containsKey(TeamHasher.hashTeamWithUsers(teamId))) {
                            teamDtoMap.put(teamId, (TeamDto) cache.getCache().get(TeamHasher.hashTeamWithUsers(teamId)));
                            break;
                        } else {
                            needRetrieveTeamIds.add(teamId);
                        }
                    }

                    if (!needRetrieveTeamIds.isEmpty()) {
                        teams = teamQueryProxy.getPluralTeamsWithUsers(
                                operatorId,
                                needRetrieveTeamIds,
                                null,
                                null
                        ).getListData();
                        for (TeamDto dto : teams) {
                            teamDtoMap.put(dto.getTeamId(), dto);
                            cache.getCache().put(TeamHasher.hashTeamWithUsers(dto.getTeamId()), dto.deepClone());
                        }
                    }
                    break;
                default:
                    needRetrieveTeamIds = new ArrayList<>();
                    for (String teamId : teamIds) {
                        if (cache.getCache().containsKey(TeamHasher.hashTeam(teamId))) {
                            teamDtoMap.put(teamId, (TeamDto) cache.getCache().get(TeamHasher.hashTeam(teamId)));
                            break;
                        } else {
                            needRetrieveTeamIds.add(teamId);
                        }
                    }
                    if (!needRetrieveTeamIds.isEmpty()) {
                        teams = teamQueryProxy.getPluralTeams(
                                operatorId,
                                needRetrieveTeamIds,
                                null,
                                null
                        ).getListData();
                        for (TeamDto dto : teams) {
                            teamDtoMap.put(dto.getTeamId(), dto);
                            cache.getCache().put(TeamHasher.hashTeam(dto.getTeamId()), dto.deepClone());
                        }
                    }
                    break;
            }

            for (OrganizationDto dto : organizationDto) {
                dto.setTeams(ListRelation.<TeamDto>builder()
                        .value(
                                teamIds.stream()
                                        .map(teamDtoMap::get)
                                        .toList()
                        )
                        .build()
                );
                ListRelation<TeamDto> teamsRelation = retriever.getTeamsRelationRetriever().getRelationRetriever().apply(dto);
                retriever.getTeamsRelationRetriever().getChain().forEach(subRetriever -> {
                    if (subRetriever != null && teamsRelation.isHasValue() && teamsRelation.getValue() != null) {
                        // TODO: Implement this
                    }
                });
            }
        }
    }
}
