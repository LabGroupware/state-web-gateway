package org.cresplanex.api.state.webgateway.controller;

import build.buf.gen.storage.v1.FileObject;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.cresplanex.api.state.common.constants.WebGatewayApplicationCode;
import org.cresplanex.api.state.webgateway.dto.CommandResponseDto;
import org.cresplanex.api.state.webgateway.dto.storage.CreateFileObjectRequestDto;
import org.cresplanex.api.state.webgateway.proxy.command.StorageCommandServiceProxy;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/storages")
@AllArgsConstructor
public class StorageController {

    private final StorageCommandServiceProxy storageCommandServiceProxy;

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
        response.setData(new CommandResponseDto.Data(jobId));
        response.setCode(WebGatewayApplicationCode.SUCCESS);
        response.setCaption("File object create pending.");

        return ResponseEntity.ok(response);
    }
}
