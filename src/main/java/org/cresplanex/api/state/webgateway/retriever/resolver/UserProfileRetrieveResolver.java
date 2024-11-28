package org.cresplanex.api.state.webgateway.retriever.resolver;

import org.cresplanex.api.state.webgateway.dto.domain.organization.OrganizationDto;
import org.cresplanex.api.state.webgateway.dto.domain.organization.OrganizationOnUserProfileDto;
import org.cresplanex.api.state.webgateway.dto.domain.plan.TaskDto;
import org.cresplanex.api.state.webgateway.dto.domain.team.TeamOnUserProfileDto;
import org.cresplanex.api.state.webgateway.dto.domain.userpreference.UserPreferenceDto;
import org.cresplanex.api.state.webgateway.dto.domain.userprofile.UserProfileDto;
import org.cresplanex.api.state.webgateway.retriever.*;
import org.cresplanex.api.state.webgateway.retriever.domain.*;

import java.util.HashSet;
import java.util.Set;

public class UserProfileRetrieveResolver {

    public static UserProfileRetriever resolve(String ...path) {
        UserProfileRetriever userProfileRetriever = new UserProfileRetriever();
        Result result = getResult(path);

        if (!result.userPreferenceRelationPath.isEmpty()) {
            userProfileRetriever.setUserPreferenceRelationRetriever(
                    RelationRetrieverBuilder.<UserPreferenceDto, UserProfileDto, UserPreferenceRetriever>builder()
                            .idRetriever(UserProfileDto::getUserId)
                            .relationRetriever(UserProfileDto::getUserPreference)
                            .chain(
                                    result.userPreferenceRelationPath.stream()
                                            .filter(s -> !s.isEmpty())
                                            .map(UserPreferenceRetrieveResolver::resolve).toArray(UserPreferenceRetriever[]::new)
                            ).build());
        }
        if (!result.organizationsRelationPath.isEmpty()) {
            userProfileRetriever.setOrganizationsRelationRetriever(
                    ListRelationRetrieverBuilder.<OrganizationOnUserProfileDto, UserProfileDto, OrganizationRetriever>builder()
                            .idRetriever(dto -> dto.getOrganizations().isHasValue() ?
                                    dto.getOrganizations().getValue().stream().map(OrganizationOnUserProfileDto::getOrganizationId).toList() : null)
                            .relationRetriever(UserProfileDto::getOrganizations)
                            .chain(
                                    result.organizationsRelationPath.stream()
                                            .filter(s -> !s.isEmpty())
                                            .map(OrganizationRetrieveResolver::resolve).toArray(OrganizationRetriever[]::new)
                            ).build());
        }
        if (!result.teamsRelationPath.isEmpty()) {
            userProfileRetriever.setTeamsRelationRetriever(
                    ListRelationRetrieverBuilder.<TeamOnUserProfileDto, UserProfileDto, TeamRetriever>builder()
                            .idRetriever(dto -> dto.getTeams().isHasValue() ?
                                    dto.getTeams().getValue().stream().map(TeamOnUserProfileDto::getTeamId).toList() : null)
                            .relationRetriever(UserProfileDto::getTeams)
                            .chain(
                                    result.teamsRelationPath.stream()
                                            .filter(s -> !s.isEmpty())
                                            .map(TeamRetrieveResolver::resolve).toArray(TeamRetriever[]::new)
                            ).build());
        }
        if (!result.ownedOrganizationsRelationPath.isEmpty()) {
            userProfileRetriever.setOwnedOrganizationsRelationRetriever(
                    ListRelationRetrieverBuilder.<OrganizationDto, UserProfileDto, OrganizationRetriever>builder()
                            .idRetriever(dto -> dto.getOwnedOrganizations().isHasValue() ?
                                    dto.getOwnedOrganizations().getValue().stream().map(OrganizationDto::getOrganizationId).toList() : null)
                            .relationRetriever(UserProfileDto::getOwnedOrganizations)
                            .chain(
                                    result.ownedOrganizationsRelationPath.stream()
                                            .filter(s -> !s.isEmpty())
                                            .map(OrganizationRetrieveResolver::resolve).toArray(OrganizationRetriever[]::new)
                            ).build());
        }
        if (!result.chargeTasksRelationPath.isEmpty()) {
            userProfileRetriever.setChargeTasksRelationRetriever(
                    ListRelationRetrieverBuilder.<TaskDto, UserProfileDto, TaskRetriever>builder()
                            .idRetriever(dto -> dto.getChargeTasks().isHasValue() ?
                                    dto.getChargeTasks().getValue().stream().map(TaskDto::getTaskId).toList() : null)
                            .relationRetriever(UserProfileDto::getChargeTasks)
                            .chain(
                                    result.chargeTasksRelationPath.stream()
                                            .filter(s -> !s.isEmpty())
                                            .map(TaskRetrieveResolver::resolve).toArray(TaskRetriever[]::new)
                            ).build());
        }

        return userProfileRetriever;
    }

