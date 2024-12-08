package org.cresplanex.api.state.webgateway.dto.domain.storage;

import lombok.*;
import org.cresplanex.api.state.webgateway.dto.domain.DeepCloneable;
import org.cresplanex.api.state.webgateway.dto.domain.DomainDto;
import org.cresplanex.api.state.webgateway.dto.domain.ListRelation;
import org.cresplanex.api.state.webgateway.dto.domain.organization.OrganizationDto;
import org.cresplanex.api.state.webgateway.dto.domain.plan.TaskOnFileObjectDto;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class FileObjectDto extends DomainDto implements DeepCloneable {

    private String fileObjectId;

    private String bucketId;

    private String name;

    private String path;

    private String mimeType;

    private long size;

    private String checksum;

    private ListRelation<TaskOnFileObjectDto> attachedTasks;

    @Override
    public FileObjectDto deepClone() {
        FileObjectDto cloned = (FileObjectDto) super.clone();
        if (attachedTasks != null){
            cloned.setAttachedTasks(attachedTasks.clone());
        }
        return cloned;
    }

    public FileObjectDto merge(FileObjectDto fileObjectDto) {
        if (fileObjectDto == null) {
            return this;
        }

        if (fileObjectDto.getAttachedTasks() != null && fileObjectDto.getAttachedTasks().isHasValue()) {
            if (this.getAttachedTasks() == null || !this.getAttachedTasks().isHasValue()) {
                this.setAttachedTasks(fileObjectDto.getAttachedTasks());
            } else {
                for (TaskOnFileObjectDto taskOnFileObjectDto : fileObjectDto.getAttachedTasks().getValue()) {
                    boolean isExist = false;
                    for (TaskOnFileObjectDto thisTaskOnFileObjectDto : this.getAttachedTasks().getValue()) {
                        if (taskOnFileObjectDto.getTaskId().equals(thisTaskOnFileObjectDto.getTaskId())) {
                            thisTaskOnFileObjectDto.merge(taskOnFileObjectDto);
                            isExist = true;
                            break;
                        }
                    }
                    if (!isExist) {
                        this.getAttachedTasks().getValue().add(taskOnFileObjectDto);
                    }
                }
            }
        }

        return this;
    }
}
