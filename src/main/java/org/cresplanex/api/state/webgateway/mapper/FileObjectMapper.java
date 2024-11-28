package org.cresplanex.api.state.webgateway.mapper;

import build.buf.gen.plan.v1.FileObjectOnTask;
import build.buf.gen.storage.v1.FileObject;
import org.cresplanex.api.state.webgateway.dto.domain.ListRelation;
import org.cresplanex.api.state.webgateway.dto.domain.plan.TaskOnFileObjectDto;
import org.cresplanex.api.state.webgateway.dto.domain.storage.FileObjectDto;
import org.cresplanex.api.state.webgateway.dto.domain.storage.FileObjectOnTaskDto;

import java.util.List;

public class FileObjectMapper {
    public static FileObjectDto convert(FileObject fileObject) {
        return FileObjectDto.builder()
                .fileObjectId(fileObject.getFileObjectId())
                .name(fileObject.getName())
                .bucketId(fileObject.getBucketId())
                .path(fileObject.getPath())
                .mimeType(fileObject.getMimeType().getHasValue() ? fileObject.getMimeType().getValue() : null)
                .size(fileObject.getSize().getHasValue() ? fileObject.getSize().getValue() : 0)
                .checksum(fileObject.getChecksum().getHasValue() ? fileObject.getChecksum().getValue() : null)
                .attachedTasks(ListRelation.<TaskOnFileObjectDto>builder().hasValue(false).build())
                .build();
    }

    public static FileObjectDto convertFromFileObjectId(String fileObjectId) {
        return FileObjectDto.builder()
                .fileObjectId(fileObjectId)
                .build();
    }

    public static FileObjectOnTaskDto convert(FileObjectOnTask fileObjectOnTask) {
        FileObjectOnTaskDto dto = new FileObjectOnTaskDto();
        dto.setFileObjectId(fileObjectOnTask.getFileObjectId());
        return dto;
    }
}
