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
            Organization organization,
            List<OrganizationUserRequestType> users
    ) {
        CreateOrganizationRequest request = CreateOrganizationRequest.newBuilder()
                .setOperatorId(operatorId)
                .setName(organization.getName())
                .setPlan(organization.getPlan())
                .addAllUsers(users)
                .build();

        CreateOrganizationResponse response = organizationServiceBlockingStub.createOrganization(request);
        return response.getJobId();
    }

    public String addUsersToOrganization(
            String operatorId,
            String organizationId,
            List<OrganizationUserRequestType> users
    ) {
        AddOrganizationUserResponse response = organizationServiceBlockingStub.addOrganizationUser(AddOrganizationUserRequest.newBuilder()
                .setOperatorId(operatorId)
                .setOrganizationId(organizationId)
                .addAllUsers(users)
                .build());
        return response.getJobId();
    }
}
