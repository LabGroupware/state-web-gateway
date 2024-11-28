package org.cresplanex.api.state.webgateway.retriever.domain;

import lombok.Getter;
import lombok.Setter;
import org.cresplanex.api.state.webgateway.dto.domain.organization.OrganizationDto;
import org.cresplanex.api.state.webgateway.dto.domain.team.TeamDto;
import org.cresplanex.api.state.webgateway.dto.domain.userprofile.UserProfileDto;
import org.cresplanex.api.state.webgateway.dto.domain.userprofile.UserProfileOnOrganizationDto;
import org.cresplanex.api.state.webgateway.retriever.ListRelationRetriever;
import org.cresplanex.api.state.webgateway.retriever.RelationRetriever;
import org.cresplanex.api.state.webgateway.retriever.Retriever;

@Setter
@Getter
public class OrganizationRetriever implements Retriever<OrganizationDto> {

    public static final String ROOT_PATH = "organizations";

    public static final String USERS_RELATION = "users";
    private ListRelationRetriever<UserProfileOnOrganizationDto, OrganizationDto, UserProfileRetriever> usersRelationRetriever;

    public static final String OWNER_RELATION = "owner";
    private RelationRetriever<UserProfileDto, OrganizationDto, UserProfileRetriever> ownerRelationRetriever;

    public static final String TEAMS_RELATION = "teams";
    private ListRelationRetriever<TeamDto, OrganizationDto, TeamRetriever> teamsRelationRetriever;
}
