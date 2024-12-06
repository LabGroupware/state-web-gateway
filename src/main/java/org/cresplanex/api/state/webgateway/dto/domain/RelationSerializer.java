package org.cresplanex.api.state.webgateway.dto.domain;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class RelationSerializer<T extends DeepCloneable> extends JsonSerializer<Relation<T>> {

    @Override
    public void serialize(Relation<T> relation, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (relation.isHasValue()) {
            if (relation.getValue() == null) {
                gen.writeNull();
            } else {
                gen.writeObject(relation.getValue().deepClone());
            }
        } else {
            gen.writeNull();
        }
    }
}
