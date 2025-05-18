package vn.truongngo.lib.dynamicquery.projection.annotation;

import java.lang.annotation.*;

/**
 * Annotation to specify GROUP BY clause elements in a query projection.
 * <p>
 * The {@code reference} value should correspond to the alias or name of a selected column
 * or expression. This typically refers to fields annotated with {@code @Column} or other
 * expression-related annotations in the projection.
 * </p>
 * <p>
 * If not found in the projection, it may refer to a column defined in one of the query sources
 * (i.e., FROM or JOIN), using the associated {@code sourceAlias}.
 * </p>
 * <p>
 * This allows grouping by any calculated or aliased value rather than raw column names.
 * </p>
 * <h2><b>Usage:</b></h2>
 * <blockquote><pre>
 * &#64;GroupBy(reference = "totalAmount")
 * public class OrderProjection {
 *     &#64;Column(name = "amount", alias = "totalAmount")
 *     private BigDecimal amount;
 *     ...
 * }
 * </pre></blockquote>
 *
 * @author Truong Ngo
 * @version 2.0.0
 */
@Repeatable(GroupBy.List.class)
@Target(ElementType.TYPE)
public @interface GroupBy {

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
     * Container annotation to allow multiple {@code @GroupBy} annotations on the same class.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface List {
        GroupBy[] value();
    }
}
