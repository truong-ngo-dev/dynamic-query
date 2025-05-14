package vn.truongngo.lib.dynamicquery.metadata.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import vn.truongngo.lib.dynamicquery.metadata.db.ColumnMetadata;

import java.sql.JDBCType;

/**
 * Default implementation of {@link FieldMetadata} representing the metadata
 * of a Java field that maps to a database column in dynamic query generation.
 *
 * <p>This class captures the name and type of a Java field, its corresponding
 * {@link JDBCType}, and the associated {@link ColumnMetadata} from the database schema.</p>
 *
 * <p>It acts as a bridge between the object model and the relational model,
 * enabling dynamic query engines to resolve and map fields to columns effectively.</p>
 *
 * @see FieldMetadata
 * @see ColumnMetadata
 * @see EntityMetadata
 * @author Truong Ngo
 * @version 2.0.0
 */
@Getter
@Setter
@SuperBuilder
public class DefaultFieldMetadata implements FieldMetadata {

    /**
     * The name of the field in the Java class.
     */
    private String fieldName;

    /**
     * The Java type of the field (e.g., String, Integer, LocalDate).
     */
    private Class<?> fieldType;

    /**
     * The JDBC type corresponding to the field, used for SQL type mapping.
     */
    private JDBCType jdbcType;

    /**
     * The index of the field in its declaration context, such as its order in a list or query projection.
     */
    private int index;

    /**
     * The metadata of the database column that this field maps to.
     */
    private ColumnMetadata columnMetadata;

}
