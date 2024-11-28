package org.cresplanex.api.state.webgateway.retriever;

import org.cresplanex.api.state.webgateway.dto.domain.DeepCloneable;
import org.cresplanex.api.state.webgateway.dto.domain.DomainDto;
import org.cresplanex.api.state.webgateway.dto.domain.ListRelation;

import java.util.List;
import java.util.function.Function;

public class ListRelationRetrieverBuilder<T extends DomainDto & DeepCloneable, U extends DomainDto, V extends Retriever<? super T>> {

    private Function<? super U, ListRelation<T>> relationRetriever;
    private Function<? super U, List<String>> idRetriever;
    private List<V> chain;

    public ListRelationRetrieverBuilder() {
    }

    public static <T extends DomainDto & DeepCloneable, U extends DomainDto, V extends Retriever<? super T>> ListRelationRetrieverBuilder<T, U, V> builder() {
        return new ListRelationRetrieverBuilder<>();
    }

    public ListRelationRetrieverBuilder<T, U, V> relationRetriever(Function<? super U, ListRelation<T>> relationRetriever) {
        this.relationRetriever = relationRetriever;
        return this;
    }

    public ListRelationRetrieverBuilder<T, U, V> idRetriever(Function<? super U, List<String>> idRetriever) {
        this.idRetriever = idRetriever;
        return this;
    }

    public ListRelationRetrieverBuilder<T, U, V> chain(List<V> chain) {
        this.chain = chain;
        return this;
    }

    public ListRelationRetrieverBuilder<T, U, V> chain(V[] chain) {
        this.chain = List.of(chain);
        return this;
    }

    public ListRelationRetriever<T, U, V> build() {
        return new ListRelationRetriever<>(relationRetriever, idRetriever, chain);
    }
}
