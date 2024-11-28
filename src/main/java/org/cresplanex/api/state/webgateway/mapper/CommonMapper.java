package org.cresplanex.api.state.webgateway.mapper;

import build.buf.gen.cresplanex.nova.v1.Count;
import org.cresplanex.api.state.webgateway.dto.ListResponseDto;

public class CommonMapper {

    public static ListResponseDto.CountData countConvert(Count count) {
        return new ListResponseDto.CountData(
                count.getCount(),
                count.getIsValid()
        );
    }

    public static ListResponseDto.CountData invalidCountGenerate() {
        return new ListResponseDto.CountData(
                0,
                false
        );
    }
}
