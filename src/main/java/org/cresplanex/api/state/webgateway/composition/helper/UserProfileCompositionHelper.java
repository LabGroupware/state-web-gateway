package org.cresplanex.api.state.webgateway.composition.helper;

import org.cresplanex.api.state.webgateway.dto.domain.userprofile.UserProfileDto;
import org.cresplanex.api.state.webgateway.hasher.UserProfileHasher;
import org.cresplanex.api.state.webgateway.proxy.query.UserProfileQueryProxy;
import org.cresplanex.api.state.webgateway.retriever.RetrievedCacheContainer;
import org.cresplanex.api.state.webgateway.retriever.domain.UserProfileRetriever;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserProfileCompositionHelper {

    public static int calculateNeedQuery(List<UserProfileRetriever> retrievers) {
        int needQuery = 0;
//        for (UserProfileRetriever retriever : retrievers) {
//        }
        return needQuery;
    }

    public static Map<String, UserProfileDto> createUserProfileDtoMap(
            UserProfileQueryProxy userProfileQueryProxy,
            RetrievedCacheContainer cache,
            String operatorId,
            List<String> userIds,
            List<UserProfileRetriever> retrievers
    ) {
        int need = calculateNeedQuery(retrievers);
        List<String> needRetrieveAttachedUserIds = new ArrayList<>();
        Map<String, UserProfileDto> userProfileDtoMap = new HashMap<>();
        List<UserProfileDto> userProfile;

        switch (need) {
            default:
                for (String userId : userIds) {
                    if (cache.getCache().containsKey(UserProfileHasher.hashUserProfileByUserId(userId))) {
                        userProfileDtoMap.put(userId, ((UserProfileDto) cache.getCache().get(UserProfileHasher.hashUserProfileByUserId(userId))).deepClone());
                        break;
                    } else {
                        needRetrieveAttachedUserIds.add(userId);
                    }
                }

                if (!needRetrieveAttachedUserIds.isEmpty()) {
                    userProfile = userProfileQueryProxy.getPluralUserProfilesByUserIds(
                            operatorId,
                            needRetrieveAttachedUserIds,
                            "none",
                            "asc"
                    ).getListData();
                    for (UserProfileDto dto : userProfile) {
                        userProfileDtoMap.put(dto.getUserId(), dto);
                        cache.getCache().put(UserProfileHasher.hashUserProfileByUserId(dto.getUserId()), dto.deepClone());
                    }
                }
                break;
        }

        return userProfileDtoMap;
    }
}
