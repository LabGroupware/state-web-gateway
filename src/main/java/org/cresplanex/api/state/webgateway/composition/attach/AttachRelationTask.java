package org.cresplanex.api.state.webgateway.composition.attach;

import lombok.RequiredArgsConstructor;
import org.cresplanex.api.state.webgateway.composition.helper.FileObjectCompositionHelper;
import org.cresplanex.api.state.webgateway.composition.helper.TeamCompositionHelper;
import org.cresplanex.api.state.webgateway.composition.helper.UserProfileCompositionHelper;
import org.cresplanex.api.state.webgateway.dto.domain.ListRelation;
import org.cresplanex.api.state.webgateway.dto.domain.Relation;
import org.cresplanex.api.state.webgateway.dto.domain.plan.TaskDto;
import org.cresplanex.api.state.webgateway.dto.domain.storage.FileObjectDto;
import org.cresplanex.api.state.webgateway.dto.domain.storage.FileObjectOnTaskDto;
import org.cresplanex.api.state.webgateway.dto.domain.team.TeamDto;
import org.cresplanex.api.state.webgateway.dto.domain.userprofile.UserProfileDto;
import org.cresplanex.api.state.webgateway.proxy.query.*;
import org.cresplanex.api.state.webgateway.retriever.RetrievedCacheContainer;
import org.cresplanex.api.state.webgateway.retriever.domain.FileObjectRetriever;
import org.cresplanex.api.state.webgateway.retriever.domain.TaskRetriever;
import org.cresplanex.api.state.webgateway.retriever.domain.TeamRetriever;
import org.cresplanex.api.state.webgateway.retriever.domain.UserProfileRetriever;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AttachRelationTask {

    private final UserProfileQueryProxy userProfileQueryProxy;
    private final TeamQueryProxy teamQueryProxy;
    private final UserPreferenceQueryProxy userPreferenceQueryProxy;
    private final OrganizationQueryProxy organizationQueryProxy;
    private final TaskQueryProxy taskQueryProxy;
    private final FileObjectQueryProxy fileObjectQueryProxy;

    public <T extends TaskDto> void attach(String operatorId, RetrievedCacheContainer cache, TaskRetriever retriever, T taskDto) {

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

    public <T extends TaskDto> void attach(String operatorId, RetrievedCacheContainer cache, TaskRetriever retriever, List<T> taskDto) {

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

            this.attachRelationToChargeUser(
                    operatorId,
                    cache,
                    taskDto,
                    userProfileDtoMap,
                    taskDto.stream()
                            .map(retriever.getChargeUserRelationRetriever().getRelationRetriever())
                            .toList(),
                    retriever.getChargeUserRelationRetriever().getChain()
            );
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

            this.attachRelationToTeam(
                    operatorId,
                    cache,
                    taskDto,
                    teamDtoMap,
                    taskDto.stream()
                            .map(retriever.getTeamRelationRetriever().getRelationRetriever())
                            .toList(),
                    retriever.getTeamRelationRetriever().getChain()
            );
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

            this.attachRelationToAttachments(
                    operatorId,
                    cache,
                    taskDto,
                    fileObjectDtoMap,
                    taskDto.stream()
                            .map(retriever.getAttachmentsRelationRetriever().getRelationRetriever())
                            .toList(),
                    retriever.getAttachmentsRelationRetriever().getChain()
            );
        }
    }

    private <T extends TaskDto> void internalAttachRelationToChargeUser(
            T taskDto,
            Map<String, UserProfileDto> userProfileDtoMap,
            String chargeUserId
    ) {
        taskDto.setChargeUser(Relation.<UserProfileDto>builder()
                .hasValue(true)
                .value(userProfileDtoMap.get(chargeUserId))
                .build()
        );
    }

    protected <T extends TaskDto> void attachRelationToChargeUser(
            String operatorId,
            RetrievedCacheContainer cache,
            T taskDto,
            Map<String, UserProfileDto> userProfileDtoMap,
            Relation<UserProfileDto> chargeUserRelation,
            List<UserProfileRetriever> retrievers
    ) {
        this.internalAttachRelationToChargeUser(taskDto, userProfileDtoMap, taskDto.getChargeUserId());
        retrievers.forEach(retriever -> {
            if (retriever != null && chargeUserRelation.isHasValue() && chargeUserRelation.getValue() != null) {
                AttachRelationUserProfile attachRelationUserProfile = new AttachRelationUserProfile(
                        userProfileQueryProxy,
                        teamQueryProxy,
                        userPreferenceQueryProxy,
                        organizationQueryProxy,
                        taskQueryProxy,
                        fileObjectQueryProxy
                );
                attachRelationUserProfile.attach(operatorId, cache, retriever, chargeUserRelation.getValue());
            }
        });
    }

    protected <T extends TaskDto> void attachRelationToChargeUser(
            String operatorId,
            RetrievedCacheContainer cache,
            List<T> taskDto,
            Map<String, UserProfileDto> userProfileDtoMap,
            List<Relation<UserProfileDto>> chargeUserRelation,
            List<UserProfileRetriever> retrievers
    ) {
        for (T dto : taskDto) {
            this.internalAttachRelationToChargeUser(dto, userProfileDtoMap, dto.getChargeUserId());
        }
        retrievers.forEach(retriever -> {
            List<UserProfileDto> chargeUserList = chargeUserRelation.stream()
                    .map(Relation::getValue)
                    .toList();
            if (retriever != null && !chargeUserList.isEmpty()) {
                AttachRelationUserProfile attachRelationUserProfile = new AttachRelationUserProfile(
                        userProfileQueryProxy,
                        teamQueryProxy,
                        userPreferenceQueryProxy,
                        organizationQueryProxy,
                        taskQueryProxy,
                        fileObjectQueryProxy
                );
                attachRelationUserProfile.attach(operatorId, cache, retriever, chargeUserList);
            }
        });
    }

    private <T extends TaskDto> void internalAttachRelationToTeam(
            T taskDto,
            Map<String, TeamDto> teamDtoMap,
            String teamId
    ) {
        taskDto.setTeam(Relation.<TeamDto>builder()
                .hasValue(true)
                .value(teamDtoMap.get(teamId))
                .build()
        );
    }

    protected <T extends TaskDto> void attachRelationToTeam(
            String operatorId,
            RetrievedCacheContainer cache,
            T taskDto,
            Map<String, TeamDto> teamDtoMap,
            Relation<TeamDto> teamRelation,
            List<TeamRetriever> retrievers
    ) {
        this.internalAttachRelationToTeam(taskDto, teamDtoMap, taskDto.getTeam().getValue().getTeamId());
        retrievers.forEach(retriever -> {
            if (retriever != null && teamRelation.isHasValue() && teamRelation.getValue() != null) {
                AttachRelationTeam attachRelationTeam = new AttachRelationTeam(
                        userProfileQueryProxy,
                        teamQueryProxy,
                        userPreferenceQueryProxy,
                        organizationQueryProxy,
                        taskQueryProxy,
                        fileObjectQueryProxy
                );
                attachRelationTeam.attach(operatorId, cache, retriever, teamRelation.getValue());
            }
        });
    }

    protected <T extends TaskDto> void attachRelationToTeam(
            String operatorId,
            RetrievedCacheContainer cache,
            List<T> taskDto,
            Map<String, TeamDto> teamDtoMap,
            List<Relation<TeamDto>> teamRelation,
            List<TeamRetriever> retrievers
    ) {
        for (T dto : taskDto) {
            this.internalAttachRelationToTeam(dto, teamDtoMap, dto.getTeam().getValue().getTeamId());
        }
        retrievers.forEach(retriever -> {
            List<TeamDto> teamList = teamRelation.stream()
                    .map(Relation::getValue)
                    .toList();
            if (retriever != null && !teamList.isEmpty()) {
                AttachRelationTeam attachRelationTeam = new AttachRelationTeam(
                        userProfileQueryProxy,
                        teamQueryProxy,
                        userPreferenceQueryProxy,
                        organizationQueryProxy,
                        taskQueryProxy,
                        fileObjectQueryProxy
                );
                attachRelationTeam.attach(operatorId, cache, retriever, teamList);
            }
        });
    }

    private <T extends TaskDto> void internalAttachRelationToAttachments(
            T taskDto,
            Map<String, FileObjectDto> fileObjectDtoMap
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
        }
    }

    protected <T extends TaskDto> void attachRelationToAttachments(
            String operatorId,
            RetrievedCacheContainer cache,
            T taskDto,
            Map<String, FileObjectDto> fileObjectDtoMap,
            ListRelation<FileObjectOnTaskDto> attachmentsRelation,
            List<FileObjectRetriever> retrievers
    ) {
        this.internalAttachRelationToAttachments(taskDto, fileObjectDtoMap);

        retrievers.forEach(retriever -> {
            if (retriever != null && attachmentsRelation.isHasValue() && attachmentsRelation.getValue() != null) {
                AttachRelationFileObject attachRelationFileObject = new AttachRelationFileObject(
                        userProfileQueryProxy,
                        teamQueryProxy,
                        userPreferenceQueryProxy,
                        organizationQueryProxy,
                        taskQueryProxy,
                        fileObjectQueryProxy
                );
                attachRelationFileObject.attach(operatorId, cache, retriever, attachmentsRelation.getValue());
            }
        });
    }

    protected <T extends TaskDto> void attachRelationToAttachments(
            String operatorId,
            RetrievedCacheContainer cache,
            List<T> taskDto,
            Map<String, FileObjectDto> fileObjectDtoMap,
            List<ListRelation<FileObjectOnTaskDto>> attachmentsRelation,
            List<FileObjectRetriever> retrievers
    ) {
        for (T dto : taskDto) {
            this.internalAttachRelationToAttachments(dto, fileObjectDtoMap);
        }

        retrievers.forEach(retriever -> {
            List<List<FileObjectOnTaskDto>> attachmentsList = attachmentsRelation.stream()
                    .filter(ListRelation::isHasValue)
                    .map(ListRelation::getValue)
                    .toList();

            if (retriever != null && !attachmentsList.isEmpty()) {
                AttachRelationFileObject attachRelationFileObject = new AttachRelationFileObject(
                        userProfileQueryProxy,
                        teamQueryProxy,
                        userPreferenceQueryProxy,
                        organizationQueryProxy,
                        taskQueryProxy,
                        fileObjectQueryProxy
                );
                attachRelationFileObject.attach(operatorId, cache, retriever, attachmentsList.stream().flatMap(List::stream).toList());
            }
        });
    }
}
