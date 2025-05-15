package vn.truongngo.lib.dynamicquery.querydsl.jpa.mapping;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.EnumPath;
import vn.truongngo.lib.dynamicquery.metadata.entity.FieldMetadata;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Factory Class to construct QPath instances
 */
public abstract class PathFactory {

    private static final Map<Class<?>, PathProvider> pathFactory = new HashMap<>();


    static {
        pathFactory.put(Array.class, (q, config) -> q.createArray(config.getFieldName(), config.getFieldType()));
        pathFactory.put(byte[].class, (q, config) -> q.createArray(config.getFieldName(), config.getFieldType()));
        pathFactory.put(Byte[].class, (q, config) -> q.createArray(config.getFieldName(), config.getFieldType()));
        pathFactory.put(long[].class, (q, config) -> q.createArray(config.getFieldName(), config.getFieldType()));
        pathFactory.put(Long[].class, (q, config) -> q.createArray(config.getFieldName(), config.getFieldType()));
        pathFactory.put(float[].class, (q, config) -> q.createArray(config.getFieldName(), config.getFieldType()));
        pathFactory.put(Float[].class, (q, config) -> q.createArray(config.getFieldName(), config.getFieldType()));
        pathFactory.put(double[].class, (q, config) -> q.createArray(config.getFieldName(), config.getFieldType()));
        pathFactory.put(Double[].class, (q, config) -> q.createArray(config.getFieldName(), config.getFieldType()));
        pathFactory.put(char[].class, (q, config) -> q.createArray(config.getFieldName(), config.getFieldType()));
        pathFactory.put(Character[].class, (q, config) -> q.createArray(config.getFieldName(), config.getFieldType()));
        pathFactory.put(boolean[].class, (q, config) -> q.createArray(config.getFieldName(), config.getFieldType()));
        pathFactory.put(Boolean[].class, (q, config) -> q.createArray(config.getFieldName(), config.getFieldType()));
        pathFactory.put(int[].class, (q, config) -> q.createArray(config.getFieldName(), config.getFieldType()));
        pathFactory.put(Integer[].class, (q, config) -> q.createArray(config.getFieldName(), config.getFieldType()));
        pathFactory.put(short[].class, (q, config) -> q.createArray(config.getFieldName(), config.getFieldType()));
        pathFactory.put(Short[].class, (q, config) -> q.createArray(config.getFieldName(), config.getFieldType()));
        pathFactory.put(Boolean.class, (q, config) -> q.createBoolean(config.getFieldName()));
        pathFactory.put(boolean.class, (q, config) -> q.createBoolean(config.getFieldName()));
        pathFactory.put(Long.class, (q, config) -> q.createNumber(config.getFieldName(), Long.class));
        pathFactory.put(long.class, (q, config) -> q.createNumber(config.getFieldName(), Long.class));
        pathFactory.put(Float.class, (q, config) -> q.createNumber(config.getFieldName(), Float.class));
        pathFactory.put(float.class, (q, config) -> q.createNumber(config.getFieldName(), Float.class));
        pathFactory.put(Double.class, (q, config) -> q.createNumber(config.getFieldName(), Double.class));
        pathFactory.put(double.class, (q, config) -> q.createNumber(config.getFieldName(), Double.class));
        pathFactory.put(Integer.class, (q, config) -> q.createNumber(config.getFieldName(), Integer.class));
        pathFactory.put(int.class, (q, config) -> q.createNumber(config.getFieldName(), Integer.class));
        pathFactory.put(Byte.class, (q, config) -> q.createNumber(config.getFieldName(), Byte.class));
        pathFactory.put(byte.class, (q, config) -> q.createNumber(config.getFieldName(), Byte.class));
        pathFactory.put(Short.class, (q, config) -> q.createNumber(config.getFieldName(), Short.class));
        pathFactory.put(short.class, (q, config) -> q.createNumber(config.getFieldName(), Short.class));
        pathFactory.put(String.class, (q, config) -> q.createString(config.getFieldName()));
        pathFactory.put(BigDecimal.class, (q, config) -> q.createNumber(config.getFieldName(), BigDecimal.class));
        pathFactory.put(BigInteger.class, (q, config) -> q.createNumber(config.getFieldName(), BigInteger.class));
        pathFactory.put(LocalDate.class, (q, config) -> q.createDate(config.getFieldName(), LocalDate.class));
        pathFactory.put(LocalDateTime.class, (q, config) -> q.createDateTime(config.getFieldName(), LocalDateTime.class));
        pathFactory.put(Instant.class, (q, config) -> q.createDateTime(config.getFieldName(), Instant.class));
        pathFactory.put(LocalTime.class, (q, config) -> q.createTime(config.getFieldName(), LocalTime.class));
        pathFactory.put(Timestamp.class, (q, config) -> q.createDateTime(config.getFieldName(), Timestamp.class));
        pathFactory.put(Date.class, (q, config) -> q.createDate(config.getFieldName(), Date.class));
//        pathFactory.put(UUID.class, (q, config) -> (new QUuidPath(q, config.getFieldName()));
        pathFactory.put(Enum.class, PathFactory::createEnum);
        pathFactory.put(Object.class, (q, config) -> q.createSimple(config.getFieldName(), config.getFieldType()));
    }

    static Path<?> create(QEntity<?> q, FieldMetadata fieldMetadata) {
        Class<?> type = fieldMetadata.getFieldType();
        if (!pathFactory.containsKey(type)) {
            type = Object.class;
        }
        return pathFactory.get(type).provide(q, fieldMetadata);
    }

    @SuppressWarnings("all")
    private static EnumPath createEnum(QEntity<?> q, FieldMetadata fieldMetadata) {
        return q.createEnum(fieldMetadata.getFieldName(), (Class<Enum>) fieldMetadata.getFieldType());
    }

    private interface PathProvider {
        Path<?> provide(QEntity<?> q, FieldMetadata metadata);
    }
}
