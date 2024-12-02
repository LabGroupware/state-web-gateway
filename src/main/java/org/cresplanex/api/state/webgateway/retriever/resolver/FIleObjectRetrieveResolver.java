package org.cresplanex.api.state.webgateway.retriever.resolver;

import org.cresplanex.api.state.webgateway.dto.domain.plan.TaskOnFileObjectDto;
import org.cresplanex.api.state.webgateway.dto.domain.storage.FileObjectDto;
import org.cresplanex.api.state.webgateway.retriever.ListRelationRetrieverBuilder;
import org.cresplanex.api.state.webgateway.retriever.domain.FileObjectRetriever;
import org.cresplanex.api.state.webgateway.retriever.domain.TaskRetriever;

import java.util.HashSet;
import java.util.Set;

public class FIleObjectRetrieveResolver {

    public static FileObjectRetriever resolve(String ...path) {
        FileObjectRetriever fileObjectRetriever = new FileObjectRetriever();
        Result result = getResult(path);

        if (!result.attachedTasksRelationPath.isEmpty()) {
            fileObjectRetriever.setAttachedTasksRelationRetriever(
                    ListRelationRetrieverBuilder.<TaskOnFileObjectDto, FileObjectDto, TaskRetriever>builder()
                            .idRetriever(dto -> dto.getAttachedTasks().isHasValue() ?
                                    dto.getAttachedTasks().getValue().stream().map(TaskOnFileObjectDto::getTaskId).toList() : null)
                            .relationRetriever(FileObjectDto::getAttachedTasks)
                            .chain(
                                    result.attachedTasksRelationPath.stream()
                                            .filter(s -> !s.isEmpty())
                                            .map(TaskRetrieveResolver::resolve).toArray(TaskRetriever[]::new)
                            )
                            .build());
        }

        return fileObjectRetriever;
    }

    private static Result getResult(String[] path) {
        Set<String> attachedTasksRelationPath = new HashSet<>();

        for (String p : path) {
            if (p == null) {
                continue;
            }
            if (p.startsWith(String.format("%s", FileObjectRetriever.ATTACHED_TASKS_RELATION))) {
                String subPath = p.substring(FileObjectRetriever.ATTACHED_TASKS_RELATION.length());
                if (subPath.startsWith(".")) {
                    attachedTasksRelationPath.add(subPath.substring(1));
                }else {
                    attachedTasksRelationPath.add("");
                }
            }
        }
        return new Result(attachedTasksRelationPath);
    }

    private record Result(Set<String> attachedTasksRelationPath) {
    }
}