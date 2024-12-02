package org.cresplanex.api.state.webgateway.proxy.query;

import build.buf.gen.cresplanex.nova.v1.SortOrder;
import build.buf.gen.team.v1.*;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.cresplanex.api.state.common.constants.TeamServiceApplicationCode;
import org.cresplanex.api.state.webgateway.composition.CompositionUtils;
import org.cresplanex.api.state.webgateway.dto.ListResponseDto;
import org.cresplanex.api.state.webgateway.dto.domain.team.TeamDto;
import org.cresplanex.api.state.webgateway.dto.domain.team.TeamOnUserProfileDto;
import org.cresplanex.api.state.webgateway.dto.domain.userprofile.UserProfileOnTeamDto;
import org.cresplanex.api.state.webgateway.exception.TeamNotFoundException;
import org.cresplanex.api.state.webgateway.mapper.CommonMapper;
import org.cresplanex.api.state.webgateway.mapper.TeamMapper;
import org.cresplanex.api.state.webgateway.mapper.UserProfileMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamQueryProxy {

    @GrpcClient("teamService")
    private TeamServiceGrpc.TeamServiceBlockingStub teamServiceBlockingStub;

    public TeamDto findTeam(
            String operatorId,
            String teamId
    ) {
        FindTeamResponse response;
        try {
            response = teamServiceBlockingStub.findTeam(
                    FindTeamRequest.newBuilder()
                            .setOperatorId(operatorId)
                            .setTeamId(teamId)
                            .build()
            );
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == io.grpc.Status.Code.NOT_FOUND) {
                throw new TeamNotFoundException(
                        TeamNotFoundException.FindType.TEAM_ID,
                        teamId,
                        TeamServiceApplicationCode.NOT_FOUND
                );
            }
            throw e;
        }
        return TeamMapper.convert(response.getTeam());
    }

    public TeamDto findTeamWithUsers(
            String operatorId,
            String teamId
    ) {
        FindTeamWithUsersResponse response;
        try {
            response = teamServiceBlockingStub.findTeamWithUsers(
                    FindTeamWithUsersRequest.newBuilder()
                            .setOperatorId(operatorId)
                            .setTeamId(teamId)
                            .build()
            );
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == io.grpc.Status.Code.NOT_FOUND) {
                throw new TeamNotFoundException(
                        TeamNotFoundException.FindType.TEAM_ID,
                        teamId,
                        TeamServiceApplicationCode.NOT_FOUND
                );
            }
            throw e;
        }
        return TeamMapper.convert(response.getTeam());
    }

    public ListResponseDto.InternalData<TeamDto> getTeams(
            String operatorId,
            int limit,
            int offset,
            String cursor,
            String pagination,
            String sortField,
            String sortOrder,
            boolean withCount,
            boolean hasIsDefaultFilter,
            boolean filterIsDefault,
            boolean hasOrganizationFilter,
            List<String> filterOrganizationIds,
            boolean hasUserFilter,
            List<String> filterUserIds,
            String userFilterType
    ) {
        GetTeamsResponse response = teamServiceBlockingStub.getTeams(
                GetTeamsRequest.newBuilder()
                        .setOperatorId(operatorId)
                        .setPagination(CompositionUtils.createPagination(limit, offset, cursor, pagination))
                        .setSort(createTeamSort(sortField, sortOrder))
                        .setWithCount(withCount)
                        .setFilterIsDefault(createTeamFilterIsDefault(hasIsDefaultFilter, filterIsDefault))
                        .setFilterOrganization(createTeamFilterOrganization(hasOrganizationFilter, filterOrganizationIds))
                        .setFilterUser(createTeamFilterUser(hasUserFilter, filterUserIds, userFilterType))
                        .build()
        );
        return new ListResponseDto.InternalData<>(
                response.getTeamsList().stream()
                        .map(TeamMapper::convert)
                        .toList(),
                CommonMapper.countConvert(response.getCount())
        );
    }

    public ListResponseDto.InternalData<TeamDto> getTeamsWithUsers(
            String operatorId,
            int limit,
            int offset,
            String cursor,
            String pagination,
            String sortField,
            String sortOrder,
            boolean withCount,
            boolean hasIsDefaultFilter,
            boolean filterIsDefault,
            boolean hasOrganizationFilter,
            List<String> filterOrganizationIds,
            boolean hasUserFilter,
            List<String> filterUserIds,
            String userFilterType
    ) {
        GetTeamsWithUsersResponse response = teamServiceBlockingStub.getTeamsWithUsers(
                GetTeamsWithUsersRequest.newBuilder()
                        .setOperatorId(operatorId)
                        .setPagination(CompositionUtils.createPagination(limit, offset, cursor, pagination))
                        .setSort(createTeamWithUsersSort(sortField, sortOrder))
                        .setWithCount(withCount)
                        .setFilterIsDefault(createTeamFilterIsDefault(hasIsDefaultFilter, filterIsDefault))
                        .setFilterOrganization(createTeamFilterOrganization(hasOrganizationFilter, filterOrganizationIds))
                        .setFilterUser(createTeamFilterUser(hasUserFilter, filterUserIds, userFilterType))
                        .build()
        );
        return new ListResponseDto.InternalData<>(
                response.getTeamsList().stream()
                        .map(TeamMapper::convert)
                        .toList(),
                CommonMapper.countConvert(response.getCount())
        );
    }

    public ListResponseDto.InternalData<TeamDto> getPluralTeams(
            String operatorId,
            List<String> teamIds,
            String sortField,
            String sortOrder
    ) {
        GetPluralTeamsResponse response = teamServiceBlockingStub.getPluralTeams(
                GetPluralTeamsRequest.newBuilder()
                        .setOperatorId(operatorId)
                        .addAllTeamIds(teamIds)
                        .setSort(createTeamSort(sortField, sortOrder))
                        .build()
        );

        return new ListResponseDto.InternalData<>(
                response.getTeamsList().stream()
                        .map(TeamMapper::convert)
                        .toList(),
                CommonMapper.invalidCountGenerate()
        );
    }

    public ListResponseDto.InternalData<TeamDto> getPluralTeamsWithUsers(
            String operatorId,
            List<String> teamIds,
            String sortField,
            String sortOrder
    ) {
        GetPluralTeamsWithUsersResponse response = teamServiceBlockingStub.getPluralTeamsWithUsers(
                GetPluralTeamsWithUsersRequest.newBuilder()
                        .setOperatorId(operatorId)
                        .addAllTeamIds(teamIds)
                        .setSort(createTeamWithUsersSort(sortField, sortOrder))
                        .build()
        );

        return new ListResponseDto.InternalData<>(
                response.getTeamsList().stream()
                        .map(TeamMapper::convert)
                        .toList(),
                CommonMapper.invalidCountGenerate()
        );
    }

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
        GetUsersOnTeamResponse response = teamServiceBlockingStub.getUsersOnTeam(
                GetUsersOnTeamRequest.newBuilder()
                        .setOperatorId(operatorId)
                        .setTeamId(teamId)
                        .setPagination(CompositionUtils.createPagination(limit, offset, cursor, pagination))
                        .setSort(createUserOnTeamSort(sortField, sortOrder))
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

    public ListResponseDto.InternalData<TeamOnUserProfileDto> getTeamsOnUser(
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
        GetTeamsOnUserResponse response = teamServiceBlockingStub.getTeamsOnUser(
                GetTeamsOnUserRequest.newBuilder()
                        .setOperatorId(operatorId)
                        .setUserId(userId)
                        .setPagination(CompositionUtils.createPagination(limit, offset, cursor, pagination))
                        .setSort(createTeamOnUserSort(sortField, sortOrder))
                        .setWithCount(withCount)
                        .build()
        );

        return new ListResponseDto.InternalData<>(
                response.getTeamsList().stream()
                        .map(TeamMapper::convert)
                        .toList(),
                CommonMapper.countConvert(response.getCount())
        );
    }

    public static TeamSort createTeamSort(
            String sortField,
            String sortOrder
    ) {
        TeamOrderField orderField = switch (sortField) {
            case "name" -> TeamOrderField.TEAM_ORDER_FIELD_NAME;
            case "create" -> TeamOrderField.TEAM_ORDER_FIELD_CREATE;
            default -> TeamOrderField.TEAM_ORDER_FIELD_CREATE;
        };
        SortOrder order = switch (sortOrder) {
            case "asc" -> SortOrder.SORT_ORDER_ASC;
            case "desc" -> SortOrder.SORT_ORDER_DESC;
            default -> SortOrder.SORT_ORDER_ASC;
        };
        return TeamSort.newBuilder()
                .setOrderField(orderField)
                .setOrder(order)
                .build();
    }

    public static TeamWithUsersSort createTeamWithUsersSort(
            String sortField,
            String sortOrder
    ) {
        TeamWithUsersOrderField orderField = switch (sortField) {
            case "name" -> TeamWithUsersOrderField.TEAM_WITH_USERS_ORDER_FIELD_NAME;
            case "create" -> TeamWithUsersOrderField.TEAM_WITH_USERS_ORDER_FIELD_CREATE;
            default -> TeamWithUsersOrderField.TEAM_WITH_USERS_ORDER_FIELD_CREATE;
        };
        SortOrder order = switch (sortOrder) {
            case "asc" -> SortOrder.SORT_ORDER_ASC;
            case "desc" -> SortOrder.SORT_ORDER_DESC;
            default -> SortOrder.SORT_ORDER_ASC;
        };
        return TeamWithUsersSort.newBuilder()
                .setOrderField(orderField)
                .setOrder(order)
                .build();
    }

    public static TeamOnUserSort createTeamOnUserSort(
            String sortField,
            String sortOrder
    ) {
        TeamOnUserOrderField orderField = switch (sortField) {
            case "add" -> TeamOnUserOrderField.TEAM_ON_USER_ORDER_FIELD_ADD;
            case "name" -> TeamOnUserOrderField.TEAM_ON_USER_ORDER_FIELD_NAME;
            case "create" -> TeamOnUserOrderField.TEAM_ON_USER_ORDER_FIELD_CREATE;
            default -> TeamOnUserOrderField.TEAM_ON_USER_ORDER_FIELD_ADD;
        };
        SortOrder order = switch (sortOrder) {
            case "asc" -> SortOrder.SORT_ORDER_ASC;
            case "desc" -> SortOrder.SORT_ORDER_DESC;
            default -> SortOrder.SORT_ORDER_ASC;
        };
        return TeamOnUserSort.newBuilder()
                .setOrderField(orderField)
                .setOrder(order)
                .build();
    }

    public static UserOnTeamSort createUserOnTeamSort(
            String sortField,
            String sortOrder
    ) {
        UserOnTeamOrderField orderField = switch (sortField) {
            case "add" -> UserOnTeamOrderField.USER_ON_TEAM_ORDER_FIELD_ADD;
            default -> UserOnTeamOrderField.USER_ON_TEAM_ORDER_FIELD_ADD;
        };
        SortOrder order = switch (sortOrder) {
            case "asc" -> SortOrder.SORT_ORDER_ASC;
            case "desc" -> SortOrder.SORT_ORDER_DESC;
            default -> SortOrder.SORT_ORDER_ASC;
        };
        return UserOnTeamSort.newBuilder()
                .setOrderField(orderField)
                .setOrder(order)
                .build();
    }

    public static TeamFilterIsDefault createTeamFilterIsDefault(
            boolean hasDefaultFilter,
            boolean filterIsDefault
    ) {
        return TeamFilterIsDefault.newBuilder()
                .setHasValue(hasDefaultFilter)
                .setIsDefault(filterIsDefault)
                .build();
    }

    public static TeamFilterOrganization createTeamFilterOrganization(
            boolean hasOrganizationFilter,
            List<String> filterOrganizations
    ) {
        return TeamFilterOrganization.newBuilder()
                .setHasValue(hasOrganizationFilter)
                .addAllOrganizationIds(filterOrganizations)
                .build();
    }

    public static TeamFilterUser createTeamFilterUser(
            boolean hasUserFilter,
            List<String> filterUserIds,
            String userFilterType
    ) {
        boolean allUserFilter = userFilterType != null && userFilterType.equals("all");
        return TeamFilterUser.newBuilder()
                .setHasValue(hasUserFilter)
                .addAllUserIds(filterUserIds)
                .setAny(!allUserFilter)
                .build();
    }
}
