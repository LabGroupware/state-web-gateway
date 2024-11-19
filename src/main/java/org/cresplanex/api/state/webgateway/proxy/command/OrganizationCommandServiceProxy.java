package org.cresplanex.api.state.webgateway.proxy.command;

import build.buf.gen.organization.v1.*;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrganizationCommandServiceProxy {

    @GrpcClient("organizationService")
    private OrganizationServiceGrpc.OrganizationServiceBlockingStub organizationServiceBlockingStub;

    public String createOrganization(
            String operatorId,
            OrganizationWithUsers organizationWithUsers
    ) {
        CreateOrganizationRequest request = CreateOrganizationRequest.newBuilder()
                .setOperatorId(operatorId)
                .setName(organizationWithUsers.getOrganization().getName())
                .setPlan(organizationWithUsers.getOrganization().getPlan())
                .addAllUsers(organizationWithUsers.getUsersList()
                        .stream().map(attachment -> UserOnOrganization.newBuilder()
                                .setUserId(attachment.getUserId())
                                .build())
                        .toList())
                .build();

        CreateOrganizationResponse response = organizationServiceBlockingStub.createOrganization(request);
        return response.getJobId();
    }

    public String addUsersToOrganization(
            String operatorId,
            String organizationId,
            List<UserOnOrganization> users
    ) {
        AddOrganizationUserResponse response = organizationServiceBlockingStub.addOrganizationUser(AddOrganizationUserRequest.newBuilder()
                .setOperatorId(operatorId)
                .setOrganizationId(organizationId)
                .addAllUsers(users)
                .build());
        return response.getJobId();
    }
}
