package org.cresplanex.api.state.webgateway.proxy.command;

import build.buf.gen.userprofile.v1.UserProfileServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
public class UserProfileCommandServiceProxy {

    @GrpcClient("userProfileService")
    private UserProfileServiceGrpc.UserProfileServiceBlockingStub userProfileServiceBlockingStub;
}
