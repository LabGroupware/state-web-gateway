package org.cresplanex.api.state.webgateway.proxy.query;

import build.buf.gen.cresplanex.nova.v1.SortOrder;
import build.buf.gen.userpreference.v1.*;
import build.buf.gen.userpreference.v1.GetPluralUserPreferencesByUserIdRequest;
import build.buf.gen.userpreference.v1.GetPluralUserPreferencesByUserIdResponse;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.cresplanex.api.state.common.constants.UserPreferenceServiceApplicationCode;
import org.cresplanex.api.state.webgateway.composition.CompositionUtils;
import org.cresplanex.api.state.webgateway.dto.ListResponseDto;
import org.cresplanex.api.state.webgateway.dto.domain.userpreference.UserPreferenceDto;
import org.cresplanex.api.state.webgateway.exception.UserNotFoundException;
import org.cresplanex.api.state.webgateway.mapper.CommonMapper;
import org.cresplanex.api.state.webgateway.mapper.UserPreferenceMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserPreferenceQueryProxy {

    @GrpcClient("userPreferenceService")
    private UserPreferenceServiceGrpc.UserPreferenceServiceBlockingStub userPreferenceServiceBlockingStub;

    public UserPreferenceDto findUserPreference(
            String operatorId,
            String userPreferenceId
    ) {
        FindUserPreferenceResponse response;
        try {
            response = userPreferenceServiceBlockingStub.findUserPreference(
                    FindUserPreferenceRequest.newBuilder()
                            .setOperatorId(operatorId)
                            .setUserPreferenceId(userPreferenceId)
                            .build()
            );
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.NOT_FOUND) {
                throw new UserNotFoundException(
                        UserNotFoundException.FindType.USER_PREFERENCE_ID,
                        userPreferenceId,
                        UserPreferenceServiceApplicationCode.NOT_FOUND
                );
            }
            throw e;
        }
        return UserPreferenceMapper.convert(response.getUserPreference());
    }

    public UserPreferenceDto findUserPreferenceByUserId(
            String operatorId,
            String userId
    ) {
        FindUserPreferenceByUserIdResponse response;
        try {
            response = userPreferenceServiceBlockingStub.findUserPreferenceByUserId(
                    FindUserPreferenceByUserIdRequest.newBuilder()
                            .setOperatorId(operatorId)
                            .setUserId(userId)
                            .build()
            );
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.NOT_FOUND) {
                throw new UserNotFoundException(
                        UserNotFoundException.FindType.USER_ID,
                        userId,
                        UserPreferenceServiceApplicationCode.NOT_FOUND
                );
            }
            throw e;
        }
        return UserPreferenceMapper.convert(response.getUserPreference());
    }

    public ListResponseDto.InternalData<UserPreferenceDto> getUserPreferences(
            String operatorId,
            int limit,
            int offset,
            String cursor,
            String pagination,
            String sortField,
            String sortOrder,
            boolean withCount,
            boolean hasLanguageFilter,
            List<String> filterLanguages
    ) {
        GetUserPreferencesResponse response = userPreferenceServiceBlockingStub.getUserPreferences(
                GetUserPreferencesRequest.newBuilder()
                        .setOperatorId(operatorId)
                        .setPagination(CompositionUtils.createPagination(limit, offset, cursor, pagination))
                        .setSort(createUserPreferenceSort(sortField, sortOrder))
                        .setWithCount(withCount)
                        .setFilterLanguage(createUserPreferenceFilterLanguage(hasLanguageFilter, filterLanguages))
                        .build()
        );
        return new ListResponseDto.InternalData<>(
                response.getUserPreferencesList().stream()
                        .map(UserPreferenceMapper::convert)
                        .toList(),
                CommonMapper.countConvert(response.getCount())
        );
    }

    public ListResponseDto.InternalData<UserPreferenceDto> getPluralUserPreferences(
            String operatorId,
            List<String> userPreferenceIds,
            String sortField,
            String sortOrder
    ) {
        GetPluralUserPreferencesResponse response = userPreferenceServiceBlockingStub.getPluralUserPreferences(
                GetPluralUserPreferencesRequest.newBuilder()
                        .setOperatorId(operatorId)
                        .addAllUserPreferenceIds(userPreferenceIds)
                        .setSort(createUserPreferenceSort(sortField, sortOrder))
                        .build()
        );

        return new ListResponseDto.InternalData<>(
                response.getUserPreferencesList().stream()
                        .map(UserPreferenceMapper::convert)
                        .toList(),
                CommonMapper.invalidCountGenerate()
        );
    }

    public ListResponseDto.InternalData<UserPreferenceDto> getPluralUserPreferencesByUserIds(
            String operatorId,
            List<String> userIds,
            String sortField,
            String sortOrder
    ) {
        GetPluralUserPreferencesByUserIdResponse response = userPreferenceServiceBlockingStub.getPluralUserPreferencesByUserId(
                GetPluralUserPreferencesByUserIdRequest.newBuilder()
                        .setOperatorId(operatorId)
                        .addAllUserIds(userIds)
                        .setSort(createUserPreferenceSort(sortField, sortOrder))
                        .build()
        );

        return new ListResponseDto.InternalData<>(
                response.getUserPreferencesList().stream()
                        .map(UserPreferenceMapper::convert)
                        .toList(),
                CommonMapper.invalidCountGenerate()
        );
    }

    public static UserPreferenceSort createUserPreferenceSort(
            String sortField,
            String sortOrder
    ) {
        UserPreferenceOrderField orderField = switch (sortField) {
            case "create" -> UserPreferenceOrderField.USER_PREFERENCE_ORDER_FIELD_CREATE;
            default -> UserPreferenceOrderField.USER_PREFERENCE_ORDER_FIELD_CREATE;
        };
        SortOrder order = switch (sortOrder) {
            case "asc" -> SortOrder.SORT_ORDER_ASC;
            case "desc" -> SortOrder.SORT_ORDER_DESC;
            default -> SortOrder.SORT_ORDER_ASC;
        };
        return UserPreferenceSort.newBuilder()
                .setOrderField(orderField)
                .setOrder(order)
                .build();
    }

    public static UserPreferenceFilterLanguage createUserPreferenceFilterLanguage(
            boolean hasLanguageFilter,
            List<String> filterLanguages
    ) {
        return UserPreferenceFilterLanguage.newBuilder()
                .setHasValue(hasLanguageFilter)
                .addAllLanguages(filterLanguages)
                .build();
    }
}
