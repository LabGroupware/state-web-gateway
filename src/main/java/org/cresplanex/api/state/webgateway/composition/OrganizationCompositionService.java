package org.cresplanex.api.state.webgateway.composition;

import lombok.RequiredArgsConstructor;
import org.cresplanex.api.state.webgateway.dto.ListResponseDto;
import org.cresplanex.api.state.webgateway.dto.domain.Relation;
import org.cresplanex.api.state.webgateway.dto.domain.organization.OrganizationDto;
import org.cresplanex.api.state.webgateway.dto.domain.userprofile.UserProfileDto;
import org.cresplanex.api.state.webgateway.dto.domain.userprofile.UserProfileOnOrganizationDto;
import org.cresplanex.api.state.webgateway.proxy.query.OrganizationQueryProxy;
import org.cresplanex.api.state.webgateway.proxy.query.UserProfileQueryProxy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class OrganizationCompositionService {

    private final OrganizationQueryProxy organizationQueryProxy;
    private final UserProfileQueryProxy userProfileQueryProxy;

    // 1
    public ListResponseDto.InternalData<OrganizationDto> getOrganizations(
            String operatorId,
            int limit,
            int offset,
            String cursor,
            String pagination,
            String sortField,
            String sortOrder,
            boolean withCount,
            boolean hasOwnerFilter,
            List<String> filterOwnerIds,
            boolean hasPlanFilter,
            List<String> filterPlans,
            boolean hasUserFilter,
            List<String> filterUserIds,
            String userFilterType
    ) {
        return organizationQueryProxy.getOrganizations(
                operatorId,
                limit,
                offset,
                cursor,
                pagination,
                sortField,
                sortOrder,
                withCount,
                hasOwnerFilter,
                filterOwnerIds,
                hasPlanFilter,
                filterPlans,
                hasUserFilter,
                filterUserIds,
                userFilterType
        );
    }

    // 2
    public ListResponseDto.InternalData<OrganizationDto> getOrganizationsWithUsers(
            String operatorId,
            int limit,
            int offset,
            String cursor,
            String pagination,
            String sortField,
            String sortOrder,
            boolean withCount,
            boolean hasOwnerFilter,
            List<String> filterOwnerIds,
            boolean hasPlanFilter,
            List<String> filterPlans,
            boolean hasUserFilter,
            List<String> filterUserIds,
            String userFilterType
    ) {
        ListResponseDto.InternalData<OrganizationDto> organizations = organizationQueryProxy.getOrganizationsWithUsers(
                operatorId,
                limit,
                offset,
                cursor,
                pagination,
                sortField,
                sortOrder,
                withCount,
                hasOwnerFilter,
                filterOwnerIds,
                hasPlanFilter,
                filterPlans,
                hasUserFilter,
                filterUserIds,
                userFilterType
        );

        Set<String> userIds = new java.util.HashSet<String>(Set.of());

        organizations.getListData()
                .forEach(org -> {
                    Set<String> addIds = CommonUtils.createIdsSet(org.getUsers().getValue(), UserProfileOnOrganizationDto::getUserId);
                    userIds.addAll(addIds);
                });

        Map<String, UserProfileDto> userProfileMap = ForUserProfileUtils.getStringUserProfileDtoMap(
                userProfileQueryProxy,
                operatorId,
                userIds
        );

        organizations.getListData().forEach(org -> {
            org.setUsers(Relation.<List<UserProfileOnOrganizationDto>>builder()
                            .hasValue(true)
                            .value(org.getUsers().getValue().stream()
                                    .map(user -> {
                                        UserProfileDto userProfile = userProfileMap.get(user.getUserId());
                                        if (userProfile == null) {
                                            return null;
                                        }
                                        return new UserProfileOnOrganizationDto(userProfile);
                                    })
                                    .collect(Collectors.toList())
                            )
                            .build());
        });

        return organizations;
    }
}
