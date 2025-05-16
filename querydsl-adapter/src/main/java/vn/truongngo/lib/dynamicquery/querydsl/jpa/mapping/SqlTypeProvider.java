package vn.truongngo.lib.dynamicquery.querydsl.jpa.mapping;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Provides a mapping from Java types to corresponding SQL types (java.sql.Types).
 * <p>
 * This class maintains a registry of Java classes and their associated SQL type providers,
 * which determine the SQL type integer constant for a given column definition.
 * </p>
 * <p>
 * It is used to resolve the SQL type when building metadata for QueryDSL entities.
 * </p>
 *
 * @author Truong
 * @version 2.0.0
 */
public abstract class SqlTypeProvider {

    /**
     * Map linking Java classes to their respective SQL type provider.
     */
    private static final Map<Class<?>, TypeProvider> sqlTypeProvider = new HashMap<>();

    static {
        sqlTypeProvider.put(BigInteger.class, (columnDefinition -> Types.BIGINT));
        sqlTypeProvider.put(Long.class, (columnDefinition -> Types.BIGINT));
        sqlTypeProvider.put(Integer.class, (columnDefinition -> Types.INTEGER));
        sqlTypeProvider.put(Short.class, (columnDefinition -> Types.SMALLINT));
        sqlTypeProvider.put(Byte.class, (columnDefinition -> Types.SMALLINT));
        sqlTypeProvider.put(String.class, (columnDefinition -> "CLOB".equals(columnDefinition) ? Types.CLOB : Types.VARCHAR));
        sqlTypeProvider.put(BigDecimal.class, (columnDefinition -> Types.DECIMAL));
        sqlTypeProvider.put(Float.class, (columnDefinition -> Types.DECIMAL));
        sqlTypeProvider.put(Double.class, (columnDefinition -> Types.DECIMAL));
        sqlTypeProvider.put(LocalDate.class, (columnDefinition -> Types.TIMESTAMP));
        sqlTypeProvider.put(LocalDateTime.class, (columnDefinition -> Types.TIMESTAMP));
        sqlTypeProvider.put(Instant.class, (columnDefinition -> Types.TIMESTAMP));
        sqlTypeProvider.put(LocalTime.class, (columnDefinition -> Types.TIMESTAMP));
        sqlTypeProvider.put(Timestamp.class, (columnDefinition -> Types.TIMESTAMP));
        sqlTypeProvider.put(Date.class, (columnDefinition -> Types.TIMESTAMP));
        sqlTypeProvider.put(UUID.class, (columnDefinition -> Types.VARCHAR));
        sqlTypeProvider.put(Enum.class, (columnDefinition -> Types.VARCHAR));
        sqlTypeProvider.put(Object.class, (columnDefinition -> Types.OTHER));
        sqlTypeProvider.put(Array.class, (columnDefinition -> Types.ARRAY));

    }

    /**
     * Retrieves the {@link TypeProvider} for a given Java class.
     *
     * @param computedFieldType the Java class to look up
     * @return an {@link Optional} containing the {@link TypeProvider} if found, or empty otherwise
     */
    public static Optional<TypeProvider> get(Class<?> computedFieldType) {
        return Optional.ofNullable(sqlTypeProvider.get(computedFieldType));
    }

    /**
     * Functional interface to provide the SQL type code for a given column definition.
     */
    public interface TypeProvider {

        /**
         * Returns the SQL type corresponding to the given column definition.
         *
         * @param columnDefinition the column definition string (may be null or empty)
         * @return the SQL type as defined in {@link java.sql.Types}
         */
        int getSqlType(String columnDefinition);

    }
}
