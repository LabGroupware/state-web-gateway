package org.cresplanex.api.state.webgateway.controller;

import build.buf.gen.userpreference.v1.UserPreference;
import lombok.AllArgsConstructor;
import org.cresplanex.api.state.common.constants.WebGatewayApplicationCode;
import org.cresplanex.api.state.common.utils.ValueFromNullable;
import org.cresplanex.api.state.webgateway.composition.UserCompositionService;
import org.cresplanex.api.state.webgateway.dto.CommandResponseDto;
import org.cresplanex.api.state.webgateway.dto.ListResponseDto;
import org.cresplanex.api.state.webgateway.dto.ResponseDto;
import org.cresplanex.api.state.webgateway.dto.domain.userpreference.UserPreferenceDto;
import org.cresplanex.api.state.webgateway.dto.userpreference.UpdateUserPreferenceRequestDto;
import org.cresplanex.api.state.webgateway.proxy.command.UserPreferenceCommandServiceProxy;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user-preferences")
@AllArgsConstructor
public class UserPreferenceController {

    private final UserPreferenceCommandServiceProxy userPreferenceCommandServiceProxy;
    private final UserCompositionService userCompositionService;

    @RequestMapping(value = "/{userPreferenceId}", method = RequestMethod.PUT)
    public ResponseEntity<CommandResponseDto> updateUserPreference(
            @PathVariable String userPreferenceId,
            @RequestBody UpdateUserPreferenceRequestDto updateUserPreferenceRequestDto
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        UserPreference userPreference = UserPreference.newBuilder()
                        .setUserPreferenceId(userPreferenceId)
                        .setTimezone(ValueFromNullable.toNullableString(updateUserPreferenceRequestDto.getTimezone()))
                        .setLanguage(ValueFromNullable.toNullableString(updateUserPreferenceRequestDto.getLanguage()))
                        .setTheme(ValueFromNullable.toNullableString(updateUserPreferenceRequestDto.getTheme()))
                        .build();
        String jobId = userPreferenceCommandServiceProxy.updateUserPreference(jwt.getSubject(), userPreference);

        CommandResponseDto response = new CommandResponseDto();

        response.setSuccess(true);
        response.setData(new CommandResponseDto.InternalData(jobId));
        response.setCode(WebGatewayApplicationCode.SUCCESS);
        response.setCaption("User preference update pending.");

        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "/{userPreferenceId}", method = RequestMethod.GET)
    public ResponseEntity<ResponseDto<UserPreferenceDto>> getUserPreference(
            @PathVariable String userPreferenceId,
            @RequestParam(name = "with", required = false) List<String> with
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();

        UserPreferenceDto userPreference = userCompositionService.findUserPreference(
                jwt.getSubject(),
                userPreferenceId,
                with
        );

        ResponseDto<UserPreferenceDto> response = new ResponseDto<>();

        response.setSuccess(true);
        response.setData(userPreference);
        response.setCode(WebGatewayApplicationCode.SUCCESS);
        response.setCaption("Find user preference.");

        return ResponseEntity.ok(response);
    }
}
