package org.cresplanex.api.state.webgateway.composition;

import org.cresplanex.api.state.webgateway.dto.domain.DomainDto;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CommonUtils {

    public static <T extends DomainDto> Set<String> createIdsSet(List<T> list, Function<? super T, String> idRetriever) {
        return list.stream()
                .map(idRetriever)
                .collect(Collectors.toSet());
    }
}
