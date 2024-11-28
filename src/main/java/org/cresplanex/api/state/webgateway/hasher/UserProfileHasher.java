package org.cresplanex.api.state.webgateway.hasher;

public class UserProfileHasher {

    public static final String HASHER_PREFIX = "UserProfile";

    public static String hashUserProfile(String userProfileId) {
        return String.format("%s:hashUserProfile:%s", HASHER_PREFIX, userProfileId);
    }

    public static String hashUserProfileByUserId(String userId) {
        return String.format("%s:hashUserProfileByUserId:%s", HASHER_PREFIX, userId);
    }
}
