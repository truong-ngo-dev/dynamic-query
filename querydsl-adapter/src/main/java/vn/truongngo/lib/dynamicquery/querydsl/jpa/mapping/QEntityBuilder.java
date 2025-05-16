package vn.truongngo.lib.dynamicquery.querydsl.jpa.mapping;

import com.querydsl.core.types.Path;
import com.querydsl.sql.ColumnMetadata;
import lombok.extern.slf4j.Slf4j;
import vn.truongngo.lib.dynamicquery.metadata.db.TableMetadata;
import vn.truongngo.lib.dynamicquery.metadata.entity.FieldMetadata;
import vn.truongngo.lib.dynamicquery.metadata.jpa.JoinColumnFieldMetadata;
import vn.truongngo.lib.dynamicquery.metadata.jpa.JpaEntityMetadata;

import java.sql.Types;

/**
 * Builder class to create {@link QEntity} instances from JPA entity metadata.
 * <p>
 * This class maps the JPA entity's fields and their metadata into QueryDSL QEntity,
 * including columns and primary key definitions.
 * It also provides a placeholder to handle JPA join column mappings (currently not supported).
 * </p>
 *
 * @author Truong Ngo
 * @version 2.0.0
 */
@Slf4j
public class QEntityBuilder {

    /**
     * Builds a {@link QEntity} instance from given {@link JpaEntityMetadata}.
     *
     * @param metadata    the JPA entity metadata containing table and field information
     * @param variable    the variable name to be used for the QEntity instance
     * @param withMapping if true, attempt to map relationships (join columns) - currently not supported
     * @return a fully constructed QEntity instance representing the JPA entity
     */
    public static QEntity<?> build(JpaEntityMetadata metadata, String variable, boolean withMapping) {

        TableMetadata tableMetadata = metadata.getTableMetadata();
        QEntity<?> qEntity = new QEntity<>(
                metadata.getEntityClass(),
                variable,
                tableMetadata.getSchemaName(),
                tableMetadata.getTableName());

        for (FieldMetadata fieldMetadata : metadata.getFields()) {
            addColumn(qEntity, fieldMetadata);
        }

        if (withMapping) {
            // Xử lý các quan hệ ở đây
            log.info("Not supported yet");
        }

        qEntity.createPrimaryKey(metadata.getIdFields());
        return qEntity;
    }

    /**
     * Adds a column path to the {@link QEntity} based on the provided {@link FieldMetadata}.
     * <p>
     * This method creates a QueryDSL Path instance for the field using {@link PathFactory},
     * then constructs {@link ColumnMetadata} for SQL type and nullability,
     * and finally attaches the metadata to the QEntity.
     * </p>
     *
     * @param qEntity the QEntity to which the column should be added
     * @param field   the metadata of the field representing the column
     */
    private static void addColumn(QEntity<?> qEntity, FieldMetadata field) {
        Path<?> path = PathFactory.create(qEntity, field);
        int sqlType = getSqlType(field);
        ColumnMetadata columnMetadata = ColumnMetadata
                .named(field.getColumnMetadata().getColumnName())
                .withIndex(field.getIndex())
                .ofType(sqlType);
        if (!field.getColumnMetadata().isNullable()) {
            columnMetadata = columnMetadata.notNull();
        }
        qEntity.addMetadata(path, columnMetadata);
    }

    /**
     * Placeholder method to handle join columns (relationship mappings).
     * <p>
     * This method should be implemented to add foreign key and join column metadata
     * to the QEntity for JPA relationships such as @ManyToOne, @OneToMany, etc.
     * </p>
     *
     * @param qEntity   the QEntity instance being constructed
     * @param joinField the metadata describing the join column / relationship field
     */
    private void addJoinColumn(QEntity<?> qEntity, JoinColumnFieldMetadata joinField) {
//        QEntityScanner qEntityScanner = qEntityScannerCheck("JoinColumn");
//
//        QJoinColumn qColumn = new QJoinColumn(this, column, qEntityScanner, false);
//        if (qColumn.getPaths().size() > 1) {
//            throw new InvalidArgumentException(String.format("Single JoinColumn mapped to a Composite Primary Key: %s",
//                    column.getFieldName())
//            );
//        }
//        qColumn.getPaths().forEach((path, metadata) -> {
//            String idColumnName = String.format("%sId", column.getFieldName());
//            this.rawColumns.put(idColumnName, path);
//            this.columnsMap.put(idColumnName, path.get());
//            addMetadata(path.get(), metadata);
//            ForeignKey<?> foreignKey = createForeignKey(path.get(), qColumn.getForeignColumnNames().getFirst());
//            this.rawJoinColumns.put(
//                    column.getFieldName(),
//                    new QForeignKey(foreignKey, column.getFieldType(), qColumn.getPaths(), qColumn.getForeignColumnNames())
//            );
//            this.joinColumnsMap.put(column.getFieldName(), foreignKey);
//        });
    }

    /**
     * Resolves the SQL type corresponding to a given field's Java type.
     * <p>
     * Uses {@link SqlTypeProvider} to map the Java type to a SQL {@code java.sql.Types} constant.
     * Defaults to {@link Types#OTHER} if no mapping is found.
     * </p>
     *
     * @param fieldMetadata the field metadata containing the Java type
     * @return the SQL type constant for the field
     */
    private static int getSqlType(FieldMetadata fieldMetadata) {
        return SqlTypeProvider
                .get(fieldMetadata.getFieldType())
                .map(t -> t.getSqlType(fieldMetadata.getColumnMetadata().getColumnDefinition()))
                .orElse(Types.OTHER);
    }
}
