package org.cresplanex.api.state.webgateway.proxy.command;

import build.buf.gen.team.v1.*;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamCommandServiceProxy {

    @GrpcClient("teamService")
    private TeamServiceGrpc.TeamServiceBlockingStub teamServiceBlockingStub;

    public String createTeam(String operatorId, Team team, List<TeamUserRequestType> teamUserRequest) {
        return this.teamServiceBlockingStub.createTeam(
                CreateTeamRequest.newBuilder()
                        .setOperatorId(operatorId)
                        .setName(team.getName())
                        .setOrganizationId(team.getOrganizationId())
                        .setDescription(team.getDescription())
                        .addAllUsers(teamUserRequest)
                        .build()
        ).getJobId();
    }

    public String addUsersToTeam(String operatorId, String teamId, List<TeamUserRequestType> teamUserRequest) {
        return this.teamServiceBlockingStub.addTeamUser(
                AddTeamUserRequest.newBuilder()
                        .setOperatorId(operatorId)
                        .setTeamId(teamId)
                        .addAllUsers(teamUserRequest)
                        .build()
        ).getJobId();
    }
}
