package org.cresplanex.api.state.webgateway.dto.domain.storage;

import lombok.*;
import org.cresplanex.api.state.webgateway.dto.domain.DeepCloneable;
import org.cresplanex.api.state.webgateway.dto.domain.OverMerge;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
public class FileObjectOnTaskDto extends FileObjectDto implements OverMerge<FileObjectDto, FileObjectOnTaskDto>, DeepCloneable {

    public FileObjectOnTaskDto(FileObjectDto fileObjectDto) {
        super(
                fileObjectDto.getFileObjectId(),
                fileObjectDto.getBucketId(),
                fileObjectDto.getName(),
                fileObjectDto.getPath(),
                fileObjectDto.getMimeType(),
                fileObjectDto.getSize(),
                fileObjectDto.getChecksum(),
                fileObjectDto.getAttachedTasks()
        );
    }

    public FileObjectOnTaskDto(FileObjectOnTaskDto fileObjectOnTaskDto) {
        super(
                fileObjectOnTaskDto.getFileObjectId(),
                fileObjectOnTaskDto.getBucketId(),
                fileObjectOnTaskDto.getName(),
                fileObjectOnTaskDto.getPath(),
                fileObjectOnTaskDto.getMimeType(),
                fileObjectOnTaskDto.getSize(),
                fileObjectOnTaskDto.getChecksum(),
                fileObjectOnTaskDto.getAttachedTasks()
        );
    }

    public FileObjectOnTaskDto(FileObjectDto fileObjectDto, FileObjectOnTaskDto fileObjectOnTaskDto) {
        this(fileObjectDto);
    }

    @Override
    public FileObjectOnTaskDto overMerge(FileObjectDto fileObjectDto) {
        return new FileObjectOnTaskDto(fileObjectDto, this);
    }

    @Override
    public FileObjectOnTaskDto deepClone() {
        return (FileObjectOnTaskDto) super.clone();
    }
}
