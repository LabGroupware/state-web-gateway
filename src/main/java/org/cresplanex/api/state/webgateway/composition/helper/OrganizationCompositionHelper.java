package org.cresplanex.api.state.webgateway.composition.helper;

import lombok.extern.slf4j.Slf4j;
import org.cresplanex.api.state.webgateway.dto.domain.ListRelation;
import org.cresplanex.api.state.webgateway.dto.domain.organization.OrganizationDto;
import org.cresplanex.api.state.webgateway.dto.domain.organization.OrganizationOnUserProfileDto;
import org.cresplanex.api.state.webgateway.dto.domain.userprofile.UserProfileDto;
import org.cresplanex.api.state.webgateway.hasher.OrganizationHasher;
import org.cresplanex.api.state.webgateway.proxy.query.OrganizationQueryProxy;
import org.cresplanex.api.state.webgateway.retriever.RetrievedCacheContainer;
import org.cresplanex.api.state.webgateway.retriever.domain.OrganizationRetriever;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class OrganizationCompositionHelper {

    public static final int NEED_ORGANIZATION_USERS = 1 << 0;

    public static final int GET_ORGANIZATION_WITH_USERS = NEED_ORGANIZATION_USERS;

    public static int calculateNeedQuery(List<OrganizationRetriever> retrievers) {
        int needQuery = 0;
        for (OrganizationRetriever retriever : retrievers) {
            if (retriever != null && retriever.getUsersRelationRetriever() != null) {
                needQuery |= NEED_ORGANIZATION_USERS;
            }
        }
        return needQuery;
    }

    public static Map<String, OrganizationDto> createOrganizationDtoMap(
            OrganizationQueryProxy organizationQueryProxy,
            RetrievedCacheContainer cache,
            String operatorId,
            List<String> organizationIds,
            List<OrganizationRetriever> retrievers
    ) {
        int need = calculateNeedQuery(retrievers);
        List<String> needRetrieveAttachedOrganizationIds = new ArrayList<>();
        Map<String, OrganizationDto> organizationDtoMap = new HashMap<>();
        List<OrganizationDto> organization;

        switch (need) {
            case GET_ORGANIZATION_WITH_USERS:
                for (String organizationId : organizationIds) {
                    if (cache.getCache().containsKey(OrganizationHasher.hashOrganizationWithUsers(organizationId))) {
                        organizationDtoMap.put(organizationId, ((OrganizationDto) cache.getCache().get(OrganizationHasher.hashOrganizationWithUsers(organizationId))).deepClone());
                    } else {
                        needRetrieveAttachedOrganizationIds.add(organizationId);
                    }
                }
                if (!needRetrieveAttachedOrganizationIds.isEmpty()) {
                    organization = organizationQueryProxy.getPluralOrganizationsWithUsers(
                            operatorId,
                            needRetrieveAttachedOrganizationIds,
                            "none",
                            "asc"
                    ).getListData();

                    for (OrganizationDto dto : organization) {
                        organizationDtoMap.put(dto.getOrganizationId(), dto);
                        cache.getCache().put(OrganizationHasher.hashOrganizationWithUsers(dto.getOrganizationId()), dto.deepClone());
                    }
                }
                break;
            default:
                for (String organizationId : organizationIds) {
                    if (cache.getCache().containsKey(OrganizationHasher.hashOrganization(organizationId))) {
                        organizationDtoMap.put(organizationId, ((OrganizationDto) cache.getCache().get(OrganizationHasher.hashOrganization(organizationId))).deepClone());
                    } else {
                        needRetrieveAttachedOrganizationIds.add(organizationId);
                    }
                }

                if (!needRetrieveAttachedOrganizationIds.isEmpty()) {
                    organization = organizationQueryProxy.getPluralOrganizations(
                            operatorId,
                            needRetrieveAttachedOrganizationIds,
                            "none",
                            "asc"
                    ).getListData();
                    for (OrganizationDto dto : organization) {
                        organizationDtoMap.put(dto.getOrganizationId(), dto);
                        cache.getCache().put(OrganizationHasher.hashOrganization(dto.getOrganizationId()), dto.deepClone());
                    }
                }
                break;
        }

        return organizationDtoMap;
    }

    public static <T extends UserProfileDto> void preAttachToUserProfile(
            OrganizationQueryProxy organizationQueryProxy,
            RetrievedCacheContainer cache,
            String operatorId,
            List<T> userProfileDtos
    ) {
        List<OrganizationDto> relationOrganizations = organizationQueryProxy.getOrganizationsWithUsers(
                operatorId,
                0,
                0,
                "",
                "none",
                "none",
                "asc",
                false,
                false,
                List.of(),
                false,
                List.of(),
                true,
                userProfileDtos.stream().map(UserProfileDto::getUserId).toList(),
                "any"
        ).getListData();

        Map<String, UserProfileDto> userProfileDtoMap = new HashMap<>();

        for (UserProfileDto dto : userProfileDtos) {
            userProfileDtoMap.put(dto.getUserId(), dto);
        }

        Map<String, List<OrganizationOnUserProfileDto>> organizationOnUserProfileDtoMap = new HashMap<>();

        for (OrganizationDto dto : relationOrganizations) {
            cache.getCache().put(OrganizationHasher.hashOrganizationWithUsers(dto.getOrganizationId()), dto.deepClone());
            if (dto.getUsers().isHasValue()) {
                dto.getUsers().getValue().forEach(userOnOrganization -> {
                    UserProfileDto targetUserProfileDto = userProfileDtoMap.get(userOnOrganization.getUserId());
                    if (targetUserProfileDto != null) {
                        organizationOnUserProfileDtoMap.computeIfAbsent(targetUserProfileDto.getUserId(), k -> new ArrayList<>()).add(new OrganizationOnUserProfileDto(dto));
                    }
                });
            }
        }
        for (Map.Entry<String, List<OrganizationOnUserProfileDto>> entry : organizationOnUserProfileDtoMap.entrySet()) {
            UserProfileDto targetUserProfileDto = userProfileDtoMap.get(entry.getKey());
            if (targetUserProfileDto == null) {
                continue;
            }
            targetUserProfileDto.setOrganizations(ListRelation.<OrganizationOnUserProfileDto>builder()
                    .hasValue(true)
                    .value(entry.getValue())
                    .build()
            );
        }
        userProfileDtos.stream().filter(dto -> !organizationOnUserProfileDtoMap.containsKey(dto.getUserId())).forEach(dto -> {
            dto.setOrganizations(ListRelation.<OrganizationOnUserProfileDto>builder()
                    .hasValue(true)
                    .value(List.of())
                    .build()
            );
        });
    }

    public static <T extends UserProfileDto> void preAttachToOwner(
            OrganizationQueryProxy organizationQueryProxy,
            RetrievedCacheContainer cache,
            String operatorId,
            List<T> userProfileDtos
    ) {
        log.info("userProfileIds: {}", userProfileDtos.stream().map(UserProfileDto::getUserId).toList());
        List<OrganizationDto> relationOrganizations = organizationQueryProxy.getOrganizations(
                operatorId,
                0,
                0,
                "",
                "none",
                "none",
                "asc",
                false,
                true,
                userProfileDtos.stream().map(UserProfileDto::getUserId).toList(),
                true,
                List.of(),
                false,
                List.of(),
                "none"
        ).getListData();

        Map<String, UserProfileDto> userProfileDtoMap = new HashMap<>();

        for (UserProfileDto dto : userProfileDtos) {
            userProfileDtoMap.put(dto.getUserId(), dto);
        }
        Map<String, List<OrganizationDto>> organizationDtoMap = new HashMap<>();

        for (OrganizationDto dto : relationOrganizations) {
            cache.getCache().put(OrganizationHasher.hashOrganization(dto.getOrganizationId()), dto.deepClone());
            String ownerId = dto.getOwnerId();
            if (ownerId != null) {
                organizationDtoMap.computeIfAbsent(ownerId, k -> new ArrayList<>()).add(dto);
            }
        }

        for (Map.Entry<String, List<OrganizationDto>> entry : organizationDtoMap.entrySet()) {
            UserProfileDto targetUserProfileDto = userProfileDtoMap.get(entry.getKey());
            if (targetUserProfileDto == null) {
                continue;
            }
            targetUserProfileDto.setOwnedOrganizations(ListRelation.<OrganizationDto>builder()
                    .hasValue(true)
                    .value(entry.getValue())
                    .build()
            );
        }
        userProfileDtos.stream().filter(dto -> !organizationDtoMap.containsKey(dto.getUserId())).forEach(dto -> {
            dto.setOwnedOrganizations(ListRelation.<OrganizationDto>builder()
                    .hasValue(true)
                    .value(List.of())
                    .build()
            );
        });
    }
}
