package org.cresplanex.api.state.webgateway.proxy.query;

import build.buf.gen.cresplanex.nova.v1.SortOrder;
import build.buf.gen.organization.v1.*;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.cresplanex.api.state.common.constants.OrganizationServiceApplicationCode;
import org.cresplanex.api.state.webgateway.composition.CompositionUtils;
import org.cresplanex.api.state.webgateway.dto.ListResponseDto;
import org.cresplanex.api.state.webgateway.dto.domain.organization.OrganizationDto;
import org.cresplanex.api.state.webgateway.dto.domain.organization.OrganizationOnUserProfileDto;
import org.cresplanex.api.state.webgateway.dto.domain.userprofile.UserProfileOnOrganizationDto;
import org.cresplanex.api.state.webgateway.exception.OrganizationNotFoundException;
import org.cresplanex.api.state.webgateway.mapper.CommonMapper;
import org.cresplanex.api.state.webgateway.mapper.OrganizationMapper;
import org.cresplanex.api.state.webgateway.mapper.UserProfileMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrganizationQueryProxy {

    @GrpcClient("organizationService")
    private OrganizationServiceGrpc.OrganizationServiceBlockingStub organizationServiceBlockingStub;

    public OrganizationDto findOrganization(
            String operatorId,
            String organizationId
    ) {
        FindOrganizationResponse response;
        try {
            response = organizationServiceBlockingStub.findOrganization(
                    FindOrganizationRequest.newBuilder()
                            .setOperatorId(operatorId)
                            .setOrganizationId(organizationId)
                            .build()
            );
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.NOT_FOUND) {
                throw new OrganizationNotFoundException(
                        OrganizationNotFoundException.FindType.ORGANIZATION_ID,
                        organizationId,
                        OrganizationServiceApplicationCode.NOT_FOUND
                );
            }
            throw e;
        }
        return OrganizationMapper.convert(response.getOrganization());
    }

    public OrganizationDto findOrganizationWithUsers(
            String operatorId,
            String organizationId
    ) {
        FindOrganizationWithUsersResponse response;
        try {
            response = organizationServiceBlockingStub.findOrganizationWithUsers(
                    FindOrganizationWithUsersRequest.newBuilder()
                            .setOperatorId(operatorId)
                            .setOrganizationId(organizationId)
                            .build()
            );
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.NOT_FOUND) {
                throw new OrganizationNotFoundException(
                        OrganizationNotFoundException.FindType.ORGANIZATION_ID,
                        organizationId,
                        OrganizationServiceApplicationCode.NOT_FOUND
                );
            }
            throw e;
        }
        return OrganizationMapper.convert(response.getOrganization());
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
            String userFilterType
    ) {
        GetOrganizationsResponse response = organizationServiceBlockingStub.getOrganizations(
                GetOrganizationsRequest.newBuilder()
                        .setOperatorId(operatorId)
                        .setPagination(CompositionUtils.createPagination(limit, offset, cursor, pagination))
                        .setSort(createOrganizationSort(sortField, sortOrder))
                        .setWithCount(withCount)
                        .setFilterOwner(createOrganizationFilterOwner(hasOwnerFilter, filterOwnerIds))
                        .setFilterPlan(createOrganizationFilterPlan(hasPlanFilter, filterPlans))
                        .setFilterUser(createOrganizationFilterUser(hasUserFilter, filterUserIds, userFilterType))
                        .build()
        );
        return new ListResponseDto.InternalData<>(
                response.getOrganizationsList().stream()
                        .map(OrganizationMapper::convert)
                        .toList(),
                CommonMapper.countConvert(response.getCount())
        );
    }

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
        GetOrganizationsWithUsersResponse response = organizationServiceBlockingStub.getOrganizationsWithUsers(
                GetOrganizationsWithUsersRequest.newBuilder()
                        .setOperatorId(operatorId)
                        .setPagination(CompositionUtils.createPagination(limit, offset, cursor, pagination))
                        .setSort(createOrganizationWithUsersSort(sortField, sortOrder))
                        .setWithCount(withCount)
                        .setFilterOwner(createOrganizationFilterOwner(hasOwnerFilter, filterOwnerIds))
                        .setFilterPlan(createOrganizationFilterPlan(hasPlanFilter, filterPlans))
                        .setFilterUser(createOrganizationFilterUser(hasUserFilter, filterUserIds, userFilterType))
                        .build()
        );
        return new ListResponseDto.InternalData<>(
                response.getOrganizationsList().stream()
                        .map(OrganizationMapper::convert)
                        .toList(),
                CommonMapper.countConvert(response.getCount())
        );
    }

    public ListResponseDto.InternalData<OrganizationDto> getPluralOrganizations(
            String operatorId,
            List<String> organizationIds,
            String sortField,
            String sortOrder
    ) {
        GetPluralOrganizationsResponse response = organizationServiceBlockingStub.getPluralOrganizations(
                GetPluralOrganizationsRequest.newBuilder()
                        .setOperatorId(operatorId)
                        .addAllOrganizationIds(organizationIds)
                        .setSort(createOrganizationSort(sortField, sortOrder))
                        .build()
        );

        return new ListResponseDto.InternalData<>(
                response.getOrganizationsList().stream()
                        .map(OrganizationMapper::convert)
                        .toList(),
                CommonMapper.invalidCountGenerate()
        );
    }

    public ListResponseDto.InternalData<OrganizationDto> getPluralOrganizationsWithUsers(
            String operatorId,
            List<String> organizationIds,
            String sortField,
            String sortOrder
    ) {
        GetPluralOrganizationsWithUsersResponse response = organizationServiceBlockingStub.getPluralOrganizationsWithUsers(
                GetPluralOrganizationsWithUsersRequest.newBuilder()
                        .setOperatorId(operatorId)
                        .addAllOrganizationIds(organizationIds)
                        .setSort(createOrganizationWithUsersSort(sortField, sortOrder))
                        .build()
        );

        return new ListResponseDto.InternalData<>(
                response.getOrganizationsList().stream()
                        .map(OrganizationMapper::convert)
                        .toList(),
                CommonMapper.invalidCountGenerate()
        );
    }

    public ListResponseDto.InternalData<UserProfileOnOrganizationDto> getUsersOnOrganization(
            String operatorId,
            String organizationId,
            int limit,
            int offset,
            String cursor,
            String pagination,
            String sortField,
            String sortOrder,
            boolean withCount
    ) {
        GetUsersOnOrganizationResponse response = organizationServiceBlockingStub.getUsersOnOrganization(
                GetUsersOnOrganizationRequest.newBuilder()
                        .setOperatorId(operatorId)
                        .setOrganizationId(organizationId)
                        .setPagination(CompositionUtils.createPagination(limit, offset, cursor, pagination))
                        .setSort(createUserOnOrganizationSort(sortField, sortOrder))
                        .setWithCount(withCount)
                        .build()
        );

        return new ListResponseDto.InternalData<>(
                response.getUsersList().stream()
                        .map(UserProfileMapper::convert)
                        .toList(),
                CommonMapper.countConvert(response.getCount())
        );
    }

    public ListResponseDto.InternalData<OrganizationOnUserProfileDto> getOrganizationsOnUser(
            String operatorId,
            String userId,
            int limit,
            int offset,
            String cursor,
            String pagination,
            String sortField,
            String sortOrder,
            boolean withCount
    ) {
        GetOrganizationsOnUserResponse response = organizationServiceBlockingStub.getOrganizationsOnUser(
                GetOrganizationsOnUserRequest.newBuilder()
                        .setOperatorId(operatorId)
                        .setUserId(userId)
                        .setPagination(CompositionUtils.createPagination(limit, offset, cursor, pagination))
                        .setSort(createOrganizationOnUserSort(sortField, sortOrder))
                        .setWithCount(withCount)
                        .build()
        );

        return new ListResponseDto.InternalData<>(
                response.getOrganizationsList().stream()
                        .map(OrganizationMapper::convert)
                        .toList(),
                CommonMapper.countConvert(response.getCount())
        );
    }

    public static OrganizationSort createOrganizationSort(
            String sortField,
            String sortOrder
    ) {
        OrganizationOrderField orderField = switch (sortField) {
            case "name" -> OrganizationOrderField.ORGANIZATION_ORDER_FIELD_NAME;
            case "create" -> OrganizationOrderField.ORGANIZATION_ORDER_FIELD_CREATE;
            default -> OrganizationOrderField.ORGANIZATION_ORDER_FIELD_CREATE;
        };
        SortOrder order = switch (sortOrder) {
            case "asc" -> SortOrder.SORT_ORDER_ASC;
            case "desc" -> SortOrder.SORT_ORDER_DESC;
            default -> SortOrder.SORT_ORDER_ASC;
        };
        return OrganizationSort.newBuilder()
                .setOrderField(orderField)
                .setOrder(order)
                .build();
    }

    public static  OrganizationWithUsersSort createOrganizationWithUsersSort(
            String sortField,
            String sortOrder
    ) {
        OrganizationWithUsersOrderField orderField = switch (sortField) {
            case "name" -> OrganizationWithUsersOrderField.ORGANIZATION_WITH_USERS_ORDER_FIELD_NAME;
            case "create" -> OrganizationWithUsersOrderField.ORGANIZATION_WITH_USERS_ORDER_FIELD_CREATE;
            default -> OrganizationWithUsersOrderField.ORGANIZATION_WITH_USERS_ORDER_FIELD_CREATE;
        };
        SortOrder order = switch (sortOrder) {
            case "asc" -> SortOrder.SORT_ORDER_ASC;
            case "desc" -> SortOrder.SORT_ORDER_DESC;
            default -> SortOrder.SORT_ORDER_ASC;
        };
        return OrganizationWithUsersSort.newBuilder()
                .setOrderField(orderField)
                .setOrder(order)
                .build();
    }

    public static OrganizationOnUserSort createOrganizationOnUserSort(
            String sortField,
            String sortOrder
    ) {
        OrganizationOnUserOrderField orderField = switch (sortField) {
            case "add" -> OrganizationOnUserOrderField.ORGANIZATION_ON_USER_ORDER_FIELD_ADD;
            case "name" -> OrganizationOnUserOrderField.ORGANIZATION_ON_USER_ORDER_FIELD_NAME;
            case "create" -> OrganizationOnUserOrderField.ORGANIZATION_ON_USER_ORDER_FIELD_CREATE;
            default -> OrganizationOnUserOrderField.ORGANIZATION_ON_USER_ORDER_FIELD_ADD;
        };
        SortOrder order = switch (sortOrder) {
            case "asc" -> SortOrder.SORT_ORDER_ASC;
            case "desc" -> SortOrder.SORT_ORDER_DESC;
            default -> SortOrder.SORT_ORDER_ASC;
        };
        return OrganizationOnUserSort.newBuilder()
                .setOrderField(orderField)
                .setOrder(order)
                .build();
    }

    public static UserOnOrganizationSort createUserOnOrganizationSort(
            String sortField,
            String sortOrder
    ) {
        UserOnOrganizationOrderField orderField = switch (sortField) {
            case "add" -> UserOnOrganizationOrderField.USER_ON_ORGANIZATION_ORDER_FIELD_ADD;
            default -> UserOnOrganizationOrderField.USER_ON_ORGANIZATION_ORDER_FIELD_ADD;
        };
        SortOrder order = switch (sortOrder) {
            case "asc" -> SortOrder.SORT_ORDER_ASC;
            case "desc" -> SortOrder.SORT_ORDER_DESC;
            default -> SortOrder.SORT_ORDER_ASC;
        };
        return UserOnOrganizationSort.newBuilder()
                .setOrderField(orderField)
                .setOrder(order)
                .build();
    }

    public static OrganizationFilterOwner createOrganizationFilterOwner(
            boolean hasOwnerFilter,
            List<String> filterOwnerIds
    ) {
        return OrganizationFilterOwner.newBuilder()
                .setHasValue(hasOwnerFilter)
                .addAllOwnerIds(filterOwnerIds)
                .build();
    }

    public static OrganizationFilterPlan createOrganizationFilterPlan(
            boolean hasPlanFilter,
            List<String> filterPlans
    ) {
        return OrganizationFilterPlan.newBuilder()
                .setHasValue(hasPlanFilter)
                .addAllPlans(filterPlans)
                .build();
    }

    public static OrganizationFilterUser createOrganizationFilterUser(
            boolean hasUserFilter,
            List<String> filterUserIds,
            String userFilterType
    ) {
        boolean allUserFilter = userFilterType != null && userFilterType.equals("all");
        return OrganizationFilterUser.newBuilder()
                .setHasValue(hasUserFilter)
                .addAllUserIds(filterUserIds)
                .setAny(!allUserFilter)
                .build();
    }
}
