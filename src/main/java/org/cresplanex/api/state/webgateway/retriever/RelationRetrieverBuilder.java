package org.cresplanex.api.state.webgateway.retriever;

import org.cresplanex.api.state.webgateway.dto.domain.DomainDto;
import org.cresplanex.api.state.webgateway.dto.domain.Relation;

import java.util.List;
import java.util.function.Function;

public class RelationRetrieverBuilder<T extends DomainDto, U extends DomainDto, V extends Retriever<? super T>> {

    private Function<? super U, Relation<T>> relationRetriever;
    private Function<? super U, String> idRetriever;
    private List<V> chain;

    public RelationRetrieverBuilder() {
    }

    public static <T extends DomainDto, U extends DomainDto, V extends Retriever<? super T>> RelationRetrieverBuilder<T, U, V> builder() {
        return new RelationRetrieverBuilder<>();
    }

    public RelationRetrieverBuilder<T, U, V> relationRetriever(Function<? super U, Relation<T>> relationRetriever) {
        this.relationRetriever = relationRetriever;
        return this;
    }

    public RelationRetrieverBuilder<T, U, V> idRetriever(Function<U, String> idRetriever) {
        this.idRetriever = idRetriever;
        return this;
    }

    public RelationRetrieverBuilder<T, U, V> chain(List<V> chain) {
        this.chain = chain;
        return this;
    }

    public RelationRetrieverBuilder<T, U, V> chain(V[] chain) {
        this.chain = List.of(chain);
        return this;
    }

    public RelationRetriever<T, U, V> build() {
        return new RelationRetriever<>(relationRetriever, idRetriever, chain);
    }
}
