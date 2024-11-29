package org.cresplanex.api.state.webgateway.dto.domain.storage;

import lombok.*;
import org.cresplanex.api.state.webgateway.dto.domain.DeepCloneable;
import org.cresplanex.api.state.webgateway.dto.domain.DomainDto;
import org.cresplanex.api.state.webgateway.dto.domain.ListRelation;
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
        try {
            FileObjectDto cloned = (FileObjectDto) super.clone();
            if (attachedTasks != null){
                cloned.setAttachedTasks(attachedTasks.clone());
            }
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
