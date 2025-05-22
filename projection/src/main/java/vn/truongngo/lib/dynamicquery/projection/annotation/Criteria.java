package vn.truongngo.lib.dynamicquery.projection.annotation;

import vn.truongngo.lib.dynamicquery.core.enumerate.Operator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies criteria metadata for a field used in dynamic query construction.
 *
 * <p>This annotation is typically applied to fields in a criteria class,
 * indicating how the field should be interpreted in query filtering logic.
 * It supports specifying the comparison operator and the optional alias
 * of the source (typically a table or entity alias) the field belongs to.</p>
 *
 * <h2>Example usage:</h2>
 * <blockquote><pre>
 * public class AccountCriteria {
 *
 *     {@code @Criteria}(operator = Operator.LIKE, sourceAlias = "a")
 *     private String username;
 *
 *     {@code @Criteria}
 *     private Long id;
 * }
 * </pre></blockquote>
 *
 * @author Truong Ngo
 * @version 2.0.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Criteria {

    /**
     * Defines the comparison operator to be used for the annotated field.
     * Default is {@code EQUAL}.
     *
     * @return the comparison operator
     */
    Operator operator() default Operator.EQUAL;

    /**
     * Specifies the source alias (e.g., table alias or entity alias) to
     * prepend to the field name in the generated query.
     * Default is an empty string, indicating no alias.
     *
     * @return the source alias for the field
     */
    String sourceAlias() default "";
}
