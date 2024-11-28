package org.cresplanex.api.state.webgateway.retriever.resolver;

import org.cresplanex.api.state.webgateway.dto.domain.organization.OrganizationDto;
import org.cresplanex.api.state.webgateway.dto.domain.plan.TaskDto;
import org.cresplanex.api.state.webgateway.dto.domain.team.TeamDto;
import org.cresplanex.api.state.webgateway.dto.domain.userprofile.UserProfileOnTeamDto;
import org.cresplanex.api.state.webgateway.retriever.*;
import org.cresplanex.api.state.webgateway.retriever.domain.OrganizationRetriever;
import org.cresplanex.api.state.webgateway.retriever.domain.TaskRetriever;
import org.cresplanex.api.state.webgateway.retriever.domain.TeamRetriever;
import org.cresplanex.api.state.webgateway.retriever.domain.UserProfileRetriever;

import java.util.HashSet;
import java.util.Set;

public class TeamRetrieveResolver {

    public static TeamRetriever resolve(String ...path) {
        TeamRetriever teamRetriever = new TeamRetriever();
        Result result = getResult(path);

        if (!result.usersRelationPath.isEmpty()) {
            teamRetriever.setUsersRelationRetriever(
                    ListRelationRetrieverBuilder.<UserProfileOnTeamDto, TeamDto, UserProfileRetriever>builder()
                            .idRetriever(dto -> dto.getUsers().isHasValue() ?
                                    dto.getUsers().getValue().stream().map(UserProfileOnTeamDto::getUserId).toList() : null)
                            .relationRetriever(TeamDto::getUsers)
                            .chain(
                                    result.usersRelationPath.stream()
                                            .filter(s -> !s.isEmpty())
                                            .map(UserProfileRetrieveResolver::resolve).toArray(UserProfileRetriever[]::new)
                            ).build());
        }
        if (!result.organizationRelationPath.isEmpty()) {
            teamRetriever.setOrganizationRelationRetriever(
                    RelationRetrieverBuilder.<OrganizationDto, TeamDto, OrganizationRetriever>builder()
                            .idRetriever(TeamDto::getOrganizationId)
                            .relationRetriever(TeamDto::getOrganization)
                            .chain(
                                    result.organizationRelationPath.stream()
                                            .filter(s -> !s.isEmpty())
                                            .map(OrganizationRetrieveResolver::resolve).toArray(OrganizationRetriever[]::new)
                            ).build());
        }
        if (!result.tasksRelationPath.isEmpty()) {
            teamRetriever.setTasksRelationRetriever(
                    ListRelationRetrieverBuilder.<TaskDto, TeamDto, TaskRetriever>builder()
                            .idRetriever(dto -> dto.getTasks().isHasValue() ?
                                    dto.getTasks().getValue().stream().map(TaskDto::getTaskId).toList() : null)
                            .relationRetriever(TeamDto::getTasks)
                            .chain(
                                    result.tasksRelationPath.stream()
                                            .filter(s -> !s.isEmpty())
                                            .map(TaskRetrieveResolver::resolve).toArray(TaskRetriever[]::new)
                            ).build());
        }

        return teamRetriever;
    }

    private static Result getResult(String[] path) {
        Set<String> usersRelationPath = new HashSet<>();
        Set<String> organizationRelationPath = new HashSet<>();
        Set<String> tasksRelationPath = new HashSet<>();

        for (String p : path) {
            if (p == null) {
                continue;
            }
            if (p.startsWith(String.format("%s", TeamRetriever.USERS_RELATION))) {
                String subPath = p.substring(TeamRetriever.USERS_RELATION.length() + 1);
                if (subPath.startsWith(".")) {
                    usersRelationPath.add(subPath.substring(1));
                }else {
                    usersRelationPath.add("");
                }
            } else if (p.startsWith(String.format("%s", TeamRetriever.ORGANIZATION_RELATION))) {
                String subPath = p.substring(TeamRetriever.ORGANIZATION_RELATION.length() + 1);
                if (subPath.startsWith(".")) {
                    organizationRelationPath.add(subPath.substring(1));
                }else {
                    organizationRelationPath.add("");
                }
            } else if (p.startsWith(String.format("%s", TeamRetriever.TASKS_RELATION))) {
                String subPath = p.substring(TeamRetriever.TASKS_RELATION.length() + 1);
                if (subPath.startsWith(".")) {
                    tasksRelationPath.add(subPath.substring(1));
                }else {
                    tasksRelationPath.add("");
                }
            }
        }
        return new Result(usersRelationPath, organizationRelationPath, tasksRelationPath);
    }

    private record Result(Set<String> usersRelationPath, Set<String> organizationRelationPath, Set<String> tasksRelationPath) {
    }
}
