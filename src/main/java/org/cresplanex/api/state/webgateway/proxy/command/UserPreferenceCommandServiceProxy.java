package org.cresplanex.api.state.webgateway.proxy.command;

import build.buf.gen.userpreference.v1.UpdateUserPreferenceRequest;
import build.buf.gen.userpreference.v1.UserPreference;
import build.buf.gen.userpreference.v1.UserPreferenceServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
public class UserPreferenceCommandServiceProxy {

    @GrpcClient("userPreferenceService")
    private UserPreferenceServiceGrpc.UserPreferenceServiceBlockingStub userPreferenceServiceBlockingStub;

    public String updateUserPreference(String operatorId, UserPreference userPreference) {
        return this.userPreferenceServiceBlockingStub.updateUserPreference(
                UpdateUserPreferenceRequest.newBuilder()
                        .setOperatorId(operatorId)
                        .setUserPreferenceId(userPreference.getUserPreferenceId())
                        .setTimezone(userPreference.getTimezone())
                        .setLanguage(userPreference.getLanguage())
                        .setTheme(userPreference.getTheme())
                        .build()
        ).getJobId();
    }
}
