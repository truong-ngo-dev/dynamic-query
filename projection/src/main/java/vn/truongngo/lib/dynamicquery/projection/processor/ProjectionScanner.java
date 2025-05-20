package vn.truongngo.lib.dynamicquery.projection.processor;

import vn.truongngo.lib.dynamicquery.metadata.utils.NamingUtil;
import vn.truongngo.lib.dynamicquery.projection.annotation.*;
import vn.truongngo.lib.dynamicquery.projection.descriptor.*;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * Utility class responsible for scanning and parsing projection classes annotated
 * with custom annotations such as {@code @Projection}, {@code @Column}, {@code @Aggregate},
 * {@code @Arithmetic}, {@code @Subquery}, and others.
 * <p>
 * This class inspects the given projection class and extracts metadata for
 * building query projections, including selected columns, joins, group by,
 * order by, and computed expressions.
 * </p>
 *
 * <h2>Usage Example</h2>
 * <blockquote><pre>
 * ProjectionDescriptor descriptor = ProjectionScanner.scanProjection(MyProjection.class);
 * </pre></blockquote>
 *
 * @author Truong Ngo
 * @version 2.0.0
 */
public class ProjectionScanner {

    /**
     * Scans the given projection class and builds a {@link ProjectionDescriptor}
     * containing metadata extracted from annotations.
     *
     * @param projectionClass the class annotated with {@code @Projection} representing the projection
     * @return a {@code ProjectionDescriptor} encapsulating all metadata for the projection
     * @throws NullPointerException if {@code projectionClass} is {@code null}
     * @throws IllegalArgumentException if the class is not annotated with {@code @Projection}
     */
    public static ProjectionDescriptor scanProjection(Class<?> projectionClass) {
        Objects.requireNonNull(projectionClass);
        ProjectionDescriptor descriptor = new ProjectionDescriptor();
        scanEntity(descriptor, projectionClass);
        scanJoin(descriptor, projectionClass);
        scanSelection(descriptor, projectionClass);
        scanGroupBy(descriptor, projectionClass);
        scanOrderBy(descriptor, projectionClass);

        return descriptor;
    }

    /**
     * Scans the {@code @Projection} annotation on the projection class and sets
     * the basic projection metadata such as target class, entity class, alias, and distinct flag.
     *
     * @param descriptor the {@link ProjectionDescriptor} to update
     * @param projectionClass the projection class being scanned
     * @throws IllegalArgumentException if the projection class is not annotated with {@code @Projection}
     */
    private static void scanEntity(ProjectionDescriptor descriptor, Class<?> projectionClass) {
        Projection projection = projectionClass.getDeclaredAnnotation(Projection.class);
        if (projection == null) throw new IllegalArgumentException("Projection class " + projectionClass.getName() + " is not annotated with @Projection");
        descriptor.setTarget(projectionClass);
        descriptor.setEntity(projection.entity());
        descriptor.setAlias(projection.alias() == null ? NamingUtil.camelToUnderscore(projectionClass.getSimpleName()) : projection.alias());
        descriptor.setDistinct(projection.distinct());
    }

    /**
     * Scans the {@code projectionClass} for all {@link Join} annotations and
     * converts each annotation into a corresponding {@link JoinDescriptor}.
     * <p>
     * Each {@code JoinDescriptor} is constructed by extracting attributes from
     * the {@code Join} annotation, with sensible defaults:
     * <ul>
     *     <li>If {@code source()} is {@code Void.class}, the main entity class from the descriptor is used.</li>
     *     <li>If {@code sourceAlias()} is empty, the main alias from the descriptor is used.</li>
     * </ul>
     * The created {@code JoinDescriptor} instances are added to the given {@code ProjectionDescriptor}.
     * </p>
     *
     * @param descriptor the {@code ProjectionDescriptor} to which the parsed join descriptors will be added
     * @param projectionClass the projection class to scan for {@code Join} annotations
     */
    private static void scanJoin(ProjectionDescriptor descriptor, Class<?> projectionClass) {
        Join[] joins = projectionClass.getDeclaredAnnotationsByType(Join.class);
        for (Join join : joins) {
            JoinDescriptor joinDescriptor = JoinDescriptor.builder()
                    .joinType(join.joinType())
                    .targetEntity(join.target())
                    .targetAlias(join.targetAlias())
                    .targetColumn(join.targetColumn())
                    .sourceEntity(join.source() == Void.class ? descriptor.getEntity() : join.source())
                    .sourceAlias(join.sourceAlias().isEmpty() ? descriptor.getAlias() : join.sourceAlias())
                    .sourceColumn(join.sourceColumn())
                    .build();
            descriptor.addJoin(joinDescriptor);
        }
    }

