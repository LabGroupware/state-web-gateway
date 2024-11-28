package org.cresplanex.api.state.webgateway.retriever.domain;

import lombok.Getter;
import lombok.Setter;
import org.cresplanex.api.state.webgateway.dto.domain.organization.OrganizationDto;
import org.cresplanex.api.state.webgateway.dto.domain.plan.TaskDto;
import org.cresplanex.api.state.webgateway.dto.domain.team.TeamDto;
import org.cresplanex.api.state.webgateway.dto.domain.userprofile.UserProfileOnTeamDto;
import org.cresplanex.api.state.webgateway.retriever.ListRelationRetriever;
import org.cresplanex.api.state.webgateway.retriever.RelationRetriever;
import org.cresplanex.api.state.webgateway.retriever.Retriever;

@Getter
@Setter
public class TeamRetriever implements Retriever<TeamDto> {

    public static final String ROOT_PATH = "teams";

    public static final String USERS_RELATION = "users";
    private ListRelationRetriever<UserProfileOnTeamDto, TeamDto, UserProfileRetriever> usersRelationRetriever;

    public static final String ORGANIZATION_RELATION = "organization";
    private RelationRetriever<OrganizationDto, TeamDto, OrganizationRetriever> organizationRelationRetriever;

    public static final String TASKS_RELATION = "tasks";
    private ListRelationRetriever<TaskDto, TeamDto, TaskRetriever> tasksRelationRetriever;
}
