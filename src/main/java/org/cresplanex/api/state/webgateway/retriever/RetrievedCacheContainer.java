package org.cresplanex.api.state.webgateway.retriever;

import lombok.Getter;
import lombok.Setter;
import org.cresplanex.api.state.webgateway.dto.domain.DomainDto;

import java.util.Map;

@Getter
@Setter
public class RetrievedCacheContainer {

    /**
     * Cache of retrieved data
     * Key: hash of the kind of data
     * Value: Map of retrieved data(Key: id, Value: DomainDto)
     */
    Map<String, DomainDto> cache;
}