    /**
     * Scans all declared fields of the projection class for selection-related annotations
     * such as {@code @Column}, {@code @Aggregate}, {@code @Arithmetic}, {@code @Subquery},
     * and {@code @Expression}, and adds corresponding selection descriptors.
     * <p>
     * The {@code index} parameter is assigned incrementally to each selected element
     * to indicate the order in the SELECT clause.
     * </p>
     *
     * @param descriptor the {@link ProjectionDescriptor} to update
     * @param projectionClass the projection class being scanned
     */
    private static void scanSelection(ProjectionDescriptor descriptor, Class<?> projectionClass) {
        Field[] fields = projectionClass.getDeclaredFields();
        int index = 0;
        for (Field field : fields) {
            if (field.isAnnotationPresent(Column.class)) scanColumn(descriptor, field, index++);
            if (field.isAnnotationPresent(Aggregate.class)) scanAggregate(descriptor, field, index++);
            if (field.isAnnotationPresent(Arithmetic.class)) scanArithmetic(descriptor, field, index++);
            if (field.isAnnotationPresent(Subquery.class)) scanSubquery(descriptor, field, index++);
            if (field.isAnnotationPresent(Expression.class)) scanExpression(descriptor, field, index++);
        }
    }

    /**
     * Creates and adds a {@link ColumnDescriptor} based on the {@code @Column} annotation
     * present on the given field.
     *
     * @param descriptor the {@link ProjectionDescriptor} to update
     * @param field the field annotated with {@code @Column}
     * @param index the index representing the order in the SELECT clause
     */
    private static void scanColumn(ProjectionDescriptor descriptor, Field field, int index) {
        Column column = field.getDeclaredAnnotation(Column.class);
        ColumnDescriptor columnDescriptor = ColumnDescriptor.builder()
                .name(column.name().isEmpty() ? field.getName() : column.name())
                .alias(column.alias())
                .from(column.from() == null ? descriptor.getAlias() : column.from())
                .index(index)
                .build();
        descriptor.addSelect(columnDescriptor);
    }

    /**
     * Creates and adds an {@link AggregateDescriptor} based on the {@code @Aggregate} annotation
     * present on the given field.
     *
     * @param descriptor the {@link ProjectionDescriptor} to update
     * @param field the field annotated with {@code @Aggregate}
     * @param index the index representing the order in the SELECT clause
     */
    private static void scanAggregate(ProjectionDescriptor descriptor, Field field, int index) {
        Aggregate aggregate = field.getDeclaredAnnotation(Aggregate.class);
        ColumnDescriptor columnDescriptor = ColumnDescriptor.builder()
                .name(aggregate.column())
                .from(aggregate.source().isEmpty() ? descriptor.getAlias() : aggregate.source())
                .build();
        AggregateDescriptor aggregateDescriptor = AggregateDescriptor.builder()
                .function(aggregate.function())
                .column(columnDescriptor)
                .alias(aggregate.alias())
                .distinct(aggregate.distinct())
                .index(index)
                .build();
        descriptor.addSelect(aggregateDescriptor);
    }

