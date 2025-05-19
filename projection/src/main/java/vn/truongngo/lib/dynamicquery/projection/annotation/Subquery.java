package vn.truongngo.lib.dynamicquery.projection.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to declare a scalar subquery used as a selected expression in a query projection.
 * <p>
 * This annotation allows embedding a subquery that returns a single value (scalar) within the SELECT clause.
 * The subquery is defined by specifying the target entity (or projection) class and the column to select.
 * </p>
 *
 * <h2>Usage Example</h2>
 * <blockquote><pre>
 * {@code @Subquery}(target = Order.class, column = "totalAmount", alias = "maxOrderAmount")
 * private BigDecimal maxOrderAmount;
 * </pre></blockquote>
 *
 * <p>
 * The {@code target} specifies the entity or projection class representing the subquery source.
 * The {@code column} is the name of the column or expression selected in the subquery.
 * The {@code alias} is an optional alias for the projected subquery result.
 * </p>
 *
 * <p>
 * The framework should build the subquery automatically based on the target class and column information.
 * </p>
 *
 * @author Truong Ngo
 * @version 2.0.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Subquery {

    /**
     * The target entity or projection class used as the source of the subquery.
     *
     * @return the class representing the subquery source
     */
    Class<?> target();

    /**
     * The column or expression to select in the subquery.
     *
     * @return the column name or expression in the subquery
     */
    String column();

    /**
     * The alias to assign to the subquery result in the projection.
     * If omitted, the field name may be used as the alias.
     *
     * @return the alias of the subquery result
     */
    String alias() default "";
}
