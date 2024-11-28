package org.cresplanex.api.state.webgateway.controller;

import build.buf.gen.plan.v1.FileObjectOnTask;
import build.buf.gen.plan.v1.Task;
import build.buf.gen.plan.v1.TaskWithAttachments;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.cresplanex.api.state.common.constants.WebGatewayApplicationCode;
import org.cresplanex.api.state.webgateway.composition.TaskCompositionService;
import org.cresplanex.api.state.webgateway.dto.CommandResponseDto;
import org.cresplanex.api.state.webgateway.dto.ListResponseDto;
import org.cresplanex.api.state.webgateway.dto.ResponseDto;
import org.cresplanex.api.state.webgateway.dto.domain.organization.OrganizationDto;
import org.cresplanex.api.state.webgateway.dto.domain.plan.TaskDto;
import org.cresplanex.api.state.webgateway.dto.plan.CreateTaskRequestDto;
import org.cresplanex.api.state.webgateway.dto.plan.UpdateStatusTaskRequestDto;
import org.cresplanex.api.state.webgateway.proxy.command.PlanCommandServiceProxy;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/plans")
@AllArgsConstructor
public class PlanController {

    private final PlanCommandServiceProxy planCommandServiceProxy;
    private final TaskCompositionService taskCompositionService;

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

    @RequestMapping(value = "/{taskId}", method = RequestMethod.GET)
    public ResponseEntity<ResponseDto<TaskDto>> findTask(
            @PathVariable String taskId,
            @RequestParam(name = "with", required = false) List<String> with
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();

        TaskDto task = taskCompositionService.findTask(
                jwt.getSubject(),
                taskId,
                with
        );

        ResponseDto<TaskDto> response = new ResponseDto<>();
        response.setData(task);
        response.setSuccess(true);
        response.setCode(WebGatewayApplicationCode.SUCCESS);
        response.setCaption("Find Task.");

        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<ListResponseDto<TaskDto>> getTasks(
            @RequestParam(name = "teamId", required = false) String teamId,
            @RequestParam(name = "limit", required = false) int limit,
            @RequestParam(name = "offset", required = false) int offset,
            @RequestParam(name = "cursor", required = false) String cursor,
            @RequestParam(name = "pagination", required = false) String pagination,
            @RequestParam(name = "sortField", required = false) String sortField,
            @RequestParam(name = "sortOrder", required = false) String sortOrder,
            @RequestParam(name = "withCount", required = false) boolean withCount,
            @RequestParam(name = "hasStatusFilter", required = false) boolean hasStatusFilter,
            @RequestParam(name = "filterStatuses", required = false) List<String> filterStatuses,
            @RequestParam(name = "hasChargeUserFilter", required = false) boolean hasChargeUserFilter,
            @RequestParam(name = "filterChargeUserIds", required = false) List<String> filterChargeUserIds,
            @RequestParam(name = "filterStartDatetimeEarlierThan", required = false) String filterStartDatetimeEarlierThan,
            @RequestParam(name = "filterStartDatetimeLaterThan", required = false) String filterStartDatetimeLaterThan,
            @RequestParam(name = "filterDueDatetimeEarlierThan", required = false) String filterDueDatetimeEarlierThan,
            @RequestParam(name = "filterDueDatetimeLaterThan", required = false) String filterDueDatetimeLaterThan,
            @RequestParam(name = "hasFileObjectFilter", required = false) boolean hasFileObjectFilter,
            @RequestParam(name = "filterFileObjectIds", required = false) List<String> filterFileObjectIds,
            @RequestParam(name = "fileObjectFilterType", required = false) String fileObjectFilterType,
            @RequestParam(name = "with", required = false) List<String> with
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();

        ListResponseDto.InternalData<TaskDto> tasks = taskCompositionService.getTasks(
                jwt.getSubject(),
                teamId,
                limit,
                offset,
                cursor,
                pagination,
                sortField,
                sortOrder,
                withCount,
                hasStatusFilter,
                filterStatuses,
                hasChargeUserFilter,
                filterChargeUserIds,
                filterStartDatetimeEarlierThan,
                filterStartDatetimeLaterThan,
                filterDueDatetimeEarlierThan,
                filterDueDatetimeLaterThan,
                hasFileObjectFilter,
                filterFileObjectIds,
                fileObjectFilterType,
                with
        );

        ListResponseDto<TaskDto> response = new ListResponseDto<>();
        response.setData(tasks);
        response.setSuccess(true);
        response.setCode(WebGatewayApplicationCode.SUCCESS);
        response.setCaption("Get Tasks.");

        return ResponseEntity.ok(response);
    }
}