    /**
     * Creates and adds an {@link ArithmeticDescriptor} based on the {@code @Arithmetic} annotation
     * present on the given field, representing a basic arithmetic expression.
     *
     * @param descriptor the {@link ProjectionDescriptor} to update
     * @param field the field annotated with {@code @Arithmetic}
     * @param index the index representing the order in the SELECT clause
     */
    private static void scanArithmetic(ProjectionDescriptor descriptor, Field field, int index) {
        Arithmetic arithmetic = field.getDeclaredAnnotation(Arithmetic.class);
        ColumnDescriptor left = ColumnDescriptor.builder()
                .name(arithmetic.left())
                .from(arithmetic.leftSource().isEmpty() ? descriptor.getAlias() : arithmetic.leftSource())
                .build();
        ColumnDescriptor right = ColumnDescriptor.builder()
                .name(arithmetic.right())
                .from(arithmetic.rightSource().isEmpty() ? descriptor.getAlias() : arithmetic.rightSource())
                .build();
        ArithmeticDescriptor arithmeticDescriptor = ArithmeticDescriptor.builder()
                .left(left)
                .right(right)
                .operator(arithmetic.operator())
                .alias(arithmetic.alias())
                .index(index)
                .build();
        descriptor.addSelect(arithmeticDescriptor);
    }

    /**
     * Creates and adds a {@link SubqueryDescriptor} based on the {@code @Subquery} annotation
     * present on the given field.
     *
     * @param descriptor the {@link ProjectionDescriptor} to update
     * @param field the field annotated with {@code @Subquery}
     * @param index the index representing the order in the SELECT clause
     */
    private static void scanSubquery(ProjectionDescriptor descriptor, Field field, int index) {
        Subquery subquery = field.getDeclaredAnnotation(Subquery.class);
        SubqueryDescriptor subqueryDescriptor = SubqueryDescriptor.builder()
                .targetProjection(subquery.target())
                .column(subquery.column())
                .alias(subquery.alias())
                .index(index)
                .build();
        descriptor.addSelect(subqueryDescriptor);
    }

    /**
     * Creates and adds an {@link ExpressionDescriptor} based on the {@code @Expression} annotation
     * present on the given field.
     *
     * @param descriptor the {@link ProjectionDescriptor} to update
     * @param field the field annotated with {@code @Expression}
     * @param index the index representing the order in the SELECT clause
     */
    private static void scanExpression(ProjectionDescriptor descriptor, Field field, int index) {
        Expression expression = field.getDeclaredAnnotation(Expression.class);
        ExpressionDescriptor expressionDescriptor = ExpressionDescriptor.builder()
                .expression(expression.value())
                .alias(expression.alias())
                .index(index)
                .build();
        descriptor.addSelect(expressionDescriptor);

    }

    /**
     * Scans all {@code @GroupBy} annotations on the projection class and adds corresponding
     * group-by descriptors.
     *
     * @param descriptor the {@link ProjectionDescriptor} to update
     * @param projectionClass the projection class being scanned
     */
    private static void scanGroupBy(ProjectionDescriptor descriptor, Class<?> projectionClass) {
        GroupBy[] groupBys = projectionClass.getDeclaredAnnotationsByType(GroupBy.class);
        for (GroupBy groupBy : groupBys) {
            SelectDescriptor selectDescriptor = groupBy.expression().isEmpty() ?
                    getSelect(descriptor, groupBy.reference(), groupBy.sourceAlias()) :
                    ExpressionDescriptor.builder().expression(groupBy.expression()).build();
            descriptor.addGroupBy(selectDescriptor);
        }
    }

    /**
     * Scans all {@code @OrderBy} annotations on the projection class and adds corresponding
     * order-by descriptors.
     *
     * @param descriptor the {@link ProjectionDescriptor} to update
     * @param projectionClass the projection class being scanned
     */
    private static void scanOrderBy(ProjectionDescriptor descriptor, Class<?> projectionClass) {
        OrderBy[] orderBys = projectionClass.getDeclaredAnnotationsByType(OrderBy.class);
        for (OrderBy orderBy : orderBys) {
            SelectDescriptor selectDescriptor = orderBy.expression().isEmpty() ?
                    getSelect(descriptor, orderBy.reference(), orderBy.sourceAlias()) :
                    ExpressionDescriptor.builder().expression(orderBy.expression()).build();
            OrderByDescriptor orderByDescriptor = OrderByDescriptor.builder()
                    .selection(selectDescriptor)
                    .order(orderBy.order())
                    .build();
            descriptor.addOrderBy(orderByDescriptor);
        }
    }

