package org.cresplanex.api.state.webgateway.dto.domain;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class ListRelationSerializer<T extends DeepCloneable> extends JsonSerializer<ListRelation<T>> {

    @Override
    public void serialize(ListRelation<T> relation, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (relation.isHasValue()) {
            gen.writeObject(relation.getValue());
        }
    }
}
