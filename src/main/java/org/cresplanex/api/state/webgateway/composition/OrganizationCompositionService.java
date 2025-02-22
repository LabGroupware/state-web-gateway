package org.cresplanex.api.state.webgateway.composition;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.cresplanex.api.state.webgateway.composition.attach.AttachRelationOrganization;
import org.cresplanex.api.state.webgateway.composition.helper.OrganizationCompositionHelper;
import org.cresplanex.api.state.webgateway.dto.ListResponseDto;
import org.cresplanex.api.state.webgateway.dto.domain.organization.OrganizationDto;
import org.cresplanex.api.state.webgateway.hasher.OrganizationHasher;
import org.cresplanex.api.state.webgateway.hasher.UserProfileHasher;
import org.cresplanex.api.state.webgateway.proxy.query.OrganizationQueryProxy;
import org.cresplanex.api.state.webgateway.retriever.RetrievedCacheContainer;
import org.cresplanex.api.state.webgateway.retriever.domain.OrganizationRetriever;
import org.cresplanex.api.state.webgateway.retriever.resolver.OrganizationRetrieveResolver;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrganizationCompositionService {

    private final OrganizationQueryProxy organizationQueryProxy;
    private final AttachRelationOrganization attachRelationOrganization;

    public OrganizationDto findOrganization(String operatorId, String organizationId, List<String> with) {
        OrganizationDto organization;
        OrganizationRetriever organizationRetriever = OrganizationRetrieveResolver.resolve(
                with != null ? with.toArray(new String[0]) : new String[0]
        );
        int need = OrganizationCompositionHelper.calculateNeedQuery(List.of(organizationRetriever));
        RetrievedCacheContainer cache = new RetrievedCacheContainer();
        switch (need) {
            case OrganizationCompositionHelper.GET_ORGANIZATION_WITH_USERS:
                organization = organizationQueryProxy.findOrganizationWithUsers(
                        operatorId,
                        organizationId
                );
                cache.getCache().put(OrganizationHasher.hashOrganizationWithUsers(organizationId), organization.deepClone());
                break;
            default:
                organization = organizationQueryProxy.findOrganization(
                        operatorId,
                        organizationId
                );
                cache.getCache().put(OrganizationHasher.hashOrganization(organizationId), organization.deepClone());
                break;
        }
        attachRelationOrganization.attach(
                operatorId,
                cache,
                organizationRetriever,
                organization
        );

        return organization;
    }

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
            String userFilterType,
            List<String> with
    ) {
        ListResponseDto.InternalData<OrganizationDto> organizations;
        OrganizationRetriever organizationRetriever = OrganizationRetrieveResolver.resolve(
                with != null ? with.toArray(new String[0]) : new String[0]
        );
        int need = OrganizationCompositionHelper.calculateNeedQuery(List.of(organizationRetriever));
        RetrievedCacheContainer cache = new RetrievedCacheContainer();
        switch (need) {
            case OrganizationCompositionHelper.GET_ORGANIZATION_WITH_USERS:
                organizations = organizationQueryProxy.getOrganizationsWithUsers(
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
                for (OrganizationDto dto : organizations.getListData()) {
                    cache.getCache().put(OrganizationHasher.hashOrganizationWithUsers(dto.getOrganizationId()), dto.deepClone());
                }
                break;
            default:
                organizations = organizationQueryProxy.getOrganizations(
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
                for (OrganizationDto dto : organizations.getListData()) {
                    cache.getCache().put(OrganizationHasher.hashOrganization(dto.getOrganizationId()), dto.deepClone());
                }
                break;
        }

        attachRelationOrganization.attach(
                operatorId,
                cache,
                organizationRetriever,
                organizations.getListData()
        );

        return organizations;
    }
}
