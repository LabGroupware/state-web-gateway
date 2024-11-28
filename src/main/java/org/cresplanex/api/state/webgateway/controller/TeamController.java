package org.cresplanex.api.state.webgateway.controller;

import build.buf.gen.team.v1.Team;
import build.buf.gen.team.v1.TeamUserRequestType;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.cresplanex.api.state.common.constants.WebGatewayApplicationCode;
import org.cresplanex.api.state.webgateway.dto.CommandResponseDto;
import org.cresplanex.api.state.webgateway.dto.team.CreateTeamRequestDto;
import org.cresplanex.api.state.webgateway.proxy.command.TeamCommandServiceProxy;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/teams")
@AllArgsConstructor
public class TeamController {

    private final TeamCommandServiceProxy teamCommandServiceProxy;

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<CommandResponseDto> createTeam(
            @Valid @RequestBody CreateTeamRequestDto requestDTO
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Team team = Team.newBuilder()
                .setName(requestDTO.getName())
                .build();
        List<TeamUserRequestType> users = requestDTO.getUserIds().stream()
                .map(user -> TeamUserRequestType.newBuilder()
                        .setUserId(user)
                        .build())
                .toList();
        String jobId = teamCommandServiceProxy.createTeam(jwt.getSubject(), team, users);

        CommandResponseDto response = new CommandResponseDto();

        response.setSuccess(true);
        response.setData(new CommandResponseDto.InternalData(jobId));
        response.setCode(WebGatewayApplicationCode.SUCCESS);
        response.setCaption("Team create pending.");

        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "/{teamId}/users", method = RequestMethod.POST)
    public ResponseEntity<CommandResponseDto> addUsersToTeam(
            @PathVariable String teamId,
            @Valid @RequestBody List<String> userIds
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        List<TeamUserRequestType> users = userIds.stream()
                .map(user -> TeamUserRequestType.newBuilder()
                        .setUserId(user)
                        .build())
                .toList();
        String jobId = teamCommandServiceProxy.addUsersToTeam(jwt.getSubject(), teamId, users);

        CommandResponseDto response = new CommandResponseDto();

        response.setSuccess(true);
        response.setData(new CommandResponseDto.InternalData(jobId));
        response.setCode(WebGatewayApplicationCode.SUCCESS);
        response.setCaption("Team user add pending.");

        return ResponseEntity.ok(response);
    }
}