    /**
     * Attempts to find a {@link SelectDescriptor} within the projection descriptor
     * or in external joined entities by identifier (alias or column name).
     * Used to resolve references in {@code @GroupBy} and {@code @OrderBy}.
     *
     * @param descriptor the {@link ProjectionDescriptor} containing selects and joins
     * @param identifier the alias or column name to look for
     * @param sourceAlias the alias of the source entity or join target
     * @return the matched {@link SelectDescriptor}
     * @throws IllegalArgumentException if no matching selection is found
     */
    private static SelectDescriptor getSelect(ProjectionDescriptor descriptor, String identifier, String sourceAlias) {
        SelectDescriptor selectDescriptor = getSelectInternal(descriptor, identifier);
        if (selectDescriptor != null) return selectDescriptor;
        selectDescriptor = getSelectExternal(descriptor, identifier, sourceAlias);
        if (selectDescriptor != null) return selectDescriptor;
        throw new IllegalArgumentException("Identifier declared in @GroupBy or @OrderBy: " + identifier + " not found in projection descriptor or source entity");
    }

    /**
     * Searches for a {@link SelectDescriptor} within the given {@link ProjectionDescriptor}
     * by matching the identifier with either the alias or the column name of the selection.
     * <p>
     * This method only looks within the direct selections of the projection descriptor
     * and does not check joined entities.
     * </p>
     *
     * @param descriptor the {@code ProjectionDescriptor} containing the select descriptors
     * @param identifier the alias or name of the selection to find
     * @return the matching {@code SelectDescriptor} if found; {@code null} otherwise
     */
    private static SelectDescriptor getSelectInternal(ProjectionDescriptor descriptor, String identifier) {
        for (SelectDescriptor select : descriptor.getSelects()) {
            if (select instanceof ColumnDescriptor columnDescriptor) {
                if (columnDescriptor.getAlias() != null && columnDescriptor.getAlias().equals(identifier)) {
                    return columnDescriptor;
                } else if (columnDescriptor.getName() != null && columnDescriptor.getName().equals(identifier)) {
                    return columnDescriptor;
                }
            } else {
                if (select.getAlias() != null && select.getAlias().equals(identifier)) {
                    return select;
                }
            }
        }
        return null;
    }

    /**
     * Searches for a column descriptor in an external source entity or joined entity,
     * identified by the given {@code sourceAlias}, matching the specified identifier
     * with the field name in the entity class.
     * <p>
     * If the {@code sourceAlias} matches the main entity alias of the descriptor,
     * the entity class of the descriptor is searched; otherwise, the class of the
     * joined target with the given alias is searched.
     * </p>
     *
     * @param descriptor the {@code ProjectionDescriptor} containing metadata including joins
     * @param identifier the field name to search for in the external source entity
     * @param sourceAlias the alias of the source or join entity to search within
     * @return a new {@code ColumnDescriptor} representing the matched column if found; {@code null} otherwise
     * @throws IllegalArgumentException if the source alias cannot be resolved to an entity class
     */
    private static SelectDescriptor getSelectExternal(ProjectionDescriptor descriptor, String identifier, String sourceAlias) {
        Class<?> sourceClass = Objects.equals(sourceAlias, descriptor.getAlias()) ?
                descriptor.getEntity() :
                descriptor.getJoinTargetByAlias(sourceAlias);

        if (sourceClass == null) {
            throw new IllegalArgumentException("Source class " + sourceAlias + " not found in projection descriptor");
        }

        Field[] fields = sourceClass.getDeclaredFields();
        for (Field field : fields) {
            if (field.getName().equals(identifier)) {
                return ColumnDescriptor.builder()
                        .name(field.getName())
                        .from(sourceAlias)
                        .build();
            }
        }
        return null;
    }
}
