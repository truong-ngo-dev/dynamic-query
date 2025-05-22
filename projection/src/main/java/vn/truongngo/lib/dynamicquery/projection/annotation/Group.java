package vn.truongngo.lib.dynamicquery.projection.annotation;

import vn.truongngo.lib.dynamicquery.core.enumerate.LogicalOperator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies a logical grouping for multiple criteria conditions,
 * allowing predicates to be combined using a logical operator (e.g., AND, OR).
 * <p>
 * This annotation is typically used in combination with {@link Criteria}
 * to group multiple conditions under the same logical context.
 * </p>
 *
 * <p>Example usage:</p>
 * <blockquote><pre>
 * &#64;Group(id = "group1", type = LogicalOperator.OR)
 * &#64;Criteria(column = "status", operator = Operator.EQUAL)
 *
 * &#64;Group(id = "group1")
 * &#64;Criteria(column = "type", operator = Operator.EQUAL)
 * </pre></blockquote>
 *
 * <p>In this example, the two criteria with the same group id <code>"group1"</code>
 * will be combined using the <code>OR</code> operator.</p>
 *
 * @author Truong Ngo
 * @version 2.0.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Group {

    /**
     * Specifies the logical operator used to combine the grouped criteria.
     * Default is {@link LogicalOperator#AND}.
     *
     * @return the logical operator
     */
    LogicalOperator type() default LogicalOperator.AND;

    /**
     * Group identifier to associate multiple criteria into the same logical group.
     * <p>
     * If multiple fields share the same group id, they will be grouped together.
     * </p>
     *
     * @return the group identifier
     */
    String id() default "";
}
