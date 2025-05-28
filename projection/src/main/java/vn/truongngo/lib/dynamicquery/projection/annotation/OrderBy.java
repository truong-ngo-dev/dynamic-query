package vn.truongngo.lib.dynamicquery.projection.annotation;

import vn.truongngo.lib.dynamicquery.core.enumerate.Order;

import java.lang.annotation.*;

/**
 * Annotation to specify ORDER BY clause elements in a query projection.
 * <p>
 * Ordering can be declared in two ways:
 * <ul>
 *   <li>By referencing a selected column or expression alias using {@link #reference()}</li>
 *   <li>By specifying a raw SQL expression directly using {@link #expression()}</li>
 * </ul>
 * <p>
 * The primary method is using {@code reference()}, which maps to existing projection selections.
 * If the reference cannot be resolved from the projection, it will fallback to the source entity
 * (FROM or JOIN) specified via {@code sourceAlias} to find a matching field using reflection.
 * <p>
 * If ordering by a derived expression not present in either the projection or the source entity,
 * then {@code expression()} must be used to provide the SQL fragment directly.
 *
 * <h2>Resolution Logic:</h2>
 * <ol>
 *   <li>Try resolving {@code reference} from projection's {@code SelectionDescriptor} (by alias or column name)</li>
 *   <li>If not found, use {@code sourceAlias} to inspect the corresponding entity class and locate the field</li>
 *   <li>If unresolved or ordering by expression, use {@code expression()}</li>
 * </ol>
 *
 * <h2>Examples:</h2>
 *
 * <h3>Order by an alias in the projection:</h3>
 * <blockquote><pre>
 * &#64;OrderBy(reference = "createdDate", order = Order.DESC)
 * public class UserProjection {
 *     &#64;Column(name = "created_at", alias = "createdDate")
 *     private LocalDateTime createdAt;
 * }
 * </pre></blockquote>
 *
 * <h3>Order by a column from a joined source:</h3>
 * <blockquote><pre>
 * &#64;OrderBy(reference = "username", sourceAlias = "u")
 * &#64;Join(entity = User.class, alias = "u", ...)
 * public class LogProjection {
 *     &#64;Column(name = "message")
 *     private String message;
 * }
 * </pre></blockquote>
 *
 * <h3>Order by a SQL expression:</h3>
 * <blockquote><pre>
 * &#64;OrderBy(expression = "LENGTH(message)", order = Order.DESC)
 * public class LogProjection {
 *     &#64;Column(name = "message")
 *     private String message;
 * }
 * </pre></blockquote>
 *
 * @author Truong Ngo
 * @version 2.1.0
 */
@Repeatable(value = OrderBy.List.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface OrderBy {

    /**
     * Reference to the alias or name of a selected column or expression to be used in the ORDER BY clause.
     * <p>
     * This should match either an alias declared in the projection, or a column from the source entity.
     * If this cannot be resolved, {@link #expression()} must be used instead.
     * <p>
     * Ignored if {@code expression()} is provided.
     *
     * @return the referenced alias or column name
     */
    String reference() default "";

    /**
     * The alias of the data source (FROM or JOIN) used to locate the source entity class if {@code reference}
     * cannot be found in the projection.
     * <p>
     * If omitted, the primary source defined in {@code @Projection} will be used.
     *
     * @return the alias of the source entity
     */
    String sourceAlias() default "";

    /**
     * A raw SQL expression to be used directly in the ORDER BY clause.
     * <p>
     * This is used when ordering is based on expressions not present in the projection or entity.
     * <p>
     * If provided, this takes precedence over {@code reference()} and {@code sourceAlias()}.
     *
     * @return the SQL expression used for ordering
     */
    String expression() default "";

    /**
     * Defines the ordering direction, either ascending or descending.
     *
     * @return the order direction, default is {@link Order#ASC}
     */
    Order order() default Order.ASC;

    /**
     * Container annotation to allow multiple {@code @OrderBy} annotations on the same class.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface List {
        OrderBy[] value();
    }
}
