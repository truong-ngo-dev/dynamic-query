package vn.truongngo.lib.dynamicquery.metadata.scanner;

import jakarta.persistence.*;
import vn.truongngo.lib.dynamicquery.metadata.db.ColumnMetadata;
import vn.truongngo.lib.dynamicquery.metadata.db.DefaultColumnMetadata;
import vn.truongngo.lib.dynamicquery.metadata.db.DefaultTableMetadata;
import vn.truongngo.lib.dynamicquery.metadata.db.TableMetadata;
import vn.truongngo.lib.dynamicquery.metadata.entity.DefaultFieldMetadata;
import vn.truongngo.lib.dynamicquery.metadata.entity.EntityMetadata;
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
 * and produces corresponding {@link JpaEntityMetadata} which contains table and field metadata used for querying.
 * <p>
 * It processes annotations such as {@code @Entity}, {@code @Table}, {@code @Column}, {@code @Id}, {@code @OneToMany},
 * {@code @JoinColumn}, {@code @JoinColumns}, and {@code @OneToOne} to build comprehensive metadata including:
 * - Table name and schema
 * - Column definitions
 * - Primary key fields
 * - Join and inverse join fields
 * </p>
 *
 * <p><b>Example usage:</b></p>
 * <blockquote><pre>
 * EntityScanner&lt;Class&lt;?&gt;&gt; scanner = new JpaEntityScanner();
 * EntityMetadata metadata = scanner.scan(MyJpaEntity.class);
 * </pre></blockquote>
 *
 * @author Truong Ngo
 * @version 1.0
 */
public class JpaEntityScanner implements EntityScanner<Class<?>> {

    @Override
    public EntityMetadata scan(Class<?> source) {
        JpaEntityMetadata metadata = scanTable(source);
        scanColumns(metadata, source);
        return metadata;
    }

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

    private void scanColumns(JpaEntityMetadata metadata, Class<?> source) {
        Class<?> prev = source;
        while (!prev.equals(Object.class)) {
            for (int i = 0; i < prev.getDeclaredFields().length; i++) {
                parse(prev.getDeclaredFields()[i], i + 1, metadata);
            }
            prev = prev.getSuperclass();
        }
        if (metadata.getIdFields().isEmpty()) {
            throw new IllegalArgumentException("No @Id, @E annotation found for " + source);
        }
    }

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

    private void addColumn(Field field, int index, JpaEntityMetadata metadata, Column column) {
        Id id = field.getDeclaredAnnotation(Id.class);
        ColumnMetadata columnMetadata;

        if (column != null) {
            columnMetadata = DefaultColumnMetadata.builder()
                    .columnName(column.name())
                    .columnDefinition(column.columnDefinition())
                    .nullable(column.nullable())
                    .build();
        } else {
            columnMetadata = DefaultColumnMetadata.builder()
                    .columnName(NamingUtil.camelToUnderscore(field.getName()))
                    .columnDefinition("")
                    .nullable(id == null)
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

    private void parseOneToOne(Field field, int index, JpaEntityMetadata metadata, OneToOne oneToOne) {
        Class<?> foreignType = field.getType();
        parseJoinColumn(field, index, metadata, foreignType, oneToOne.mappedBy());
    }

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

    private void parseJoinColumn(Field field, int index, JpaEntityMetadata metadata, Class<?> foreignType, String s) {
        Field foreignField = getForeignField(foreignType, s);
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

    private Field getForeignField(Class<?> foreignType, String mappedBy) {
        try {
            return foreignType.getDeclaredField(mappedBy);
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException(String.format("Unable to locate field '%s' on class '%s'", mappedBy, foreignType.getName()));
        }
    }

    private Class<?> getCollectionType(Field field) {
        Type type = field.getGenericType();
        ParameterizedType pt = (ParameterizedType) type;
        return (Class<?>) pt.getActualTypeArguments()[0];
    }

}
