package vn.truongngo.lib.dynamicquery.projection.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated field represents a column in the resulting query projection.
 * This annotation is intended to be used within a class annotated with {@link Projection}.
 *
 * <p>The {@code name} attribute defines the column name in the source entity or query.
 * The alias in the generated query will always match the field name.</p>
 *
 * <h2>Usage Example</h2>
 * <blockquote><pre>
 * {@code @Projection}(entity = User.class, alias = "u")
 * public class UserProjection {
 *
 *     {@code @Column}(name = "user_name")
 *     private String username;
 * }
 * </pre></blockquote>
 *
 * <p>Will result in:</p>
 * <blockquote><pre>
 * SELECT u.user_name AS username
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
     * The name of the column in the source entity or query.
     *
     * <p>If not specified, the field name will be used as the column name.
     * If specified and differs from the field name, the field name will be used as the alias.</p>
     *
     * <p>Examples:</p>
     * <blockquote><pre>
     * {@code @}Column
     * private String username;              // SQL: SELECT u.username
     *
     * {@code @}Column(name = "username")
     * private String username;              // SQL: SELECT u.username
     *
     * {@code @}Column(name = "user_name")
     * private String username;              // SQL: SELECT u.user_name AS username
     * </pre></blockquote>
     *
     * @return the column name to select
     */
    String name() default "";

    /**
     * The alias of the table or subquery this column comes from.
     * This is particularly useful when dealing with joins, subqueries,
     * or disambiguating columns from multiple sources.
     *
     * @return the source alias (e.g., table alias) for the column
     */
    String from() default "";

}
