package vn.truongngo.lib.dynamicquery.projection.annotation;

import vn.truongngo.lib.dynamicquery.core.enumerate.AggregateFunction;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to declare an aggregate function in the SELECT clause of a query projection.
 * <p>
 * This annotation allows you to define common SQL aggregate functions such as {@code SUM}, {@code COUNT},
 * {@code MAX}, {@code MIN}, or {@code AVG} on a specific column from a query source.
 * </p>
 * <p>
 * The column specified must be a **column name** (not an expression or alias) and should be resolvable
 * from the main or joined data source.
 * </p>
 *
 * <h2>Usage:</h2>
 * <blockquote><pre>
 * &#64;Aggregate(function = "SUM", column = "salary", alias = "totalSalary")
 * private BigDecimal totalSalary;
 * </pre></blockquote>
 *
 * <h2>Example with JOIN:</h2>
 * <blockquote><pre>
 * &#64;Aggregate(function = "COUNT", column = "id", source = "order", alias = "totalOrders")
 * private Long totalOrders;
 * </pre></blockquote>
 *
 * @author Truong Ngo
 * @version 2.0.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Aggregate {

   /**
    * The aggregate function to be applied.
    * <p>
    * Examples: {@code SUM}, {@code COUNT}, {@code AVG}, {@code MAX}, {@code MIN}.
    *
    * @return the aggregate function enum
    */
    AggregateFunction function();

   /**
    * The name of the column on which the aggregate function is applied.
    * <p>
    * This must be a raw column name, not an alias or an expression.
    *
    * @return the column name to aggregate
    */
    String column();

    /**
     * The alias of the data source (FROM or JOIN) that contains the column.
     * <p>
     * If omitted, the main projection source is assumed.
     *
     * @return the alias of the source table or entity
     */
    String source() default "";

    /**
     * The alias for the resulting aggregated value in the projection result.
     *
     * @return the alias for the aggregated output
     */
    String alias();

    /**
     * Indicates whether to apply the {@code DISTINCT} modifier inside the aggregate function.
     * <p>
     * For example: {@code COUNT(DISTINCT user_id)}
     *
     * @return {@code true} to apply DISTINCT, {@code false} otherwise
     */
    boolean distinct() default false;
}
