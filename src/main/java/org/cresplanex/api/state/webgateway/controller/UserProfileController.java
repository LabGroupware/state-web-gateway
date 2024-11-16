package org.cresplanex.api.state.webgateway.controller;

import lombok.AllArgsConstructor;
import org.cresplanex.api.state.common.constants.WebGatewayApplicationCode;
import org.cresplanex.api.state.webgateway.dto.ResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user-profiles")
@AllArgsConstructor
public class UserProfileController {

    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
//    public ResponseEntity<ResponseDto<UserProfileResponseDto>> getUserProfile(
//            @RequestParam String userId
//    ) {
    public ResponseEntity<ResponseDto<String>> getUserProfile(
                @PathVariable String userId
    ) {
        ResponseDto<String> response = new ResponseDto<>();

        response.setSuccess(true);
        response.setData("User profile retrieved successfully.");
        response.setCode(WebGatewayApplicationCode.SUCCESS);
        response.setCaption("User profile retrieved successfully.");

        return ResponseEntity.ok(response);
    }
}
