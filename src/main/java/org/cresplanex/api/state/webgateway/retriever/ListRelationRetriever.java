package org.cresplanex.api.state.webgateway.retriever;

import lombok.Getter;
import org.cresplanex.api.state.webgateway.dto.domain.DeepCloneable;
import org.cresplanex.api.state.webgateway.dto.domain.DomainDto;
import org.cresplanex.api.state.webgateway.dto.domain.ListRelation;
import org.cresplanex.api.state.webgateway.dto.domain.Relation;

import java.util.List;
import java.util.function.Function;

@Getter
public class ListRelationRetriever<T extends DomainDto & DeepCloneable, U extends DomainDto, V extends Retriever<? super T>> {

    private final Function<? super U, ListRelation<T>> relationRetriever;
    private final Function<? super U, List<String>> idRetriever;
    private final List<V> chain;

    public ListRelationRetriever(Function<? super U, ListRelation<T>> relationRetriever, Function<? super U, List<String>> idRetriever, List<V> chain) {
        this.relationRetriever = relationRetriever;
        this.idRetriever = idRetriever;
        this.chain = chain;
    }
}
