package org.cresplanex.api.state.webgateway.controller;

import lombok.AllArgsConstructor;
import org.cresplanex.api.state.webgateway.proxy.command.UserPreferenceCommandServiceProxy;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user-profiles")
@AllArgsConstructor
public class UserProfileController {

    private final UserPreferenceCommandServiceProxy userPreferenceCommandServiceProxy;
}
