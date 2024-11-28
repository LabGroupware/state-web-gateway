package org.cresplanex.api.state.webgateway.retriever.domain;

import lombok.Getter;
import lombok.Setter;
import org.cresplanex.api.state.webgateway.dto.domain.organization.OrganizationDto;
import org.cresplanex.api.state.webgateway.dto.domain.organization.OrganizationOnUserProfileDto;
import org.cresplanex.api.state.webgateway.dto.domain.plan.TaskDto;
import org.cresplanex.api.state.webgateway.dto.domain.team.TeamOnUserProfileDto;
import org.cresplanex.api.state.webgateway.dto.domain.userpreference.UserPreferenceDto;
import org.cresplanex.api.state.webgateway.dto.domain.userprofile.UserProfileDto;
import org.cresplanex.api.state.webgateway.retriever.ListRelationRetriever;
import org.cresplanex.api.state.webgateway.retriever.RelationRetriever;
import org.cresplanex.api.state.webgateway.retriever.Retriever;

@Getter
@Setter
public class UserProfileRetriever implements Retriever<UserProfileDto> {

    public static final String ROOT_PATH = "userProfiles";

    public static final String USER_PREFERENCE_RELATION = "userPreference";
    private RelationRetriever<UserPreferenceDto, UserProfileDto, UserPreferenceRetriever> userPreferenceRelationRetriever;

    public static final String ORGANIZATIONS_RELATION = "organizations";
    private ListRelationRetriever<OrganizationOnUserProfileDto, UserProfileDto, OrganizationRetriever> organizationsRelationRetriever;

    public static final String TEAMS_RELATION = "teams";
    private ListRelationRetriever<TeamOnUserProfileDto, UserProfileDto, TeamRetriever> teamsRelationRetriever;

    public static final String OWNED_ORGANIZATIONS_RELATION = "ownedOrganizations";
    private ListRelationRetriever<OrganizationDto, UserProfileDto, OrganizationRetriever> ownedOrganizationsRelationRetriever;

    public static final String CHARGE_TASKS_RELATION = "chargeTasks";
    private ListRelationRetriever<TaskDto, UserProfileDto, TaskRetriever> chargeTasksRelationRetriever;
}
