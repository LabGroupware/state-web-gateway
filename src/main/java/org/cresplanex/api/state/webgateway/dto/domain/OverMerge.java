package org.cresplanex.api.state.webgateway.dto.domain;

public interface OverMerge<T extends DomainDto, U extends T> {
    public U overMerge(T t);
}
