package org.cresplanex.api.state.webgateway.composition;

import lombok.RequiredArgsConstructor;
import org.cresplanex.api.state.webgateway.dto.domain.ListRelation;
import org.cresplanex.api.state.webgateway.dto.domain.Relation;
import org.cresplanex.api.state.webgateway.dto.domain.plan.TaskDto;
import org.cresplanex.api.state.webgateway.dto.domain.storage.FileObjectDto;
import org.cresplanex.api.state.webgateway.dto.domain.storage.FileObjectOnTaskDto;
import org.cresplanex.api.state.webgateway.dto.domain.team.TeamDto;
import org.cresplanex.api.state.webgateway.dto.domain.userprofile.UserProfileDto;
import org.cresplanex.api.state.webgateway.proxy.query.FileObjectQueryProxy;
import org.cresplanex.api.state.webgateway.proxy.query.TeamQueryProxy;
import org.cresplanex.api.state.webgateway.proxy.query.UserProfileQueryProxy;
import org.cresplanex.api.state.webgateway.retriever.RetrievedCacheContainer;
import org.cresplanex.api.state.webgateway.retriever.domain.FileObjectRetriever;
import org.cresplanex.api.state.webgateway.retriever.domain.TaskRetriever;
import org.cresplanex.api.state.webgateway.retriever.domain.TeamRetriever;
import org.cresplanex.api.state.webgateway.retriever.domain.UserProfileRetriever;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class AttachRelationTask {

    private final UserProfileQueryProxy userProfileQueryProxy;
    private final TeamQueryProxy teamQueryProxy;
    private final FileObjectQueryProxy fileObjectQueryProxy;

    public void attach(String operatorId, RetrievedCacheContainer cache, TaskRetriever retriever, TaskDto taskDto) {

        // ChargeUser
        if (retriever.getChargeUserRelationRetriever() != null) {
            String chargeUserId = retriever.getChargeUserRelationRetriever().getIdRetriever().apply(taskDto);

            Map<String, UserProfileDto> userProfileDtoMap = UserProfileCompositionHelper.createUserProfileDtoMap(
                    userProfileQueryProxy,
                    cache,
                    operatorId,
                    List.of(chargeUserId),
                    retriever.getChargeUserRelationRetriever().getChain()
            );

            this.attachRelationToChargeUser(
                    operatorId,
                    cache,
                    taskDto,
                    userProfileDtoMap,
                    chargeUserId,
                    retriever.getChargeUserRelationRetriever().getRelationRetriever().apply(taskDto),
                    retriever.getChargeUserRelationRetriever().getChain()
            );
        }

        // Team
        if (retriever.getTeamRelationRetriever() != null) {
            String teamId = retriever.getTeamRelationRetriever().getIdRetriever().apply(taskDto);

            Map<String, TeamDto> teamDtoMap = TeamCompositionHelper.createTeamDtoMap(
                    teamQueryProxy,
                    cache,
                    operatorId,
                    List.of(teamId),
                    retriever.getTeamRelationRetriever().getChain()
            );

            this.attachRelationToTeam(
                    operatorId,
                    cache,
                    taskDto,
                    teamDtoMap,
                    retriever.getTeamRelationRetriever().getRelationRetriever().apply(taskDto),
                    retriever.getTeamRelationRetriever().getChain()
            );
        }

        // FileObjects
        if (retriever.getAttachmentsRelationRetriever() != null) {
            Map<String, FileObjectDto> fileObjectDtoMap = FileObjectCompositionHelper.createFileObjectDtoMap(
                    fileObjectQueryProxy,
                    cache,
                    operatorId,
                    retriever.getAttachmentsRelationRetriever().getIdRetriever().apply(taskDto),
                    retriever.getAttachmentsRelationRetriever().getChain()
            );

            this.attachRelationToAttachments(
                    operatorId,
                    cache,
                    taskDto,
                    fileObjectDtoMap,
                    retriever.getAttachmentsRelationRetriever().getRelationRetriever().apply(taskDto),
                    retriever.getAttachmentsRelationRetriever().getChain()
            );
        }
    }

    public void attach(String operatorId, RetrievedCacheContainer cache, TaskRetriever retriever, List<TaskDto> taskDto) {

        // ChargeUser
        if (retriever.getChargeUserRelationRetriever() != null) {
            Map<String, UserProfileDto> userProfileDtoMap = UserProfileCompositionHelper.createUserProfileDtoMap(
                    userProfileQueryProxy,
                    cache,
                    operatorId,
                    taskDto.stream()
                            .map(retriever.getChargeUserRelationRetriever().getIdRetriever())
                            .toList(),
                    retriever.getChargeUserRelationRetriever().getChain()
            );

            for (TaskDto dto : taskDto) {
                this.attachRelationToChargeUser(
                        operatorId,
                        cache,
                        dto,
                        userProfileDtoMap,
                        retriever.getChargeUserRelationRetriever().getIdRetriever().apply(dto),
                        retriever.getChargeUserRelationRetriever().getRelationRetriever().apply(dto),
                        retriever.getChargeUserRelationRetriever().getChain()
                );
            }
        }

        // Team
        if (retriever.getTeamRelationRetriever() != null) {
            Map<String, TeamDto> teamDtoMap = TeamCompositionHelper.createTeamDtoMap(
                    teamQueryProxy,
                    cache,
                    operatorId,
                    taskDto.stream()
                            .map(retriever.getTeamRelationRetriever().getIdRetriever())
                            .toList(),
                    retriever.getTeamRelationRetriever().getChain()
            );

            for (TaskDto dto : taskDto) {
                this.attachRelationToTeam(
                        operatorId,
                        cache,
                        dto,
                        teamDtoMap,
                        retriever.getTeamRelationRetriever().getRelationRetriever().apply(dto),
                        retriever.getTeamRelationRetriever().getChain()
                );
            }
        }

        // FileObjects
        if (retriever.getAttachmentsRelationRetriever() != null) {
            Map<String, FileObjectDto> fileObjectDtoMap = FileObjectCompositionHelper.createFileObjectDtoMap(
                    fileObjectQueryProxy,
                    cache,
                    operatorId,
                    taskDto.stream()
                            .map(retriever.getAttachmentsRelationRetriever().getIdRetriever())
                            .flatMap(List::stream)
                            .toList(),
                    retriever.getAttachmentsRelationRetriever().getChain()
            );

            for (TaskDto dto : taskDto) {
                this.attachRelationToAttachments(
                        operatorId,
                        cache,
                        dto,
                        fileObjectDtoMap,
                        retriever.getAttachmentsRelationRetriever().getRelationRetriever().apply(dto),
                        retriever.getAttachmentsRelationRetriever().getChain()
                );
            }
        }
    }

    protected void attachRelationToChargeUser(
            String operatorId,
            RetrievedCacheContainer cache,
            TaskDto taskDto,
            Map<String, UserProfileDto> userProfileDtoMap,
            String chargeUserId,
            Relation<UserProfileDto> chargeUserRelation,
            List<UserProfileRetriever> retrievers
    ) {
        taskDto.setChargeUser(Relation.<UserProfileDto>builder()
                .hasValue(true)
                .value(userProfileDtoMap.get(chargeUserId))
                .build()
        );
        retrievers.forEach(retriever -> {
            if (retriever != null && chargeUserRelation.isHasValue() && chargeUserRelation.getValue() != null) {
                // TODO: Implement this
            }
        });
    }

    protected void attachRelationToTeam(
            String operatorId,
            RetrievedCacheContainer cache,
            TaskDto taskDto,
            Map<String, TeamDto> teamDtoMap,
            Relation<TeamDto> teamRelation,
            List<TeamRetriever> retrievers
    ) {
        if(taskDto.getTeam().isHasValue()) {
            String teamId = taskDto.getTeam().getValue().getTeamId();
            taskDto.setTeam(Relation.<TeamDto>builder()
                    .hasValue(true)
                    .value(teamDtoMap.get(teamId))
                    .build()
            );
            retrievers.forEach(retriever -> {
                if (retriever != null && teamRelation.isHasValue() && teamRelation.getValue() != null) {
                    // TODO: Implement this
                }
            });
        }
    }

    protected void attachRelationToAttachments(
            String operatorId,
            RetrievedCacheContainer cache,
            TaskDto taskDto,
            Map<String, FileObjectDto> fileObjectDtoMap,
            ListRelation<FileObjectOnTaskDto> attachmentsRelation,
            List<FileObjectRetriever> retrievers
    ) {
        if(taskDto.getAttachments().isHasValue()) {
            List<FileObjectOnTaskDto> originAttachFileObjects = taskDto.getAttachments().getValue();
            List<String> attachFileObjectIds = originAttachFileObjects
                    .stream()
                    .map(FileObjectOnTaskDto::getFileObjectId)
                    .toList();
            List<FileObjectDto> attachFileObjects = attachFileObjectIds.stream()
                    .map(fileObjectDtoMap::get)
                    .toList();
            taskDto.setAttachments(ListRelation.<FileObjectOnTaskDto>builder()
                    .value(
                            attachFileObjects.stream()
                                    .map(file -> new FileObjectOnTaskDto(file, originAttachFileObjects.stream()
                                            .filter(origin -> origin.getFileObjectId().equals(file.getFileObjectId()))
                                            .findFirst()
                                            .orElse(null)))
                                    .toList()
                    )
                    .build()
            );
            retrievers.forEach(retriever -> {
                if (retriever != null && attachmentsRelation.isHasValue() && attachmentsRelation.getValue() != null) {
                    // TODO: Implement this
                }
            });
        }
    }
}
