package vn.truongngo.lib.dynamicquery.metadata.jpa;

import lombok.Getter;
import lombok.Setter;
import vn.truongngo.lib.dynamicquery.metadata.db.ColumnMetadata;
import vn.truongngo.lib.dynamicquery.metadata.entity.FieldMetadata;

import java.sql.JDBCType;
import java.util.List;

/**
 * Represents metadata for a field that maps to a composite foreign key using multiple join columns.
 * <p>
 * This class is useful in scenarios where a relationship is established using more than one column
 * (i.e., {@code @JoinColumns}). It implements {@link FieldMetadata} but overrides the
 * {@code getColumnMetadata()} method to return {@code null} since column information is handled per item.
 * </p>
 *
 * @author Truong
 * @version 2.0.0
 */
@Getter
@Setter
public class CompositeJoinColumnFieldMetadata implements FieldMetadata {

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
     * The list of items representing individual column-to-column references in the composite join.
     */
    private List<Item> items;

    /**
     * This method is unsupported in composite join contexts and always returns {@code null}.
     *
     * @return {@code null}
     */
    @Override
    public ColumnMetadata getColumnMetadata() {
        return null;
    }

    /**
     * Represents a single column reference in a composite join.
     * Each {@code Item} contains metadata for mapping one local column to one foreign column.
     */
    @Getter
    @Setter
    public static class Item implements ReferenceColumnMetadata {

        /**
         * The name of the local column in the owning table.
         */
        private String columnName;

        /**
         * An optional SQL fragment to define the column type or constraints.
         */
        private String columnDefinition;

        /**
         * Whether the column allows {@code NULL} values.
         */
        private boolean nullable;

        /**
         * The name of the referenced column in the target entity.
         */
        private String referenceColumnName;

    }

}
