package org.cresplanex.api.state.webgateway.controller;

import build.buf.gen.cresplanex.nova.v1.NullableString;
import build.buf.gen.team.v1.Team;
import build.buf.gen.team.v1.TeamUserRequestType;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.cresplanex.api.state.common.constants.WebGatewayApplicationCode;
import org.cresplanex.api.state.webgateway.composition.TeamCompositionService;
import org.cresplanex.api.state.webgateway.dto.CommandResponseDto;
import org.cresplanex.api.state.webgateway.dto.ListResponseDto;
import org.cresplanex.api.state.webgateway.dto.ResponseDto;
import org.cresplanex.api.state.webgateway.dto.domain.team.TeamDto;
import org.cresplanex.api.state.webgateway.dto.team.AddUsersTeamRequestDto;
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
    private final TeamCompositionService teamCompositionService;

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<CommandResponseDto> createTeam(
            @Valid @RequestBody CreateTeamRequestDto requestDTO
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Team team = Team.newBuilder()
                .setOrganizationId(requestDTO.getOrganizationId())
                .setDescription(NullableString.newBuilder().setHasValue(true).setValue(requestDTO.getDescription()).build())
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
            @Valid @RequestBody AddUsersTeamRequestDto requestDto
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        List<TeamUserRequestType> users = requestDto.getUserIds().stream()
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

    @RequestMapping(value = "/{teamId}", method = RequestMethod.GET)
    public ResponseEntity<ResponseDto<TeamDto>> findTeam(
            @PathVariable String teamId,
            @RequestParam(name = "with", required = false) List<String> with
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();

        TeamDto team = teamCompositionService.findTeam(
                jwt.getSubject(),
                teamId,
                with
        );

        ResponseDto<TeamDto> response = new ResponseDto<>();
        response.setData(team);
        response.setSuccess(true);
        response.setCode(WebGatewayApplicationCode.SUCCESS);
        response.setCaption("Find Team.");

        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<ListResponseDto<TeamDto>> getTeams(
            @RequestParam(name = "limit", required = false, defaultValue = "10") int limit,
            @RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(name = "cursor", required = false, defaultValue = "") String cursor,
            @RequestParam(name = "pagination", required = false, defaultValue = "none") String pagination,
            @RequestParam(name = "sort_field", required = false, defaultValue = "none") String sortField,
            @RequestParam(name = "sort_order", required = false, defaultValue = "asc") String sortOrder,
            @RequestParam(name = "with_count", required = false, defaultValue = "false") boolean withCount,
            @RequestParam(name = "has_is_default_filter", required = false, defaultValue = "false") boolean hasIsDefaultFilter,
            @RequestParam(name = "filter_is_default", required = false, defaultValue = "false") boolean filterIsDefault,
            @RequestParam(name = "has_organization_filter", required = false, defaultValue = "false") boolean hasOrganizationFilter,
            @RequestParam(name = "filter_organization_ids", required = false, defaultValue = "") List<String> filterOrganizationIds,
            @RequestParam(name = "has_user_filter", required = false, defaultValue = "false") boolean hasUserFilter,
            @RequestParam(name = "filter_user_ids", required = false, defaultValue = "") List<String> filterUserIds,
            @RequestParam(name = "user_filter_type", required = false, defaultValue = "none") String userFilterType,
            @RequestParam(name = "with", required = false) List<String> with
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();

        ListResponseDto.InternalData<TeamDto> teams = teamCompositionService.getTeams(
                jwt.getSubject(),
                limit,
                offset,
                cursor,
                pagination,
                sortField,
                sortOrder,
                withCount,
                hasIsDefaultFilter,
                filterIsDefault,
                hasOrganizationFilter,
                filterOrganizationIds,
                hasUserFilter,
                filterUserIds,
                userFilterType,
                with
        );

        ListResponseDto<TeamDto> response = new ListResponseDto<>();
        response.setData(teams);
        response.setSuccess(true);
        response.setCode(WebGatewayApplicationCode.SUCCESS);
        response.setCaption("Get Teams.");

        return ResponseEntity.ok(response);
    }
}
