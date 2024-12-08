package org.cresplanex.api.state.webgateway.composition.helper;

import org.cresplanex.api.state.webgateway.dto.domain.userpreference.UserPreferenceDto;
import org.cresplanex.api.state.webgateway.hasher.UserPreferenceHasher;
import org.cresplanex.api.state.webgateway.proxy.query.UserPreferenceQueryProxy;
import org.cresplanex.api.state.webgateway.retriever.RetrievedCacheContainer;
import org.cresplanex.api.state.webgateway.retriever.domain.UserPreferenceRetriever;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class UserPreferenceCompositionHelper {

    public static int calculateNeedQuery(List<UserPreferenceRetriever> retrievers) {
        int needQuery = 0;
//        for (UserPreferenceRetriever retriever : retrievers) {
//        }
        return needQuery;
    }

    public static Map<String, UserPreferenceDto> createUserPreferenceDtoMap(
            UserPreferenceQueryProxy userPreferenceQueryProxy,
            RetrievedCacheContainer cache,
            String operatorId,
            List<String> userIds,
            List<UserPreferenceRetriever> retrievers
    ) {
        int need = calculateNeedQuery(retrievers);
        List<String> needRetrieveAttachedUserIds = new ArrayList<>();
        Map<String, UserPreferenceDto> userPreferenceDtoMap = new HashMap<>();
        List<UserPreferenceDto> userPreference;

        switch (need) {
            default:
                for (String userId : userIds) {
                    if (cache.getCache().containsKey(UserPreferenceHasher.hashUserPreferenceByUserId(userId))) {
                        userPreferenceDtoMap.put(userId, ((UserPreferenceDto) cache.getCache().get(UserPreferenceHasher.hashUserPreferenceByUserId(userId))).deepClone());
                    } else {
                        needRetrieveAttachedUserIds.add(userId);
                    }
                }

                if (!needRetrieveAttachedUserIds.isEmpty()) {
                    userPreference = userPreferenceQueryProxy.getPluralUserPreferencesByUserIds(
                            operatorId,
                            needRetrieveAttachedUserIds,
                            "none",
                            "asc"
                    ).getListData();
                    for (UserPreferenceDto dto : userPreference) {
                        userPreferenceDtoMap.put(dto.getUserId(), dto);
                        cache.getCache().put(UserPreferenceHasher.hashUserPreferenceByUserId(dto.getUserId()), dto.deepClone());
                        cache.getCache().put(UserPreferenceHasher.hashUserPreference(dto.getUserPreferenceId()), dto.deepClone());
                    }
                }
                break;
        }

        return userPreferenceDtoMap;
    }
}
