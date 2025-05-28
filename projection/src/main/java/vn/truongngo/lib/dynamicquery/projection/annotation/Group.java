package vn.truongngo.lib.dynamicquery.projection.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies a logical grouping for multiple criteria conditions,
 * allowing predicates to be combined using a logical operator (e.g., AND, OR).
 * <p>
 * This annotation is typically used in combination with {@code Criteria}
 * to group multiple conditions under the same logical context.
 * </p>
 *
 * <h2>Example usage:</h2>
 * <blockquote><pre>
 * &#64;Group(id = "group1")
 * &#64;Criteria(column = "status", operator = Operator.EQUAL)
 *
 * &#64;Group(id = "group1")
 * &#64;Criteria(column = "type", operator = Operator.EQUAL)
 * </pre></blockquote>
 *
 * <p>In this example, the two criteria with the same group id {@code "group1"}
 * will be combined using the default logical operator (e.g., AND or as defined in {@link GroupDefinition}).</p>
 *
 * @author Truong Ngo
 * @version 2.0.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Group {

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
