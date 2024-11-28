package org.cresplanex.api.state.webgateway.proxy.query;

import build.buf.gen.cresplanex.nova.v1.SortOrder;
import build.buf.gen.userprofile.v1.*;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.cresplanex.api.state.webgateway.composition.CompositionUtils;
import org.cresplanex.api.state.webgateway.dto.ListResponseDto;
import org.cresplanex.api.state.webgateway.dto.domain.userprofile.UserProfileDto;
import org.cresplanex.api.state.webgateway.mapper.CommonMapper;
import org.cresplanex.api.state.webgateway.mapper.UserProfileMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserProfileQueryProxy {

    @GrpcClient("userProfileService")
    private UserProfileServiceGrpc.UserProfileServiceBlockingStub userProfileServiceBlockingStub;

    public UserProfileDto findUserProfile(
            String operatorId,
            String userProfileId
    ) {
        FindUserProfileResponse response = userProfileServiceBlockingStub.findUserProfile(
                FindUserProfileRequest.newBuilder()
                        .setOperatorId(operatorId)
                        .setUserProfileId(userProfileId)
                        .build()
        );
        return UserProfileMapper.convert(response.getUserProfile());
    }

    public UserProfileDto findUserProfileByUserId(
            String operatorId,
            String userId
    ) {
        FindUserProfileByUserIdResponse response = userProfileServiceBlockingStub.findUserProfileByUserId(
                FindUserProfileByUserIdRequest.newBuilder()
                        .setOperatorId(operatorId)
                        .setUserId(userId)
                        .build()
        );
        return UserProfileMapper.convert(response.getUserProfile());
    }

    public UserProfileDto findUserProfileByUserEmail(
            String operatorId,
            String userEmail
    ) {
        FindUserProfileByEmailResponse response = userProfileServiceBlockingStub.findUserProfileByEmail(
                FindUserProfileByEmailRequest.newBuilder()
                        .setOperatorId(operatorId)
                        .setEmail(userEmail)
                        .build()
        );
        return UserProfileMapper.convert(response.getUserProfile());
    }

    public ListResponseDto.InternalData<UserProfileDto> getUserProfiles(
            String operatorId,
            int limit,
            int offset,
            String cursor,
            String pagination,
            String sortField,
            String sortOrder,
            boolean withCount
    ) {
        GetUserProfilesResponse response = userProfileServiceBlockingStub.getUserProfiles(
                GetUserProfilesRequest.newBuilder()
                        .setOperatorId(operatorId)
                        .setPagination(CompositionUtils.createPagination(limit, offset, cursor, pagination))
                        .setSort(createUserProfileSort(sortField, sortOrder))
                        .setWithCount(withCount)
                        .build()
        );
        return new ListResponseDto.InternalData<>(
                response.getUserProfilesList().stream()
                        .map(UserProfileMapper::convert)
                        .toList(),
                CommonMapper.countConvert(response.getCount())
        );
    }

    public ListResponseDto.InternalData<UserProfileDto> getPluralUserProfiles(
            String operatorId,
            List<String> userProfileIds,
            String sortField,
            String sortOrder
    ) {
        GetPluralUserProfilesResponse response = userProfileServiceBlockingStub.getPluralUserProfiles(
                GetPluralUserProfilesRequest.newBuilder()
                        .setOperatorId(operatorId)
                        .addAllUserProfileIds(userProfileIds)
                        .setSort(createUserProfileSort(sortField, sortOrder))
                        .build()
        );

        return new ListResponseDto.InternalData<>(
                response.getUserProfilesList().stream()
                        .map(UserProfileMapper::convert)
                        .toList(),
                CommonMapper.invalidCountGenerate()
        );
    }

    public ListResponseDto.InternalData<UserProfileDto> getPluralUserProfilesByUserIds(
            String operatorId,
            List<String> userIds,
            String sortField,
            String sortOrder
    ) {
        GetPluralUserProfilesByUserIdResponse response = userProfileServiceBlockingStub.getPluralUserProfilesByUserId(
                GetPluralUserProfilesByUserIdRequest.newBuilder()
                        .setOperatorId(operatorId)
                        .addAllUserIds(userIds)
                        .setSort(createUserProfileSort(sortField, sortOrder))
                        .build()
        );

        return new ListResponseDto.InternalData<>(
                response.getUserProfilesList().stream()
                        .map(UserProfileMapper::convert)
                        .toList(),
                CommonMapper.invalidCountGenerate()
        );
    }

    public static UserProfileSort createUserProfileSort(
            String sortField,
            String sortOrder
    ) {
        UserProfileOrderField orderField = switch (sortField) {
            case "name" -> UserProfileOrderField.USER_PROFILE_ORDER_FIELD_NAME;
            case "create" -> UserProfileOrderField.USER_PROFILE_ORDER_FIELD_CREATE;
            default -> UserProfileOrderField.USER_PROFILE_ORDER_FIELD_CREATE;
        };
        SortOrder order = switch (sortOrder) {
            case "asc" -> SortOrder.SORT_ORDER_ASC;
            case "desc" -> SortOrder.SORT_ORDER_DESC;
            default -> SortOrder.SORT_ORDER_ASC;
        };
        return UserProfileSort.newBuilder()
                .setOrderField(orderField)
                .setOrder(order)
                .build();
    }
}