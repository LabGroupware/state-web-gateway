package org.cresplanex.api.state.webgateway.retriever.resolver;

import org.cresplanex.api.state.webgateway.dto.domain.organization.OrganizationDto;
import org.cresplanex.api.state.webgateway.dto.domain.team.TeamDto;
import org.cresplanex.api.state.webgateway.dto.domain.userprofile.UserProfileDto;
import org.cresplanex.api.state.webgateway.dto.domain.userprofile.UserProfileOnOrganizationDto;
import org.cresplanex.api.state.webgateway.retriever.*;
import org.cresplanex.api.state.webgateway.retriever.domain.*;

import java.util.HashSet;
import java.util.Set;

public class OrganizationRetrieveResolver {

    public static OrganizationRetriever resolve(String ...path) {
        OrganizationRetriever organizationRetriever = new OrganizationRetriever();
        Result result = getResult(path);

        if (!result.usersRelationPath.isEmpty()) {
            organizationRetriever.setUsersRelationRetriever(
                    ListRelationRetrieverBuilder.<UserProfileOnOrganizationDto, OrganizationDto, UserProfileRetriever>builder()
                            .idRetriever(dto -> dto.getUsers().isHasValue() ?
                                    dto.getUsers().getValue().stream().map(UserProfileOnOrganizationDto::getUserId).toList() : null)
                            .relationRetriever(OrganizationDto::getUsers)
                            .chain(
                                    result.usersRelationPath.stream()
                                            .filter(s -> !s.isEmpty())
                                            .map(UserProfileRetrieveResolver::resolve).toArray(UserProfileRetriever[]::new)
                            )
                            .build());
        }
        if (!result.ownerRelationPath.isEmpty()) {
            organizationRetriever.setOwnerRelationRetriever(
                    RelationRetrieverBuilder.<UserProfileDto, OrganizationDto, UserProfileRetriever>builder()
                            .idRetriever(OrganizationDto::getOwnerId)
                            .relationRetriever(OrganizationDto::getOwner)
                            .chain(
                                    result.ownerRelationPath.stream()
                                            .filter(s -> !s.isEmpty())
                                            .map(UserProfileRetrieveResolver::resolve).toArray(UserProfileRetriever[]::new)
                            ).build());
        }
        if (!result.teamsRelationPath.isEmpty()) {
            organizationRetriever.setTeamsRelationRetriever(
                    ListRelationRetrieverBuilder.<TeamDto, OrganizationDto, TeamRetriever>builder()
                            .idRetriever(dto -> dto.getTeams().isHasValue() ?
                                    dto.getTeams().getValue().stream().map(TeamDto::getTeamId).toList() : null)
                            .relationRetriever(OrganizationDto::getTeams)
                            .chain(
                                    result.teamsRelationPath.stream()
                                            .filter(s -> !s.isEmpty())
                                            .map(TeamRetrieveResolver::resolve).toArray(TeamRetriever[]::new)
                            ).build());
        }

        return organizationRetriever;
    }

    private static Result getResult(String[] path) {
        Set<String> usersRelationPath = new HashSet<>();
        Set<String> ownerRelationPath = new HashSet<>();
        Set<String> teamsRelationPath = new HashSet<>();

        for (String p : path) {
            if (p == null) {
                continue;
            }
            if (p.startsWith(String.format("%s", OrganizationRetriever.USERS_RELATION))) {
                String subPath = p.substring(OrganizationRetriever.USERS_RELATION.length() + 1);
                if (subPath.startsWith(".")) {
                    usersRelationPath.add(subPath.substring(1));
                }else {
                    usersRelationPath.add("");
                }
            } else if (p.startsWith(String.format("%s", OrganizationRetriever.OWNER_RELATION))) {
                String subPath = p.substring(OrganizationRetriever.OWNER_RELATION.length() + 1);
                if (subPath.startsWith(".")) {
                    ownerRelationPath.add(subPath.substring(1));
                }else {
                    ownerRelationPath.add("");
                }
            } else if (p.startsWith(String.format("%s", OrganizationRetriever.TEAMS_RELATION))) {
                String subPath = p.substring(OrganizationRetriever.TEAMS_RELATION.length() + 1);
                if (subPath.startsWith(".")) {
                    teamsRelationPath.add(subPath.substring(1));
                }else {
                    teamsRelationPath.add("");
                }
            }
        }
        return new Result(usersRelationPath, ownerRelationPath, teamsRelationPath);
    }

    private record Result(Set<String> usersRelationPath, Set<String> ownerRelationPath, Set<String> teamsRelationPath) {
    }
}
