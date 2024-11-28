package org.cresplanex.api.state.webgateway.retriever.domain;

import lombok.Getter;
import lombok.Setter;
import org.cresplanex.api.state.webgateway.dto.domain.plan.TaskDto;
import org.cresplanex.api.state.webgateway.dto.domain.storage.FileObjectOnTaskDto;
import org.cresplanex.api.state.webgateway.dto.domain.team.TeamDto;
import org.cresplanex.api.state.webgateway.dto.domain.userprofile.UserProfileDto;
import org.cresplanex.api.state.webgateway.retriever.ListRelationRetriever;
import org.cresplanex.api.state.webgateway.retriever.RelationRetriever;
import org.cresplanex.api.state.webgateway.retriever.Retriever;

@Getter
@Setter
public class TaskRetriever implements Retriever<TaskDto> {

    public static final String ROOT_PATH = "tasks";

    public static final String ATTACHMENTS_RELATION = "attachments";
    private ListRelationRetriever<FileObjectOnTaskDto, TaskDto, FileObjectRetriever> attachmentsRelationRetriever;

    public static final String TEAM_RELATION = "team";
    private RelationRetriever<TeamDto, TaskDto, TeamRetriever> teamRelationRetriever;

    public static final String CHARGE_USER_RELATION = "chargeUser";
    private RelationRetriever<UserProfileDto, TaskDto, UserProfileRetriever> chargeUserRelationRetriever;
}
