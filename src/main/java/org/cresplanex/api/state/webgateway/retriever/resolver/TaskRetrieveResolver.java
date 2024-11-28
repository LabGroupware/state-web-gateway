package org.cresplanex.api.state.webgateway.retriever.resolver;

import org.cresplanex.api.state.webgateway.dto.domain.plan.TaskDto;
import org.cresplanex.api.state.webgateway.dto.domain.storage.FileObjectOnTaskDto;
import org.cresplanex.api.state.webgateway.dto.domain.team.TeamDto;
import org.cresplanex.api.state.webgateway.dto.domain.userprofile.UserProfileDto;
import org.cresplanex.api.state.webgateway.retriever.*;
import org.cresplanex.api.state.webgateway.retriever.domain.*;

import java.util.HashSet;
import java.util.Set;

public class TaskRetrieveResolver {

    public static TaskRetriever resolve(String ...path) {
        TaskRetriever taskRetriever = new TaskRetriever();
        Result result = getResult(path);

        if (!result.attachmentsRelationPath.isEmpty()) {
            taskRetriever.setAttachmentsRelationRetriever(
                    ListRelationRetrieverBuilder.<FileObjectOnTaskDto, TaskDto, FileObjectRetriever>builder()
                            .idRetriever(dto -> dto.getAttachments().isHasValue() ?
                                    dto.getAttachments().getValue().stream().map(FileObjectOnTaskDto::getFileObjectId).toList() : null)
                            .relationRetriever(TaskDto::getAttachments)
                            .chain(
                                    result.attachmentsRelationPath.stream()
                                            .filter(s -> !s.isEmpty())
                                            .map(FIleObjectRetrieveResolver::resolve).toArray(FileObjectRetriever[]::new)
                            ).build());
        }
        if (!result.teamRelationPath.isEmpty()) {
            taskRetriever.setTeamRelationRetriever(
                    RelationRetrieverBuilder.<TeamDto, TaskDto, TeamRetriever>builder()
                            .idRetriever(TaskDto::getTeamId)
                            .relationRetriever(TaskDto::getTeam)
                            .chain(
                                    result.teamRelationPath.stream()
                                            .filter(s -> !s.isEmpty())
                                            .map(TeamRetrieveResolver::resolve).toArray(TeamRetriever[]::new)
                            ).build());
        }
        if (!result.chargeUserRelationPath.isEmpty()) {
            taskRetriever.setChargeUserRelationRetriever(
                    RelationRetrieverBuilder.<UserProfileDto, TaskDto, UserProfileRetriever>builder()
                            .idRetriever(TaskDto::getChargeUserId)
                            .relationRetriever(TaskDto::getChargeUser)
                            .chain(
                                    result.chargeUserRelationPath.stream()
                                            .filter(s -> !s.isEmpty())
                                            .map(UserProfileRetrieveResolver::resolve).toArray(UserProfileRetriever[]::new)
                            ).build());
        }

        return taskRetriever;
    }

    private static Result getResult(String[] path) {

        Set<String> attachmentsRelationPath = new HashSet<>();
        Set<String> teamRelationPath = new HashSet<>();
        Set<String> chargeUserRelationPath = new HashSet<>();

        for (String p : path) {
            if (p == null) {
                continue;
            }
            if (p.startsWith(String.format("%s", TaskRetriever.ATTACHMENTS_RELATION))) {
                String subPath = p.substring(TaskRetriever.ATTACHMENTS_RELATION.length() + 1);
                if (subPath.startsWith(".")) {
                    attachmentsRelationPath.add(subPath.substring(1));
                }else {
                    attachmentsRelationPath.add("");
                }
            } else if (p.startsWith(String.format("%s", TaskRetriever.TEAM_RELATION))) {
                String subPath = p.substring(TaskRetriever.TEAM_RELATION.length() + 1);
                if (subPath.startsWith(".")) {
                    teamRelationPath.add(subPath.substring(1));
                }else {
                    teamRelationPath.add("");
                }
            } else if (p.startsWith(String.format("%s", TaskRetriever.CHARGE_USER_RELATION))) {
                String subPath = p.substring(TaskRetriever.CHARGE_USER_RELATION.length() + 1);
                if (subPath.startsWith(".")) {
                    chargeUserRelationPath.add(subPath.substring(1));
                }else {
                    chargeUserRelationPath.add("");
                }
            }
        }
        return new Result(attachmentsRelationPath, teamRelationPath, chargeUserRelationPath);
    }

    private record Result(Set<String> attachmentsRelationPath, Set<String> teamRelationPath, Set<String> chargeUserRelationPath) {
    }
}