package org.cresplanex.api.state.webgateway.dto.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@JsonSerialize(using = RelationSerializer.class)
public class Relation<T extends DeepCloneable> implements Cloneable {
    private boolean hasValue;
    private T value;

    public Relation(boolean hasValue, T value) {
        this.hasValue = hasValue;
        this.value = value;
    }

    public static <T extends DeepCloneable> RelationBuilder<T> builder() {
        return new RelationBuilder<>();
    }

    public static class RelationBuilder<T extends DeepCloneable> {
        private boolean hasValue;
        private T value;

        public RelationBuilder<T> hasValue(boolean hasValue) {
            this.hasValue = hasValue;
            return this;
        }

        public RelationBuilder<T> value(T value) {
            this.value = value;
            return this;
        }

        public Relation<T> build() {
            return new Relation<>(hasValue, value);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Relation<T> clone() {
        try {
            Relation<T> cloned = (Relation<T>) super.clone();
            if (this.value != null) {
                cloned.value = (T)this.value.deepClone();
            }
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
