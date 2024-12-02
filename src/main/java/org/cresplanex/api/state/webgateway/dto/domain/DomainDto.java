package org.cresplanex.api.state.webgateway.dto.domain;

public class DomainDto implements Cloneable, DeepCloneable {
    @Override
    public DomainDto deepClone() {
        try {
            return (DomainDto) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public DomainDto clone() {
        try {
            return (DomainDto) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
