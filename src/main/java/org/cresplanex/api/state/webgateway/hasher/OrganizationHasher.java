package org.cresplanex.api.state.webgateway.hasher;

public class OrganizationHasher {

    public static final String HASHER_PREFIX = "Organization";

    public static String hashOrganization(String organizationId) {
        return String.format("%s:hashOrganization:%s", HASHER_PREFIX, organizationId);
    }

    public static String hashOrganizationWithUsers(String organizationId) {
        return String.format("%s:hashOrganizationWithUsers:%s", HASHER_PREFIX, organizationId);
    }
}
