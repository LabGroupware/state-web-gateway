package org.cresplanex.api.state.webgateway.retriever.domain;

import lombok.Getter;
import lombok.Setter;
import org.cresplanex.api.state.webgateway.dto.domain.plan.TaskOnFileObjectDto;
import org.cresplanex.api.state.webgateway.dto.domain.storage.FileObjectDto;
import org.cresplanex.api.state.webgateway.retriever.ListRelationRetriever;
import org.cresplanex.api.state.webgateway.retriever.Retriever;

@Getter
@Setter
public class FileObjectRetriever implements Retriever<FileObjectDto> {

    public static final String ROOT_PATH = "fileObjects";

    public static final String ATTACHED_TASKS_RELATION = "attachedTasks";
    private ListRelationRetriever<TaskOnFileObjectDto, FileObjectDto, TaskRetriever> attachedTasksRelationRetriever;
}
