package gdsc.konkuk.platformcore.global.utils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class BooleanToIntegerConverter implements AttributeConverter<Boolean, Integer> {
    @Override
    public Integer convertToDatabaseColumn(Boolean attribute) {
        return Boolean.TRUE.equals(attribute) ? 1 : 0;
    }
    @Override
    public Boolean convertToEntityAttribute(Integer dbData) {
        return Integer.valueOf(1).equals(dbData);
    }
} 