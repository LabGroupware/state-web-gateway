package org.cresplanex.api.state.webgateway.dto;

import lombok.*;
import org.cresplanex.api.state.webgateway.dto.domain.DomainDto;

import java.io.Serial;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ListResponseDto<T extends DomainDto> extends ResponseDto<ListResponseDto.InternalData<T>> {

    @Serial
    private static final long serialVersionUID = 1L;

    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InternalData<ListData extends DomainDto> {
        private List<ListData> listData;
        private CountData countData;
    }

    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CountData {
        private int count;
        private boolean isValid;
    }
}
