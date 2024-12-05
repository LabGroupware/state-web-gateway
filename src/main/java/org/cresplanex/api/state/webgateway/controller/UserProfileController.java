package org.cresplanex.api.state.webgateway.controller;

import build.buf.gen.team.v1.Team;
import build.buf.gen.team.v1.TeamUserRequestType;
import build.buf.gen.userprofile.v1.UserProfile;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cresplanex.api.state.common.constants.WebGatewayApplicationCode;
import org.cresplanex.api.state.webgateway.composition.UserCompositionService;
import org.cresplanex.api.state.webgateway.dto.CommandResponseDto;
import org.cresplanex.api.state.webgateway.dto.ListResponseDto;
import org.cresplanex.api.state.webgateway.dto.ResponseDto;
import org.cresplanex.api.state.webgateway.dto.domain.userprofile.UserProfileDto;
import org.cresplanex.api.state.webgateway.dto.team.CreateTeamRequestDto;
import org.cresplanex.api.state.webgateway.dto.userprofile.CreateUserProfileRequestDto;
import org.cresplanex.api.state.webgateway.proxy.command.UserPreferenceCommandServiceProxy;
import org.cresplanex.api.state.webgateway.proxy.command.UserProfileCommandServiceProxy;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user-profiles")
@AllArgsConstructor
public class UserProfileController {

    private final UserProfileCommandServiceProxy userProfileCommandServiceProxy;
    private final UserCompositionService userCompositionService;

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<CommandResponseDto> createUserProfile(
            @Valid @RequestBody CreateUserProfileRequestDto requestDTO
            ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        UserProfile userProfile = UserProfile.newBuilder()
                .setName(requestDTO.getName())
                .setEmail(requestDTO.getEmail())
                .setUserId(requestDTO.getUserId())
                .build();


        String jobId = userProfileCommandServiceProxy.createUserProfile(jwt.getSubject(), userProfile);

        CommandResponseDto response = new CommandResponseDto();

        response.setSuccess(true);
        response.setData(new CommandResponseDto.InternalData(jobId));
        response.setCode(WebGatewayApplicationCode.SUCCESS);
        response.setCaption("User profile create pending.");

        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    public ResponseEntity<ResponseDto<UserProfileDto>> findUserProfile(
            @PathVariable String userId,
            @RequestParam(name = "with", required = false) List<String> with
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        UserProfileDto userProfile = userCompositionService.findUserProfile(
                jwt.getSubject(),
                userId,
                with
        );
        ResponseDto<UserProfileDto> response = new ResponseDto<>();

        response.setSuccess(true);
        response.setData(userProfile);
        response.setCode(WebGatewayApplicationCode.SUCCESS);
        response.setCaption("Find user profile.");
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<ListResponseDto<UserProfileDto>> getUserProfiles(
            @RequestParam(name = "limit", required = false, defaultValue = "10") int limit,
            @RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(name = "cursor", required = false, defaultValue = "") String cursor,
            @RequestParam(name = "pagination", required = false, defaultValue = "none") String pagination,
            @RequestParam(name = "sort_field", required = false, defaultValue = "none") String sortField,
            @RequestParam(name = "sort_order", required = false, defaultValue = "asc") String sortOrder,
            @RequestParam(name = "with_count", required = false, defaultValue = "false") boolean withCount,
            @RequestParam(name = "with", required = false) List<String> with
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();

        ListResponseDto.InternalData<UserProfileDto> userProfiles = userCompositionService.getUsers(
                jwt.getSubject(),
                limit,
                offset,
                cursor,
                pagination,
                sortField,
                sortOrder,
                withCount,
                with
        );
        ListResponseDto<UserProfileDto> response = new ListResponseDto<>();

        response.setSuccess(true);
        response.setData(userProfiles);
        response.setCode(WebGatewayApplicationCode.SUCCESS);
        response.setCaption("Get user profile.");
        return ResponseEntity.ok(response);
    }
}
