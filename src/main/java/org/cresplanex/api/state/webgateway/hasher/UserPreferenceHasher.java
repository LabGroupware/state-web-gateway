package org.cresplanex.api.state.webgateway.hasher;

public class UserPreferenceHasher {

    public static final String HASHER_PREFIX = "UserPreference";

    public static String hashUserPreference(String userPreferenceId) {
        return String.format("%s:hashUserPreference:%s", HASHER_PREFIX, userPreferenceId);
    }

    public static String hashUserPreferenceByUserId(String userId) {
        return String.format("%s:hashUserPreferenceByUserId:%s", HASHER_PREFIX, userId);
    }
}
