package vn.truongngo.lib.dynamicquery.projection.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares a raw SQL-compatible expression to be included in the SELECT clause of a query projection.
 * <p>
 * This annotation is used for advanced or custom expressions that cannot be modeled using simpler annotations
 * like {@code @Column}, {@code @Aggregate}, or {@code @Arithmetic}.
 * It allows direct definition of SQL or SQL-like expressions (e.g., "price * quantity", "CASE WHEN ... THEN ... END").
 * </p>
 *
 * <blockquote><pre>
 * // Example 1: Simple arithmetic expression
 * {@code @Expression}(value = "price * quantity", alias = "totalCost")
 * private BigDecimal totalCost;
 *
 * // Example 2: CASE WHEN expression
 * {@code @Expression}(value = "CASE WHEN status = 'ACTIVE' THEN 1 ELSE 0 END", alias = "isActive")
 * private Integer isActive;
 * </pre></blockquote>
 *
 * <p>
 * This is typically used for complex derived values that are not directly mapped to a single column or function.
 *
 * @author Truong Ngo
 * @version 2.0.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Expression {

    /**
     * The raw SQL-compatible expression to be selected.
     * <p>
     * Should be valid in the context of the underlying query engine. It may include columns, constants,
     * functions, case statements, and arithmetic expressions.
     *
     * @return the expression string
     */
    String value();

}
