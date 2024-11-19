package org.cresplanex.api.state.webgateway.controller;

import build.buf.gen.userpreference.v1.UserPreference;
import lombok.AllArgsConstructor;
import org.cresplanex.api.state.common.constants.WebGatewayApplicationCode;
import org.cresplanex.api.state.common.utils.ValueFromNullable;
import org.cresplanex.api.state.webgateway.dto.CommandResponseDto;
import org.cresplanex.api.state.webgateway.dto.userpreference.UpdateUserPreferenceRequestDto;
import org.cresplanex.api.state.webgateway.proxy.command.UserPreferenceCommandServiceProxy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user-preferences")
@AllArgsConstructor
public class UserPreferenceController {

    private final UserPreferenceCommandServiceProxy userPreferenceCommandServiceProxy;

    @RequestMapping(value = "/{userPreferenceId}", method = RequestMethod.PUT)
    public ResponseEntity<CommandResponseDto> updateUserPreference(
            @PathVariable String userPreferenceId,
            @RequestBody UpdateUserPreferenceRequestDto updateUserPreferenceRequestDto
    ) {
        UserPreference userPreference = UserPreference.newBuilder()
                        .setUserPreferenceId(userPreferenceId)
                        .setTimezone(ValueFromNullable.toNullableString(updateUserPreferenceRequestDto.getTimezone()))
                        .setLanguage(ValueFromNullable.toNullableString(updateUserPreferenceRequestDto.getLanguage()))
                        .setTheme(ValueFromNullable.toNullableString(updateUserPreferenceRequestDto.getTheme()))
                        .build();
        String jobId = userPreferenceCommandServiceProxy.updateUserPreference(userPreferenceId, userPreference);

        CommandResponseDto response = new CommandResponseDto();

        response.setSuccess(true);
        response.setData(new CommandResponseDto.Data(jobId));
        response.setCode(WebGatewayApplicationCode.SUCCESS);
        response.setCaption("User preference update pending.");

        return ResponseEntity.ok(response);
    }
}
