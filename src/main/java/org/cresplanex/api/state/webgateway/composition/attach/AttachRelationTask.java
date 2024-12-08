package org.cresplanex.api.state.webgateway.composition.attach;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class AttachRelationTask {

    private final UserProfileQueryProxy userProfileQueryProxy;
    private final TeamQueryProxy teamQueryProxy;
    private final UserPreferenceQueryProxy userPreferenceQueryProxy;
    private final OrganizationQueryProxy organizationQueryProxy;
    private final TaskQueryProxy taskQueryProxy;
    private final FileObjectQueryProxy fileObjectQueryProxy;

    public <T extends TaskDto> T attach(String operatorId, RetrievedCacheContainer cache, TaskRetriever retriever, T taskDto) {

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
                    retriever.getAttachmentsRelationRetriever().getChain()
            );
        }

        return taskDto;
    }

    public <T extends TaskDto> List<T> attach(String operatorId, RetrievedCacheContainer cache, TaskRetriever retriever, List<T> taskDto) {

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


            teamDtoMap.entrySet().forEach(entry -> {
                log.info("Key: " + entry.getKey() + " Value: " + entry.getValue());
            });

            this.attachRelationToTeam(
                    operatorId,
                    cache,
                    taskDto,
                    teamDtoMap,
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
                            .distinct()
                            .toList(),
                    retriever.getAttachmentsRelationRetriever().getChain()
            );

            this.attachRelationToAttachments(
                    operatorId,
                    cache,
                    taskDto,
                    fileObjectDtoMap,
                    retriever.getAttachmentsRelationRetriever().getChain()
            );
        }

        return taskDto;
    }

    private <T extends TaskDto> void internalAttachRelationToChargeUser(
            T taskDto,
            Map<String, UserProfileDto> userProfileDtoMap
    ) {
        UserProfileDto originUserProfile = taskDto.getChargeUser().getValue();
        taskDto.setChargeUser(Relation.<UserProfileDto>builder()
                .hasValue(true)
                .value(userProfileDtoMap.get(taskDto.getChargeUserId()).merge(originUserProfile))
                .build()
        );
    }

    protected <T extends TaskDto> void attachRelationToChargeUser(
            String operatorId,
            RetrievedCacheContainer cache,
            T taskDto,
            Map<String, UserProfileDto> userProfileDtoMap,
            List<UserProfileRetriever> retrievers
    ) {
        this.internalAttachRelationToChargeUser(taskDto, userProfileDtoMap);
        retrievers.forEach(retriever -> {
            if (retriever != null) {
                AttachRelationUserProfile attachRelationUserProfile = new AttachRelationUserProfile(
                        userProfileQueryProxy,
                        teamQueryProxy,
                        userPreferenceQueryProxy,
                        organizationQueryProxy,
                        taskQueryProxy,
                        fileObjectQueryProxy
                );
                var attached = attachRelationUserProfile.attach(operatorId, cache, retriever, taskDto.getChargeUser().getValue());

                Map<String, UserProfileDto> attachedMap = new HashMap<>();

                attachedMap.put(attached.getUserId(), attached);

                this.internalAttachRelationToChargeUser(taskDto, attachedMap);
            }
        });
    }

    protected <T extends TaskDto> void attachRelationToChargeUser(
            String operatorId,
            RetrievedCacheContainer cache,
            List<T> taskDto,
            Map<String, UserProfileDto> userProfileDtoMap,
            List<UserProfileRetriever> retrievers
    ) {
        for (T dto : taskDto) {
            this.internalAttachRelationToChargeUser(dto, userProfileDtoMap);
        }

        retrievers.forEach(retriever -> {
            if (retriever != null) {
                AttachRelationUserProfile attachRelationUserProfile = new AttachRelationUserProfile(
                        userProfileQueryProxy,
                        teamQueryProxy,
                        userPreferenceQueryProxy,
                        organizationQueryProxy,
                        taskQueryProxy,
                        fileObjectQueryProxy
                );
                var attached = attachRelationUserProfile.attach(operatorId, cache, retriever, taskDto.stream()
                        .map(T::getChargeUser)
                        .map(Relation::getValue)
                        .toList());

                Map<String, UserProfileDto> attachedMap = new HashMap<>();
                for (UserProfileDto userProfileDto : attached) {
                    attachedMap.put(userProfileDto.getUserId(), userProfileDto);
                }

                for (T dto : taskDto) {
                    this.internalAttachRelationToChargeUser(dto, attachedMap);
                }
            }
        });
    }

    private <T extends TaskDto> void internalAttachRelationToTeam(
            T taskDto,
            Map<String, TeamDto> teamDtoMap
    ) {
        TeamDto originTeam = taskDto.getTeam().getValue();
        taskDto.setTeam(Relation.<TeamDto>builder()
                .hasValue(true)
                .value(teamDtoMap.get(taskDto.getTeamId()).merge(originTeam))
                .build()
        );
    }

    protected <T extends TaskDto> void attachRelationToTeam(
            String operatorId,
            RetrievedCacheContainer cache,
            T taskDto,
            Map<String, TeamDto> teamDtoMap,
            List<TeamRetriever> retrievers
    ) {
        this.internalAttachRelationToTeam(taskDto, teamDtoMap);
        retrievers.forEach(retriever -> {
            if (retriever != null) {
                AttachRelationTeam attachRelationTeam = new AttachRelationTeam(
                        userProfileQueryProxy,
                        teamQueryProxy,
                        userPreferenceQueryProxy,
                        organizationQueryProxy,
                        taskQueryProxy,
                        fileObjectQueryProxy
                );
                var attached = attachRelationTeam.attach(operatorId, cache, retriever, taskDto.getTeam().getValue());

                Map<String, TeamDto> attachedMap = new HashMap<>();

                attachedMap.put(attached.getTeamId(), attached);

                this.internalAttachRelationToTeam(taskDto, attachedMap);
            }
        });
    }

    protected <T extends TaskDto> void attachRelationToTeam(
            String operatorId,
            RetrievedCacheContainer cache,
            List<T> taskDto,
            Map<String, TeamDto> teamDtoMap,
            List<TeamRetriever> retrievers
    ) {
        for (T dto : taskDto) {
            this.internalAttachRelationToTeam(dto, teamDtoMap);
        }
        retrievers.forEach(retriever -> {
            if (retriever != null) {
                AttachRelationTeam attachRelationTeam = new AttachRelationTeam(
                        userProfileQueryProxy,
                        teamQueryProxy,
                        userPreferenceQueryProxy,
                        organizationQueryProxy,
                        taskQueryProxy,
                        fileObjectQueryProxy
                );
                var attached = attachRelationTeam.attach(operatorId, cache, retriever, taskDto.stream()
                        .map(T::getTeam)
                        .map(Relation::getValue)
                        .toList());

                Map<String, TeamDto> attachedMap = new HashMap<>();
                for (TeamDto teamDto : attached) {
                    attachedMap.put(teamDto.getTeamId(), teamDto);
                }

                for (T dto : taskDto) {
                    this.internalAttachRelationToTeam(dto, attachedMap);
                }
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
                    .hasValue(true)
                    .value(
                            attachFileObjects.stream()
                                    .map(file -> new FileObjectOnTaskDto(file.merge(originAttachFileObjects.stream()
                                            .filter(origin -> origin.getFileObjectId().equals(file.getFileObjectId()))
                                            .findFirst()
                                            .orElse(null))))
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
            List<FileObjectRetriever> retrievers
    ) {
        this.internalAttachRelationToAttachments(taskDto, fileObjectDtoMap);

        retrievers.forEach(retriever -> {
            if (retriever != null && taskDto.getAttachments().isHasValue() && taskDto.getAttachments().getValue() != null) {
                AttachRelationFileObject attachRelationFileObject = new AttachRelationFileObject(
                        userProfileQueryProxy,
                        teamQueryProxy,
                        userPreferenceQueryProxy,
                        organizationQueryProxy,
                        taskQueryProxy,
                        fileObjectQueryProxy
                );
                var attached = attachRelationFileObject.attach(operatorId, cache, retriever, taskDto.getAttachments().getValue());

                Map<String, FileObjectDto> attachedMap = new HashMap<>();

                for (FileObjectDto fileObjectDto : attached) {
                    attachedMap.put(fileObjectDto.getFileObjectId(), fileObjectDto);
                }

                this.internalAttachRelationToAttachments(taskDto, attachedMap);
            }
        });
    }

    protected <T extends TaskDto> void attachRelationToAttachments(
            String operatorId,
            RetrievedCacheContainer cache,
            List<T> taskDto,
            Map<String, FileObjectDto> fileObjectDtoMap,
            List<FileObjectRetriever> retrievers
    ) {
        for (T dto : taskDto) {
            this.internalAttachRelationToAttachments(dto, fileObjectDtoMap);
        }

        retrievers.forEach(retriever -> {
            if (retriever != null) {
                AttachRelationFileObject attachRelationFileObject = new AttachRelationFileObject(
                        userProfileQueryProxy,
                        teamQueryProxy,
                        userPreferenceQueryProxy,
                        organizationQueryProxy,
                        taskQueryProxy,
                        fileObjectQueryProxy
                );
                Set<String> seenIds = new HashSet<>();
                List<FileObjectOnTaskDto> allAttachments = taskDto.stream()
                        .map(T::getAttachments)
                        .map(ListRelation::getValue)
                        .flatMap(List::stream)
                        .filter(fileObject -> seenIds.add(fileObject.getFileObjectId()))
                        .toList();

                var attached = attachRelationFileObject.attach(
                        operatorId,
                        cache,
                        retriever,
                        allAttachments
                );

                Map<String, FileObjectDto> attachedMap = new HashMap<>();
                for (FileObjectDto fileObjectDto : attached) {
                    attachedMap.put(fileObjectDto.getFileObjectId(), fileObjectDto);
                }

                for (T dto : taskDto) {
                    this.internalAttachRelationToAttachments(dto, attachedMap);
                }
            }
        });
    }
}
