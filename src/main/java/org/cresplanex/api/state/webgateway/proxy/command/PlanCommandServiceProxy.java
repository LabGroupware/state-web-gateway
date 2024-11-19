package org.cresplanex.api.state.webgateway.proxy.command;

import build.buf.gen.plan.v1.*;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
public class PlanCommandServiceProxy {

    @GrpcClient("planService")
    private PlanServiceGrpc.PlanServiceBlockingStub planServiceBlockingStub;

    public String createTask(
            String operatorId,
            TaskWithAttachments taskWithAttachments
    ) {
        CreateTaskRequest request = CreateTaskRequest.newBuilder()
                .setOperatorId(operatorId)
                .setTeamId(taskWithAttachments.getTask().getTeamId())
                .setChargeUserId(taskWithAttachments.getTask().getChargeUserId())
                .setTitle(taskWithAttachments.getTask().getTitle())
                .setDescription(taskWithAttachments.getTask().getDescription())
                .setStartDatetime(taskWithAttachments.getTask().getStartDatetime())
                .setDueDatetime(taskWithAttachments.getTask().getDueDatetime())
                .addAllAttachments(taskWithAttachments.getAttachmentsList()
                        .stream().map(attachment -> PlanAttachementRequestType.newBuilder()
                                .setFileObjectId(attachment.getFileObjectId())
                                .build())
                        .toList())
                .build();

        CreateTaskResponse response = planServiceBlockingStub.createTask(request);
        return response.getJobId();
    }

    public String updateStatusTask(
            String operatorId,
            String taskId,
            String status
    ) {
        UpdateTaskStatusRequest request = UpdateTaskStatusRequest.newBuilder()
                .setOperatorId(operatorId)
                .setTaskId(taskId)
                .setStatus(status)
                .build();

        UpdateTaskStatusResponse response = planServiceBlockingStub.updateTaskStatus(request);
        return response.getJobId();
    }
}
