package org.cresplanex.api.state.webgateway.service;

import build.buf.gen.userprofile.v1.FindUserProfileResponse;
import build.buf.gen.userprofile.v1.UserProfile;
import build.buf.gen.userprofile.v1.UserProfileServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
public class UserProfileService {

    @GrpcClient("userProfileService")
    private UserProfileServiceGrpc.UserProfileServiceBlockingStub userProfileServiceBlockingStub;

    public UserProfile findUserProfile(String userProfileId) {
        FindUserProfileResponse response =  userProfileServiceBlockingStub.findUserProfile(build.buf.gen.userprofile.v1.FindUserProfileRequest.newBuilder()
                .setUserProfileId(userProfileId)
                .build());
        return response.getUserProfile();
    }
}
