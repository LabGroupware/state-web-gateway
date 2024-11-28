package org.cresplanex.api.state.webgateway.dto.domain.storage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.cresplanex.api.state.webgateway.dto.domain.DeepCloneable;
import org.cresplanex.api.state.webgateway.dto.domain.OverMerge;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
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
        try {
            return (FileObjectOnTaskDto) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
