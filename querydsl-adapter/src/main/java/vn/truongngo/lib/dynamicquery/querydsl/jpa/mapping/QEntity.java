package vn.truongngo.lib.dynamicquery.querydsl.jpa.mapping;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.*;
import com.querydsl.sql.ColumnMetadata;
import com.querydsl.sql.ForeignKey;
import com.querydsl.sql.RelationalPathBase;
import vn.truongngo.lib.dynamicquery.metadata.entity.FieldMetadata;

import java.util.List;

/**
 * A Querydsl {@link RelationalPathBase} implementation customized for JPA entities.
 *
 * <p>This class acts as a Querydsl entity path representation that supports
 * JPA-style entity metadata mapping based on field metadata.</p>
 *
 * @param <T> the type of the represented entity
 *
 * @author Truong Ngo
 * @version 2.0.0
 */
public class QEntity<T> extends RelationalPathBase<T> {

    /**
     * Constructs a new {@code QEntity} with the given entity type, variable, schema, and table name.
     *
     * @param type the entity class type
     * @param variable the variable name for this path
     * @param schema the database schema
     * @param table the database table name
     */
    public QEntity(Class<? extends T> type, String variable, String schema, String table) {
        super(type, variable, schema, table);
    }

    /**
     * Constructs a new {@code QEntity} with the given entity type, path metadata, schema, and table name.
     *
     * @param type the entity class type
     * @param metadata the path metadata
     * @param schema the database schema
     * @param table the database table name
     */
    public QEntity(Class<? extends T> type, PathMetadata metadata, String schema, String table) {
        super(type, metadata, schema, table);
    }

    /**
     * Creates a primary key constraint on this entity based on the given list of {@link FieldMetadata}.
     *
     * <p>This method maps the field names to corresponding Querydsl column paths
     * and creates the primary key on those columns.</p>
     *
     * @param ids the list of field metadata representing primary key columns
     */
    public void createPrimaryKey(List<FieldMetadata> ids) {
        List<String> pkColumnNames = ids.stream().map(FieldMetadata::getFieldName).toList();
        Path<?>[] pkPaths = this.getColumns().stream()
                .filter(e -> pkColumnNames.contains(e.getMetadata().getName()))
                .toArray(Path[]::new);
        createPrimaryKey(pkPaths);
    }

    @Override
    public <P extends Path<?>> P addMetadata(P path, ColumnMetadata metadata) {
        return super.addMetadata(path, metadata);
    }

    @Override
    protected <F> ForeignKey<F> createForeignKey(Path<?> local, String foreign) {
        return super.createForeignKey(local, foreign);
    }

    @Override
    protected <F> ForeignKey<F> createForeignKey(List<? extends Path<?>> local, List<String> foreign) {
        return super.createForeignKey(local, foreign);
    }


    @Override
    protected <A, E> ArrayPath<A, E> createArray(String property, Class<? super A> type) {
        return super.createArray(property, type);
    }

    @Override
    protected BooleanPath createBoolean(String property) {
        return super.createBoolean(property);
    }

    @Override
    @SuppressWarnings("rawtypes")
    protected <A extends Comparable> ComparablePath<A> createComparable(String property, Class<? super A> type) {
        return super.createComparable(property, type);
    }

    @Override
    protected <A extends Enum<A>> EnumPath<A> createEnum(String property, Class<A> type) {
        return super.createEnum(property, type);
    }

    @Override
    @SuppressWarnings("rawtypes")
    protected <A extends Comparable> DatePath<A> createDate(String property, Class<? super A> type) {
        return super.createDate(property, type);
    }

    @Override
    @SuppressWarnings("rawtypes")
    protected <A extends Comparable> DateTimePath<A> createDateTime(String property, Class<? super A> type) {
        return super.createDateTime(property, type);
    }

    @Override
    protected <A extends Number & Comparable<?>> NumberPath<A> createNumber(String property, Class<? super A> type) {
        return super.createNumber(property, type);
    }

    @Override
    protected <A> SimplePath<A> createSimple(String property, Class<? super A> type) {
        return super.createSimple(property, type);
    }

    @Override
    protected StringPath createString(String property) {
        return super.createString(property);
    }

    @Override
    @SuppressWarnings("rawtypes")
    protected <A extends Comparable> TimePath<A> createTime(String property, Class<? super A> type) {
        return super.createTime(property, type);
    }
}
