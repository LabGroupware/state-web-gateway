package org.cresplanex.api.state.webgateway.proxy.command;

import build.buf.gen.team.v1.CreateTeamRequest;
import build.buf.gen.team.v1.Team;
import build.buf.gen.team.v1.TeamUserRequestType;
import build.buf.gen.userprofile.v1.CreateUserProfileRequest;
import build.buf.gen.userprofile.v1.UserProfile;
import build.buf.gen.userprofile.v1.UserProfileServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserProfileCommandServiceProxy {

    @GrpcClient("userProfileService")
    private UserProfileServiceGrpc.UserProfileServiceBlockingStub userProfileServiceBlockingStub;

    public String createUserProfile(String operatorId, UserProfile userProfile) {
        return this.userProfileServiceBlockingStub.createUserProfile(
                CreateUserProfileRequest.newBuilder()
                        .setOperatorId(operatorId)
                        .setName(userProfile.getName())
                        .setEmail(userProfile.getEmail())
                        .setUserId(userProfile.getUserId())
                        .build()
        ).getJobId();
    }
}
