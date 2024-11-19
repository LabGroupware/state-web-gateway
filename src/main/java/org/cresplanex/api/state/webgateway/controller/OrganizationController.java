package org.cresplanex.api.state.webgateway.controller;

import build.buf.gen.organization.v1.Organization;
import build.buf.gen.organization.v1.OrganizationWithUsers;
import build.buf.gen.organization.v1.UserOnOrganization;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cresplanex.api.state.common.constants.WebGatewayApplicationCode;
import org.cresplanex.api.state.webgateway.dto.CommandResponseDto;
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

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<CommandResponseDto> createOrganization(
            @Valid @RequestBody CreateOrganizationRequestDto requestDTO
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        OrganizationWithUsers organization = OrganizationWithUsers.newBuilder()
                .setOrganization(Organization.newBuilder()
                        .setName(requestDTO.getName())
                        .setPlan(requestDTO.getPlan())
                        .build()
                )
                .addAllUsers(
                        requestDTO.getUserIds().stream()
                                .map(user -> UserOnOrganization
                                        .newBuilder()
                                        .setUserId(user)
                                        .build())
                                .toList()
                )
                .build();
        String jobId = organizationCommandServiceProxy.createOrganization(jwt.getSubject(), organization);

        CommandResponseDto response = new CommandResponseDto();

        response.setSuccess(true);
        response.setData(new CommandResponseDto.Data(jobId));
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
        List<UserOnOrganization> users = requestDTO.getUserIds().stream()
                .map(user -> UserOnOrganization.newBuilder()
                        .setUserId(user)
                        .build())
                .toList();
        String jobId = organizationCommandServiceProxy.addUsersToOrganization(jwt.getSubject(), organizationId, users);

        CommandResponseDto response = new CommandResponseDto();

        response.setSuccess(true);
        response.setData(new CommandResponseDto.Data(jobId));
        response.setCode(WebGatewayApplicationCode.SUCCESS);
        response.setCaption("Organization user add pending.");

        return ResponseEntity.ok(response);
    }
}
