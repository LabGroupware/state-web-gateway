package org.cresplanex.api.state.webgateway.controller;

import build.buf.gen.userprofile.v1.UserProfile;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cresplanex.api.state.common.constants.WebGatewayApplicationCode;
import org.cresplanex.api.state.webgateway.composition.UserCompositionService;
import org.cresplanex.api.state.webgateway.dto.ListResponseDto;
import org.cresplanex.api.state.webgateway.dto.ResponseDto;
import org.cresplanex.api.state.webgateway.dto.domain.userprofile.UserProfileDto;
import org.cresplanex.api.state.webgateway.proxy.command.UserPreferenceCommandServiceProxy;
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

    private final UserPreferenceCommandServiceProxy userPreferenceCommandServiceProxy;
    private final UserCompositionService userCompositionService;

    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    public ResponseEntity<ResponseDto<UserProfileDto>> getUserProfile(
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
    public ResponseEntity<ListResponseDto<UserProfileDto>> getUserProfile(
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