    private static Result getResult(String[] path) {
        Set<String> userPreferenceRelationPath = new HashSet<>();
        Set<String> organizationsRelationPath = new HashSet<>();
        Set<String> teamsRelationPath = new HashSet<>();
        Set<String> ownedOrganizationsRelationPath = new HashSet<>();
        Set<String> chargeTasksRelationPath = new HashSet<>();

        for (String p : path) {
            if (p == null) {
                continue;
            }
            if (p.startsWith(String.format("%s", UserProfileRetriever.USER_PREFERENCE_RELATION))) {
                String subPath = p.substring(UserProfileRetriever.USER_PREFERENCE_RELATION.length() + 1);
                if (subPath.startsWith(".")) {
                    userPreferenceRelationPath.add(subPath.substring(1));
                }else {
                    userPreferenceRelationPath.add("");
                }
            } else if (p.startsWith(String.format("%s", UserProfileRetriever.ORGANIZATIONS_RELATION))) {
                String subPath = p.substring(UserProfileRetriever.ORGANIZATIONS_RELATION.length() + 1);
                if (subPath.startsWith(".")) {
                    organizationsRelationPath.add(subPath.substring(1));
                }else {
                    organizationsRelationPath.add("");
                }
            } else if (p.startsWith(String.format("%s", UserProfileRetriever.TEAMS_RELATION))) {
                String subPath = p.substring(UserProfileRetriever.TEAMS_RELATION.length() + 1);
                if (subPath.startsWith(".")) {
                    teamsRelationPath.add(subPath.substring(1));
                }else {
                    teamsRelationPath.add("");
                }
            } else if (p.startsWith(String.format("%s", UserProfileRetriever.OWNED_ORGANIZATIONS_RELATION))) {
                String subPath = p.substring(UserProfileRetriever.OWNED_ORGANIZATIONS_RELATION.length() + 1);
                if (subPath.startsWith(".")) {
                    ownedOrganizationsRelationPath.add(subPath.substring(1));
                }else {
                    ownedOrganizationsRelationPath.add("");
                }
            } else if (p.startsWith(String.format("%s", UserProfileRetriever.CHARGE_TASKS_RELATION))) {
                String subPath = p.substring(UserProfileRetriever.CHARGE_TASKS_RELATION.length() + 1);
                if (subPath.startsWith(".")) {
                    chargeTasksRelationPath.add(subPath.substring(1));
                }else {
                    chargeTasksRelationPath.add("");
                }
            }
        }
        return new Result(userPreferenceRelationPath, organizationsRelationPath, teamsRelationPath, ownedOrganizationsRelationPath, chargeTasksRelationPath);
    }

    private record Result(Set<String> userPreferenceRelationPath, Set<String> organizationsRelationPath, Set<String> teamsRelationPath, Set<String> ownedOrganizationsRelationPath, Set<String> chargeTasksRelationPath) {
    }
}
