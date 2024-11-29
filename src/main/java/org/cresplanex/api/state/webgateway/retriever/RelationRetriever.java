package org.cresplanex.api.state.webgateway.retriever;

import lombok.Getter;
import org.cresplanex.api.state.webgateway.dto.domain.DeepCloneable;
import org.cresplanex.api.state.webgateway.dto.domain.DomainDto;
import org.cresplanex.api.state.webgateway.dto.domain.Relation;

import java.util.List;
import java.util.function.Function;

@Getter
public class RelationRetriever<T extends DomainDto & DeepCloneable, U extends DomainDto, V extends Retriever<? super T>> {

    private final Function<? super U, Relation<T>> relationRetriever;
    private final Function<? super U, String> idRetriever;
    private final List<V> chain;

    public RelationRetriever(Function<? super U, Relation<T>> relationRetriever, Function<? super U, String> idRetriever, List<V> chain) {
        this.relationRetriever = relationRetriever;
        this.idRetriever = idRetriever;
        this.chain = chain;
    }
}
