package org.cresplanex.api.state.webgateway.dto.storage;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CreateFileObjectRequestDto {

    @NotEmpty
    private String bucketId;

    @NotEmpty
    private String name;

    @NotEmpty
    private String path;
}
