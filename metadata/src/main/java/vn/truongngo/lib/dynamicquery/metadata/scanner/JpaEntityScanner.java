package vn.truongngo.lib.dynamicquery.metadata.scanner;

import jakarta.persistence.*;
import vn.truongngo.lib.dynamicquery.metadata.db.ColumnMetadata;
import vn.truongngo.lib.dynamicquery.metadata.db.DefaultColumnMetadata;
import vn.truongngo.lib.dynamicquery.metadata.db.DefaultTableMetadata;
import vn.truongngo.lib.dynamicquery.metadata.db.TableMetadata;
import vn.truongngo.lib.dynamicquery.metadata.entity.DefaultFieldMetadata;
import vn.truongngo.lib.dynamicquery.metadata.entity.FieldMetadata;
import vn.truongngo.lib.dynamicquery.metadata.jpa.CompositeJoinColumnFieldMetadata;
import vn.truongngo.lib.dynamicquery.metadata.jpa.JoinColumnFieldMetadata;
import vn.truongngo.lib.dynamicquery.metadata.jpa.JpaEntityMetadata;
import vn.truongngo.lib.dynamicquery.metadata.utils.NamingUtil;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.stream.Stream;

/**
 * {@code JpaEntityScanner} is an implementation of {@link EntityScanner} that scans a JPA-annotated entity class
 * and produces an instance of {@link JpaEntityMetadata}.
 *
 * <p>This scanner supports common JPA annotations and resolves metadata for querying, including:</p>
 * <ul>
 *   <li>{@code @Entity}, {@code @Table} — to determine table name and schema.</li>
 *   <li>{@code @Column}, {@code @Id} — to resolve column names, types, nullability, and primary keys.</li>
 *   <li>{@code @JoinColumn}, {@code @JoinColumns}, {@code @OneToOne}, {@code @OneToMany}, {@code @ManyToOne} — for relationships.</li>
 * </ul>
 *
 * <p>The output {@link JpaEntityMetadata} contains:</p>
 * <ul>
 *   <li>{@link TableMetadata} describing the table and schema.</li>
 *   <li>{@link FieldMetadata} list with column details.</li>
 *   <li>Join column metadata for foreign keys and inverse relationships.</li>
 * </ul>
 *
 * <p><strong>Example:</strong></p>
 * <blockquote><pre>{@code
 * EntityScanner<Class<?>> scanner = new JpaEntityScanner();
 * EntityMetadata metadata = scanner.scan(MyEntity.class);
 * }</pre></blockquote>
 *
 * <p>Notes:</p>
 * <ul>
 *   <li>Fields marked with {@code @Transient} are skipped.</li>
 *   <li>Inheritance is supported — all fields up the class hierarchy (excluding {@code Object}) are scanned.</li>
 *   <li>Field ordering is preserved by scanning with an incremental index.</li>
 *   <li>If no {@code @Id} field is found, an {@link IllegalArgumentException} is thrown.</li>
 * </ul>
 *
 * @author Truong Ngo
 * @version 2.0.0
 */
public class JpaEntityScanner implements EntityScanner<Class<?>> {

    /**
     * Scans the given JPA entity class and produces {@link JpaEntityMetadata}, which includes
     * table information, fields, primary keys, and relationships.
     *
     * @param source the JPA entity class to scan
     * @return metadata representation of the given entity
     * @throws IllegalArgumentException if no {@code @Entity} or {@code @Table} annotation is found
     *                                  or if no {@code @Id} field is defined
     */
    @Override
    public JpaEntityMetadata scan(Class<?> source) {
        JpaEntityMetadata metadata = scanTable(source);
        scanColumns(metadata, source);
        return metadata;
    }

    /**
     * Parses the class-level {@code @Table} or {@code @Entity} annotation to build {@link TableMetadata}.
     *
     * @param source the entity class to inspect
     * @return a partially populated {@link JpaEntityMetadata} containing table info
     * @throws IllegalArgumentException if neither {@code @Entity} nor {@code @Table} is present
     */
    private JpaEntityMetadata scanTable(Class<?> source) {
        Table table = source.getDeclaredAnnotation(Table.class);
        if (table != null) {
            TableMetadata tableMetadata = DefaultTableMetadata.builder()
                    .tableName(table.name())
                    .schemaName(table.schema())
                    .build();
            return JpaEntityMetadata.builder()
                    .entityClass(source)
                    .tableMetadata(tableMetadata)
                    .build();
        } else {
            Entity entity = source.getDeclaredAnnotation(Entity.class);
            if (entity != null) {
                String tableName = NamingUtil.camelToUnderscore(source.getSimpleName());
                TableMetadata tableMetadata = DefaultTableMetadata.builder()
                        .tableName(tableName)
                        .schemaName("")
                        .build();

                return JpaEntityMetadata.builder()
                        .entityClass(source)
                        .tableMetadata(tableMetadata)
                        .build();
            }

            throw new IllegalArgumentException("No @Table, @Entity annotation found for " + source);
        }
    }

