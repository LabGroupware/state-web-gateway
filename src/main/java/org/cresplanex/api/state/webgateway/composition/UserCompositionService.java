package org.cresplanex.api.state.webgateway.composition;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cresplanex.api.state.webgateway.composition.attach.AttachRelationUserPreference;
import org.cresplanex.api.state.webgateway.composition.attach.AttachRelationUserProfile;
import org.cresplanex.api.state.webgateway.composition.helper.UserPreferenceCompositionHelper;
import org.cresplanex.api.state.webgateway.composition.helper.UserProfileCompositionHelper;
import org.cresplanex.api.state.webgateway.dto.ListResponseDto;
import org.cresplanex.api.state.webgateway.dto.domain.userpreference.UserPreferenceDto;
import org.cresplanex.api.state.webgateway.dto.domain.userprofile.UserProfileDto;
import org.cresplanex.api.state.webgateway.hasher.UserPreferenceHasher;
import org.cresplanex.api.state.webgateway.hasher.UserProfileHasher;
import org.cresplanex.api.state.webgateway.proxy.query.UserPreferenceQueryProxy;
import org.cresplanex.api.state.webgateway.proxy.query.UserProfileQueryProxy;
import org.cresplanex.api.state.webgateway.retriever.RetrievedCacheContainer;
import org.cresplanex.api.state.webgateway.retriever.domain.UserPreferenceRetriever;
import org.cresplanex.api.state.webgateway.retriever.domain.UserProfileRetriever;
import org.cresplanex.api.state.webgateway.retriever.resolver.UserPreferenceRetrieveResolver;
import org.cresplanex.api.state.webgateway.retriever.resolver.UserProfileRetrieveResolver;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserCompositionService {

    private final UserProfileQueryProxy userProfileQueryProxy;
    private final UserPreferenceQueryProxy userPreferenceQueryProxy;
    private final AttachRelationUserProfile attachRelationUserProfile;
    private final AttachRelationUserPreference attachRelationUserPreference;

    public UserProfileDto findUserProfile(String operatorId, String userId, List<String> with) {
        UserProfileDto userProfile;
        UserProfileRetriever userProfileRetriever = UserProfileRetrieveResolver.resolve(
                with != null ? with.toArray(new String[0]) : new String[0]
        );
        int need = UserProfileCompositionHelper.calculateNeedQuery(List.of(userProfileRetriever));
        RetrievedCacheContainer cache = new RetrievedCacheContainer();
        switch (need) {
            default:
                userProfile = userProfileQueryProxy.findUserProfileByUserId(
                        operatorId,
                        userId
                );
                cache.getCache().put(UserProfileHasher.hashUserProfileByUserId(userId), userProfile.deepClone());
                break;
        }

        attachRelationUserProfile.attach(
                operatorId,
                cache,
                userProfileRetriever,
                userProfile
        );

        return userProfile;
    }

    public UserPreferenceDto findUserPreference(String operatorId, String userPreferenceId, List<String> with) {
        UserPreferenceDto userPreference;
        UserPreferenceRetriever userPreferenceRetriever = UserPreferenceRetrieveResolver.resolve(
                with != null ? with.toArray(new String[0]) : new String[0]
        );
        int need = UserPreferenceCompositionHelper.calculateNeedQuery(List.of(userPreferenceRetriever));
        RetrievedCacheContainer cache = new RetrievedCacheContainer();
        switch (need) {
            default:
                userPreference = userPreferenceQueryProxy.findUserPreference(
                        operatorId,
                        userPreferenceId
                );
                cache.getCache().put(UserPreferenceHasher.hashUserPreference(userPreferenceId), userPreference.deepClone());
                break;
        }

        attachRelationUserPreference.attach(
                operatorId,
                cache,
                userPreferenceRetriever,
                userPreference
        );

        return userPreference;
    }

    public ListResponseDto.InternalData<UserProfileDto> getUsers(
            String operatorId,
            int limit,
            int offset,
            String cursor,
            String pagination,
            String sortField,
            String sortOrder,
            boolean withCount,
            List<String> with
    ) {
        ListResponseDto.InternalData<UserProfileDto> userProfiles;
        UserProfileRetriever userProfileRetriever = UserProfileRetrieveResolver.resolve(
                with != null ? with.toArray(new String[0]) : new String[0]
        );
        int need = UserProfileCompositionHelper.calculateNeedQuery(List.of(userProfileRetriever));
        RetrievedCacheContainer cache = new RetrievedCacheContainer();
        switch (need) {
            default:
                userProfiles = userProfileQueryProxy.getUserProfiles(
                        operatorId,
                        limit,
                        offset,
                        cursor,
                        pagination,
                        sortField,
                        sortOrder,
                        withCount
                );
                for (UserProfileDto dto : userProfiles.getListData()) {
                    cache.getCache().put(UserProfileHasher.hashUserProfileByUserId(dto.getUserId()), dto.deepClone());
                    cache.getCache().put(UserProfileHasher.hashUserProfile(dto.getUserProfileId()), dto.deepClone());
                }
                break;
        }

        attachRelationUserProfile.attach(
                operatorId,
                cache,
                userProfileRetriever,
                userProfiles.getListData()
        );

        return userProfiles;
    }
}
