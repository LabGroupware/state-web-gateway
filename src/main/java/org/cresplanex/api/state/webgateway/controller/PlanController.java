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
@RequestMapping("/tasks")
@AllArgsConstructor
public class PlanController {

    private final PlanCommandServiceProxy planCommandServiceProxy;
    private final TaskCompositionService taskCompositionService;

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<CommandResponseDto> createTask(
            @Valid @RequestBody CreateTaskRequestDto requestDTO
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
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
        String jobId = planCommandServiceProxy.createTask(jwt.getSubject(), task);

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
            @Valid @RequestBody UpdateStatusTaskRequestDto requestDTO
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String jobId = planCommandServiceProxy.updateStatusTask(jwt.getSubject(), taskId, requestDTO.getStatus());

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
            @RequestParam(name = "limit", required = false, defaultValue = "10") int limit,
            @RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(name = "cursor", required = false, defaultValue = "") String cursor,
            @RequestParam(name = "pagination", required = false, defaultValue = "none") String pagination,
            @RequestParam(name = "sortField", required = false, defaultValue = "none") String sortField,
            @RequestParam(name = "sortOrder", required = false, defaultValue = "asc") String sortOrder,
            @RequestParam(name = "withCount", required = false, defaultValue = "false") boolean withCount,
            @RequestParam(name = "hasTeamFilter", required = false, defaultValue = "false") boolean hasTeamFilter,
            @RequestParam(name = "filterTeamIds", required = false, defaultValue = "") List<String> filterTeamIds,
            @RequestParam(name = "hasStatusFilter", required = false, defaultValue = "false") boolean hasStatusFilter,
            @RequestParam(name = "filterStatuses", required = false, defaultValue = "") List<String> filterStatuses,
            @RequestParam(name = "hasChargeUserFilter", required = false, defaultValue = "false") boolean hasChargeUserFilter,
            @RequestParam(name = "filterChargeUserIds", required = false, defaultValue = "") List<String> filterChargeUserIds,
            @RequestParam(name = "filterStartDatetimeEarlierThan", required = false, defaultValue = "") String filterStartDatetimeEarlierThan,
            @RequestParam(name = "filterStartDatetimeLaterThan", required = false, defaultValue = "") String filterStartDatetimeLaterThan,
            @RequestParam(name = "filterDueDatetimeEarlierThan", required = false, defaultValue = "") String filterDueDatetimeEarlierThan,
            @RequestParam(name = "filterDueDatetimeLaterThan", required = false, defaultValue = "") String filterDueDatetimeLaterThan,
            @RequestParam(name = "hasFileObjectFilter", required = false, defaultValue = "false") boolean hasFileObjectFilter,
            @RequestParam(name = "filterFileObjectIds", required = false, defaultValue = "") List<String> filterFileObjectIds,
            @RequestParam(name = "fileObjectFilterType", required = false, defaultValue = "none") String fileObjectFilterType,
            @RequestParam(name = "with", required = false) List<String> with
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();

        ListResponseDto.InternalData<TaskDto> tasks = taskCompositionService.getTasks(
                jwt.getSubject(),
                limit,
                offset,
                cursor,
                pagination,
                sortField,
                sortOrder,
                withCount,
                hasTeamFilter,
                filterTeamIds,
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