    /**
     * Scans all declared fields (including inherited ones) for column and relationship annotations.
     * Populates the provided {@link JpaEntityMetadata} with {@link FieldMetadata}, id fields,
     * and join field metadata.
     *
     * @param metadata the metadata object to populate
     * @param source   the original entity class to start scanning from
     * @throws IllegalArgumentException if no {@code @Id} field is found
     */
    private void scanColumns(JpaEntityMetadata metadata, Class<?> source) {
        Class<?> prev = source;
        while (!prev.equals(Object.class)) {
            for (int i = 0; i < prev.getDeclaredFields().length; i++) {
                parse(prev.getDeclaredFields()[i], i + 1, metadata);
            }
            prev = prev.getSuperclass();
        }
        if (metadata.getIdFields().isEmpty()) {
            throw new IllegalArgumentException("No @Id, @EmbeddedId annotation found for " + source);
        }
    }

    /**
     * Parses a single field for supported JPA annotations and updates the metadata accordingly.
     * Handles basic column fields and relationship mappings like {@code @JoinColumn}, {@code @OneToOne},
     * {@code @OneToMany}, etc.
     *
     * @param field    the field to analyze
     * @param index    the field's index in declaration order
     * @param metadata the metadata object to populate
     */
    private void parse(Field field, int index, JpaEntityMetadata metadata) {

        Transient transientAnnotated = field.getAnnotation(Transient.class);
        OneToOne oneToOne = field.getDeclaredAnnotation(OneToOne.class);
        OneToMany oneToMany = field.getDeclaredAnnotation(OneToMany.class);
        ManyToOne manyToOne = field.getDeclaredAnnotation(ManyToOne.class);
        JoinColumn joinColumn = field.getDeclaredAnnotation(JoinColumn.class);
        JoinColumns joinColumns = field.getDeclaredAnnotation(JoinColumns.class);
        Column column = field.getDeclaredAnnotation(Column.class);

        // Basic implementation for instant usage, will be updated to cover more case

        if (transientAnnotated != null) {
            return;
        }

        if (column != null) {
            addColumn(field, index, metadata, column);
        } else {
            if (oneToOne == null && oneToMany == null && joinColumn == null && joinColumns == null) {
                addColumn(field, index, metadata, null);
            }
            if (joinColumn != null && oneToMany == null) {
                metadata.addJoinField(getJoinColumnMetadata(field.getType(), field.getName(), index, joinColumn));
            } else {
                if (joinColumns != null && oneToMany == null) {
                    metadata.addCompositeJoinField(getCompositeJoinColumnMetadata(field.getType(), field.getName(), index, joinColumns));
                } else if (oneToMany != null) {
                    parseOneToMany(field, index, metadata, oneToMany, joinColumn, joinColumns);
                } else {
                    if (oneToOne != null && !oneToOne.mappedBy().isEmpty()) {
                        parseOneToOne(field, index, metadata, oneToOne);
                    }
                }
            }
        }
    }

    /**
     * Builds composite join column metadata using {@code @JoinColumns}.
     *
     * @param type         the type of the referenced entity
     * @param name         the field name
     * @param index        the field index
     * @param joinColumns  the {@code @JoinColumns} annotation
     * @return {@link CompositeJoinColumnFieldMetadata} representing the composite join
     */
    private CompositeJoinColumnFieldMetadata getCompositeJoinColumnMetadata(Class<?> type, String name, int index, JoinColumns joinColumns) {
        CompositeJoinColumnFieldMetadata compositeJoinColumn = CompositeJoinColumnFieldMetadata.builder()
                .fieldType(type)
                .fieldName(name)
                .index(index)
                .build();

        Stream.of(joinColumns.value())
                .map(jc -> new CompositeJoinColumnFieldMetadata.Item(jc.name(), jc.columnDefinition(), jc.nullable(), jc.referencedColumnName()))
                .forEach(compositeJoinColumn::addItem);

        return compositeJoinColumn;
    }

    /**
     * Builds join column metadata for a field using {@code @JoinColumn}.
     *
     * @param type         the type of the referenced entity
     * @param name         the field name
     * @param index        the field index
     * @param joinColumn   the {@code @JoinColumn} annotation
     * @return {@link JoinColumnFieldMetadata} representing the join
     */
    private JoinColumnFieldMetadata getJoinColumnMetadata(Class<?> type, String name, int index, JoinColumn joinColumn) {

        ColumnMetadata columnMetadata = DefaultColumnMetadata.builder()
                .columnName(joinColumn.name())
                .columnDefinition(joinColumn.columnDefinition())
                .nullable(joinColumn.nullable())
                .build();

        return JoinColumnFieldMetadata.builder()
                .fieldType(type)
                .fieldName(name)
                .referenceColumnName(joinColumn.referencedColumnName())
                .columnMetadata(columnMetadata)
                .index(index)
                .build();
    }

