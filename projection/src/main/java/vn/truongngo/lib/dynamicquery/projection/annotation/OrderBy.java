package vn.truongngo.lib.dynamicquery.projection.annotation;

import vn.truongngo.lib.dynamicquery.core.enumerate.Order;

import java.lang.annotation.*;

/**
 * Annotation to specify ORDER BY clause elements in a query projection.
 * <p>
 * The {@code reference} value should correspond to the alias or name of a selected column
 * or expression. This typically refers to fields annotated with {@code @Column} or other
 * expression-related annotations in the projection.
 * <p>
 * If not found in the projection, it may refer to a column defined in one of the query sources
 * (i.e., FROM or JOIN), using the associated {@code sourceAlias}.
 * </p>
 * <p>
 * Defines the sorting order for the specified reference in the query result.
 * </p>
 *
 * <p><b>Usage:</b></p>
 * <blockquote><pre>
 * &#64;OrderBy(reference = "createdDate", order = Order.DESC)
 * public class UserProjection {
 *     &#64;Column(name = "created_at", alias = "createdDate")
 *     private LocalDateTime createdAt;
 *     ...
 * }
 * </pre></blockquote>
 *
 * @author Truong Ngo
 * @version 2.0.0
 */
@Repeatable(value = OrderBy.List.class)
@Target(ElementType.TYPE)
public @interface OrderBy {

    /**
     * Reference to the alias or name of a selected column or expression to be used in the ORDER BY clause.
     * <p>
     * This should match either an alias (name) declared in the projection (SELECT clause), or a column defined
     * in one of the query sources (FROM or JOIN).
     *
     * @return the referenced alias or name for ordering
     */
    String reference();

    /**
     * The alias of the data source (FROM or JOIN) that contains the referenced column or expression. <p>
     * This should match the alias defined in either the main source (@Projection) or one of the joined sources (@Join).
     * <p>
     * If the referenced column or expression is already defined in the projection (i.e., selected in the SELECT clause),
     * this value can be omitted.
     * <p>
     * If this value is not defined, the main source will be applied by default.
     *
     * @return the alias of the source from which the referenced column originates
     */
    String sourceAlias() default "";

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
