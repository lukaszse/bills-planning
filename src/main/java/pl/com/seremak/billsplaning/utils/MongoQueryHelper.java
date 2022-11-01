package pl.com.seremak.billsplaning.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.Map;

import static pl.com.seremak.billsplaning.utils.VersionedEntityUtils.updateMetadata;

@Component
@RequiredArgsConstructor
public class MongoQueryHelper {

    private final ObjectMapper objectMapper;


    @SuppressWarnings({"unchecked"})
    public Update preparePartialUpdateQuery(final Object object) {
        final Update categoryUpdate = new Update();
        final Map<String, Object> fieldsMap = objectMapper.convertValue(object, Map.class);
        fieldsMap.entrySet().stream()
                .filter(field -> field.getValue() != null)
                .forEach(field -> categoryUpdate.set(field.getKey(), field.getValue()));
        return updateMetadata(categoryUpdate);
    }
}
