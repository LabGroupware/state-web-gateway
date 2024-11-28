package org.cresplanex.api.state.webgateway.controller;

import build.buf.gen.organization.v1.Organization;
import build.buf.gen.organization.v1.OrganizationUserRequestType;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cresplanex.api.state.common.constants.WebGatewayApplicationCode;
import org.cresplanex.api.state.webgateway.composition.OrganizationCompositionService;
import org.cresplanex.api.state.webgateway.dto.CommandResponseDto;
import org.cresplanex.api.state.webgateway.dto.ListResponseDto;
import org.cresplanex.api.state.webgateway.dto.ResponseDto;
import org.cresplanex.api.state.webgateway.dto.domain.organization.OrganizationDto;
import org.cresplanex.api.state.webgateway.dto.organization.AddUsersOrganizationRequestDto;
import org.cresplanex.api.state.webgateway.dto.organization.CreateOrganizationRequestDto;
import org.cresplanex.api.state.webgateway.proxy.command.OrganizationCommandServiceProxy;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/organizations")
@AllArgsConstructor
public class OrganizationController {

    private final OrganizationCommandServiceProxy organizationCommandServiceProxy;
    private final OrganizationCompositionService organizationCompositionService;

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<CommandResponseDto> createOrganization(
            @Valid @RequestBody CreateOrganizationRequestDto requestDTO
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Organization organization = Organization.newBuilder()
                .setName(requestDTO.getName())
                .setPlan(requestDTO.getPlan())
                .build();
        List<OrganizationUserRequestType> users = requestDTO.getUserIds().stream()
                .map(user -> OrganizationUserRequestType.newBuilder()
                        .setUserId(user)
                        .build())
                .toList();
        String jobId = organizationCommandServiceProxy.createOrganization(jwt.getSubject(), organization, users);

        CommandResponseDto response = new CommandResponseDto();

        response.setSuccess(true);
        response.setData(new CommandResponseDto.InternalData(jobId));
        response.setCode(WebGatewayApplicationCode.SUCCESS);
        response.setCaption("Organization create pending.");

        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "/{organizationId}/users", method = RequestMethod.POST)
    public ResponseEntity<CommandResponseDto> addUsersToOrganization(
            @Valid @RequestBody AddUsersOrganizationRequestDto requestDTO,
            @PathVariable String organizationId
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        List<OrganizationUserRequestType> users = requestDTO.getUserIds().stream()
                .map(user -> OrganizationUserRequestType.newBuilder()
                        .setUserId(user)
                        .build())
                .toList();
        String jobId = organizationCommandServiceProxy.addUsersToOrganization(jwt.getSubject(), organizationId, users);

        CommandResponseDto response = new CommandResponseDto();

        response.setSuccess(true);
        response.setData(new CommandResponseDto.InternalData(jobId));
        response.setCode(WebGatewayApplicationCode.SUCCESS);
        response.setCaption("Organization user add pending.");

        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "/{organizationId}", method = RequestMethod.GET)
    public ResponseEntity<ResponseDto<OrganizationDto>> findOrganization(
            @PathVariable String organizationId,
            @RequestParam(name = "with", required = false) List<String> with
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();

        OrganizationDto organization = organizationCompositionService.findOrganization(
                jwt.getSubject(),
                organizationId,
                with
        );
        ResponseDto<OrganizationDto> response = new ResponseDto<>();
        response.setData(organization);
        response.setSuccess(true);
        response.setCode(WebGatewayApplicationCode.SUCCESS);
        response.setCaption("Find Organization.");

        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<ListResponseDto<OrganizationDto>> getOrganizations(
            @RequestParam(name = "limit", required = false, defaultValue = "10") int limit,
            @RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(name = "cursor", required = false, defaultValue = "") String cursor,
            @RequestParam(name = "pagination", required = false, defaultValue = "none") String pagination,
            @RequestParam(name = "sort_field", required = false) String sortField,
            @RequestParam(name = "sort_order", required = false, defaultValue = "asc") String sortOrder,
            @RequestParam(name = "with_count", required = false, defaultValue = "false") boolean withCount,
            @RequestParam(name = "has_owner_filter", required = false, defaultValue = "false") boolean hasOwnerFilter,
            @RequestParam(name = "filter_owner_ids", required = false) List<String> filterOwnerIds,
            @RequestParam(name = "has_plan_filter", required = false, defaultValue = "false") boolean hasPlanFilter,
            @RequestParam(name = "filter_plans", required = false) List<String> filterPlans,
            @RequestParam(name = "has_user_filter", required = false, defaultValue = "false") boolean hasUserFilter,
            @RequestParam(name = "filter_user_ids", required = false) List<String> filterUserIds,
            @RequestParam(name = "user_filter_type", required = false, defaultValue = "any") String userFilterType,
            @RequestParam(name = "with", required = false) List<String> with
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();

        ListResponseDto.InternalData<OrganizationDto> organizations = organizationCompositionService.getOrganizations(
                jwt.getSubject(),
                limit,
                offset,
                cursor,
                pagination,
                sortField,
                sortOrder,
                withCount,
                hasOwnerFilter,
                filterOwnerIds,
                hasPlanFilter,
                filterPlans,
                hasUserFilter,
                filterUserIds,
                userFilterType,
                with
        );

        ListResponseDto<OrganizationDto> response = new ListResponseDto<>();
        response.setData(organizations);
        response.setSuccess(true);
        response.setCode(WebGatewayApplicationCode.SUCCESS);
        response.setCaption("Get Organizations list.");

        return ResponseEntity.ok(response);
    }
}
