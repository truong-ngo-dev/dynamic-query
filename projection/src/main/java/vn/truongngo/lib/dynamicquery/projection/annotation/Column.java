package vn.truongngo.lib.dynamicquery.projection.annotation;

import vn.truongngo.lib.dynamicquery.core.builder.QueryMetadata;
import vn.truongngo.lib.dynamicquery.core.expression.ColumnReferenceExpression;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated field represents a column in the resulting query projection.
 * This annotation is intended to be used within a class annotated with {@link Projection}.
 * It supports specifying the exact column name, an optional alias for the column, and the source alias
 * (useful when working with joins or subqueries).
 *
 * <p>This metadata will be processed and converted into a {@link ColumnReferenceExpression} within the
 * {@link QueryMetadata}.</p>
 *
 * <h2>Usage Example</h2>
 * <blockquote><pre>
 * {@code @Projection}(entity = User.class, alias = "u")
 * public class UserProjection {
 *
 *     {@code @Column}(name = "id")
 *     private Long id;
 *
 *     {@code @Column}(name = "username", alias = "user_name", from = "u")
 *     private String username;
 * }
 * </pre></blockquote>
 *
 * <p>This will result in query expressions like:</p>
 * <blockquote><pre>
 * SELECT u.id, u.username AS user_name
 * FROM User u
 * </pre></blockquote>
 *
 * @author Truong Ngo
 * @version 2.0.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {

    /**
     * The name of the column in the source entity.
     *
     * @return the name of the column to select
     */
    String name() default "";

    /**
     * Optional alias to use in the select clause of the query.
     * If not provided, the field name will be used as the alias.
     *
     * @return the alias of the column in the result set
     */
    String alias() default "";

    /**
     * The alias of the table or subquery this column comes from.
     * This is particularly useful when dealing with joins, subqueries,
     * or disambiguating columns from multiple sources.
     *
     * @return the source alias (e.g., table alias) for the column
     */
    String from() default "";

}
