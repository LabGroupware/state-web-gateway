package org.cresplanex.api.state.webgateway.dto.domain;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class ListRelationSerializer<T extends DeepCloneable> extends JsonSerializer<ListRelation<T>> {

    @Override
    public void serialize(ListRelation<T> relation, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        log.info(relation.toString());

        if (relation.isHasValue()) {
            log.info(relation.getValue().toString());
            if (relation.getValue() == null) {
                gen.writeNull();
            } else {
                gen.writeStartArray();
                for (T t : relation.getValue()) {
                    gen.writeObject(t.deepClone());
                    gen.flush();
                }
                gen.writeEndArray();
                gen.close();
            }
        }else {
            gen.writeNull();
        }
    }
}
