package org.cresplanex.api.state.webgateway.controller;

import build.buf.gen.storage.v1.FileObject;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.cresplanex.api.state.common.constants.WebGatewayApplicationCode;
import org.cresplanex.api.state.webgateway.composition.FileObjectCompositionService;
import org.cresplanex.api.state.webgateway.dto.CommandResponseDto;
import org.cresplanex.api.state.webgateway.dto.ListResponseDto;
import org.cresplanex.api.state.webgateway.dto.ResponseDto;
import org.cresplanex.api.state.webgateway.dto.domain.storage.FileObjectDto;
import org.cresplanex.api.state.webgateway.dto.storage.CreateFileObjectRequestDto;
import org.cresplanex.api.state.webgateway.proxy.command.StorageCommandServiceProxy;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/storages")
@AllArgsConstructor
public class StorageController {

    private final StorageCommandServiceProxy storageCommandServiceProxy;
    private final FileObjectCompositionService fileObjectCompositionService;

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<CommandResponseDto> createFileObject(
            @Valid @RequestBody CreateFileObjectRequestDto requestDTO
            ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        FileObject fileObject = FileObject.newBuilder()
                .setBucketId(requestDTO.getBucketId())
                .setName(requestDTO.getName())
                .setPath(requestDTO.getPath())
                .build();
        String jobId = storageCommandServiceProxy.createFileObject(jwt.getSubject(), fileObject);

        CommandResponseDto response = new CommandResponseDto();

        response.setSuccess(true);
        response.setData(new CommandResponseDto.InternalData(jobId));
        response.setCode(WebGatewayApplicationCode.SUCCESS);
        response.setCaption("File object create pending.");

        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "/{fileObjectId}", method = RequestMethod.GET)
    public ResponseEntity<ResponseDto<FileObjectDto>> getFileObject(
            @PathVariable String fileObjectId,
            @RequestParam(name = "with", required = false) List<String> with
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();

        FileObjectDto fileObject = fileObjectCompositionService.findFileObject(
                jwt.getSubject(), fileObjectId, with
        );

        ResponseDto<FileObjectDto> response = new ResponseDto<>();

        response.setSuccess(true);
        response.setData(fileObject);
        response.setCode(WebGatewayApplicationCode.SUCCESS);
        response.setCaption("File object found.");

        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<ListResponseDto<FileObjectDto>> getFileObjects(
            @RequestParam(name = "limit", required = false, defaultValue = "10") int limit,
            @RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(name = "cursor", required = false) String cursor,
            @RequestParam(name = "pagination", required = false) String pagination,
            @RequestParam(name = "sortField", required = false) String sortField,
            @RequestParam(name = "sortOrder", required = false) String sortOrder,
            @RequestParam(name = "withCount", required = false, defaultValue = "false") boolean withCount,
            @RequestParam(name = "hasBucketFilter", required = false, defaultValue = "false") boolean hasBucketFilter,
            @RequestParam(name = "filterBucketIds", required = false) List<String> filterBucketIds,
            @RequestParam(name = "with", required = false) List<String> with
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();

        ListResponseDto.InternalData<FileObjectDto> fileObjects = fileObjectCompositionService.getFileObjects(
                jwt.getSubject(),
                limit,
                offset,
                cursor,
                pagination,
                sortField,
                sortOrder,
                withCount,
                hasBucketFilter,
                filterBucketIds,
                with
        );

        ListResponseDto<FileObjectDto> response = new ListResponseDto<>();

        response.setSuccess(true);
        response.setData(fileObjects);
        response.setCode(WebGatewayApplicationCode.SUCCESS);
        response.setCaption("File objects found.");

        return ResponseEntity.ok(response);
    }
}
