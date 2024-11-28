package org.cresplanex.api.state.webgateway.hasher;

public class TeamHasher {

    public static final String HASHER_PREFIX = "Team";

    public static String hashTeam(String teamId) {
        return String.format("%s:hashTeam:%s", HASHER_PREFIX, teamId);
    }

    public static String hashTeamWithUsers(String teamId) {
        return String.format("%s:hashTeamWithUsers:%s", HASHER_PREFIX, teamId);
    }
}