    /**
     * Adds basic column metadata to the entity metadata, optionally using {@code @Column} and {@code @Id}.
     *
     * @param field    the field being scanned
     * @param index    the field index
     * @param metadata the metadata container
     * @param column   the column annotation (nullable)
     */
    private void addColumn(Field field, int index, JpaEntityMetadata metadata, Column column) {
        Id id = field.getDeclaredAnnotation(Id.class);
        ColumnMetadata columnMetadata;

        if (column != null) {
            columnMetadata = DefaultColumnMetadata.builder()
                    .columnName(column.name())
                    .columnDefinition(column.columnDefinition())
                    .nullable(column.nullable())
                    .primaryKey(id != null)
                    .build();
        } else {
            columnMetadata = DefaultColumnMetadata.builder()
                    .columnName(NamingUtil.camelToUnderscore(field.getName()))
                    .columnDefinition("")
                    .nullable(id == null)
                    .primaryKey(id != null)
                    .build();
        }

        FieldMetadata fieldMetadata = DefaultFieldMetadata.builder()
                .fieldName(field.getName())
                .fieldType(field.getType())
                .index(index)
                .columnMetadata(columnMetadata)
                .build();

        metadata.addField(fieldMetadata);

        if (id != null) {
            metadata.addIdField(fieldMetadata);
        }
    }

    /**
     * Handles {@code @OneToOne(mappedBy = ...)} relationship and adds inverse join metadata.
     *
     * @param field     the owning field
     * @param index     the field index
     * @param metadata  the metadata container
     * @param oneToOne  the {@code @OneToOne} annotation
     */
    private void parseOneToOne(Field field, int index, JpaEntityMetadata metadata, OneToOne oneToOne) {
        Class<?> foreignType = field.getType();
        parseJoinColumn(field, index, metadata, foreignType, oneToOne.mappedBy());
    }

    /**
     * Handles {@code @OneToMany} relationship and adds inverse or foreign join metadata accordingly.
     *
     * @param field        the field representing a collection
     * @param index        the field index
     * @param metadata     the metadata container
     * @param oneToMany    the {@code @OneToMany} annotation
     * @param joinColumn   optional {@code @JoinColumn} annotation
     * @param joinColumns  optional {@code @JoinColumns} annotation
     */
    private void parseOneToMany(Field field, int index, JpaEntityMetadata metadata, OneToMany oneToMany, JoinColumn joinColumn, JoinColumns joinColumns) {
        if (!Collection.class.isAssignableFrom(field.getType())) {
            return;
        }
        Class<?> foreignType = getCollectionType(field);
        if (!oneToMany.mappedBy().isEmpty()) {
            parseJoinColumn(field, index, metadata, foreignType, oneToMany.mappedBy());
        } else if (joinColumn != null) {
            metadata.addInverseJoinField(getJoinColumnMetadata(foreignType, field.getName(), index, joinColumn));
        } else if (joinColumns != null) {
            metadata.addInverseCompositeJoinField(getCompositeJoinColumnMetadata(foreignType, field.getName(), index, joinColumns));
        }
    }

    /**
     * Resolves the inverse join column metadata by inspecting the foreign class's field specified by {@code mappedBy}.
     *
     * @param field       the current field in the entity
     * @param index       the index of the field
     * @param metadata    the metadata container
     * @param foreignType the class containing the referenced field
     * @param mappedBy    the name of the field in the foreign class
     */
    private void parseJoinColumn(Field field, int index, JpaEntityMetadata metadata, Class<?> foreignType, String mappedBy) {
        Field foreignField = getForeignField(foreignType, mappedBy);
        JoinColumn foreignJoinColumn = foreignField.getDeclaredAnnotation(JoinColumn.class);
        if (foreignJoinColumn != null) {
            metadata.addInverseJoinField(getJoinColumnMetadata(foreignType, field.getName(), index, foreignJoinColumn));
        } else {
            JoinColumns foreignJoinColumns = foreignField.getDeclaredAnnotation(JoinColumns.class);
            if (foreignJoinColumns != null) {
                metadata.addInverseCompositeJoinField(getCompositeJoinColumnMetadata(foreignType, field.getName(), index, foreignJoinColumns));
            }
        }
    }

    /**
     * Retrieves the field from a foreign entity class by name.
     *
     * @param foreignType the class to search
     * @param mappedBy    the field name
     * @return the resolved {@link Field}
     * @throws IllegalArgumentException if the field does not exist
     */
    private Field getForeignField(Class<?> foreignType, String mappedBy) {
        try {
            return foreignType.getDeclaredField(mappedBy);
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException(String.format("Unable to locate field '%s' on class '%s'", mappedBy, foreignType.getName()));
        }
    }

    /**
     * Extracts the generic element type from a collection field.
     *
     * @param field the collection field
     * @return the actual type argument (i.e., element type)
     * @throws ClassCastException if the field is not properly parameterized
     */
    private Class<?> getCollectionType(Field field) {
        Type type = field.getGenericType();
        ParameterizedType pt = (ParameterizedType) type;
        return (Class<?>) pt.getActualTypeArguments()[0];
    }

}
