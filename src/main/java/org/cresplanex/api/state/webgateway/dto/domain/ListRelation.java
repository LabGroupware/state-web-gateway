package org.cresplanex.api.state.webgateway.dto.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@JsonSerialize(using = RelationSerializer.class)
public class ListRelation<T extends DeepCloneable> implements Cloneable, DeepCloneable {
    private boolean hasValue;
    private List<T> value;

    public ListRelation(boolean hasValue, List<T> value) {
        this.hasValue = hasValue;
        this.value = value;
    }

    public static <T extends DeepCloneable> ListRelationBuilder<T> builder() {
        return new ListRelationBuilder<>();
    }

    public static class ListRelationBuilder<T extends DeepCloneable> {
        private boolean hasValue;
        private List<T> value;

        public ListRelationBuilder<T> hasValue(boolean hasValue) {
            this.hasValue = hasValue;
            return this;
        }

        public ListRelationBuilder<T> value(List<T> value) {
            this.value = value;
            return this;
        }

        public ListRelation<T> build() {
            return new ListRelation<>(hasValue, value);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public ListRelation<T> clone() {
        try {
            ListRelation<T> cloned = (ListRelation<T>) super.clone();
            if (this.value != null) {
                cloned.value = this.value.stream().map(v -> (T) v.deepClone()).toList();
            }
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
