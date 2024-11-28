package org.cresplanex.api.state.webgateway.controller;

import build.buf.gen.plan.v1.FileObjectOnTask;
import build.buf.gen.plan.v1.Task;
import build.buf.gen.plan.v1.TaskWithAttachments;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.cresplanex.api.state.common.constants.WebGatewayApplicationCode;
import org.cresplanex.api.state.webgateway.dto.CommandResponseDto;
import org.cresplanex.api.state.webgateway.dto.plan.CreateTaskRequestDto;
import org.cresplanex.api.state.webgateway.dto.plan.UpdateStatusTaskRequestDto;
import org.cresplanex.api.state.webgateway.proxy.command.PlanCommandServiceProxy;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/plans")
@AllArgsConstructor
public class PlanController {

    private final PlanCommandServiceProxy planCommandServiceProxy;

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<CommandResponseDto> createTask(
            @Valid @RequestBody CreateTaskRequestDto requestDTO,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        TaskWithAttachments task = TaskWithAttachments.newBuilder()
                .setTask(Task.newBuilder()
                        .setTeamId(requestDTO.getTeamId())
                        .setChargeUserId(requestDTO.getChargeUserId())
                        .setTitle(requestDTO.getTitle())
                        .setDescription(requestDTO.getDescription())
                        .setStartDatetime(requestDTO.getStartDatetime())
                        .setDueDatetime(requestDTO.getDueDatetime())
                        .build()
                )
                .addAllAttachments(
                        requestDTO.getAttachmentIds().stream()
                                .map(attachment -> FileObjectOnTask
                                        .newBuilder()
                                        .setFileObjectId(attachment)
                                        .build())
                                .toList()
                )
                .build();
        String jobId = planCommandServiceProxy.createTask(userDetails.getUsername(), task);

        CommandResponseDto response = new CommandResponseDto();

        response.setSuccess(true);
        response.setData(new CommandResponseDto.InternalData(jobId));
        response.setCode(WebGatewayApplicationCode.SUCCESS);
        response.setCaption("Plan create pending.");

        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "/{taskId}/status", method = RequestMethod.PUT)
    public ResponseEntity<CommandResponseDto> updateStatusTask(
            @PathVariable String taskId,
            @Valid @RequestBody UpdateStatusTaskRequestDto requestDTO,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String jobId = planCommandServiceProxy.updateStatusTask(userDetails.getUsername(), taskId, requestDTO.getStatus());

        CommandResponseDto response = new CommandResponseDto();

        response.setSuccess(true);
        response.setData(new CommandResponseDto.InternalData(jobId));
        response.setCode(WebGatewayApplicationCode.SUCCESS);
        response.setCaption("Plan status update pending.");

        return ResponseEntity.ok(response);
    }
}
