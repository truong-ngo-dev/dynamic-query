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
     * Refers to either:
     * <ul>
     *     <li>A column in the entity (e.g., {@code "status"})</li>
     *     <li>An alias of a computed select field (e.g., {@code "totalCount"})</li>
     * </ul>
     * If left empty, the field name will be used as the default reference.
     *
     * @return the reference to a column, alias, or expression
     */
    String reference() default "";

    /**
     * Provides a raw SQL-compatible expression that represents the left-hand side of the predicate.
     * This is useful for advanced or computed expressions that not included in selected expression.
     * <p>
     * Example:
     * <pre>{@code
     * @Criteria(expression = "LOWER(username)")
     * private String username;
     * }</pre>
     * <p>
     * If this property is set, it takes precedence over {@link #reference()}.
     * </p>
     *
     * @return the SQL expression to use in the predicate
     */
    String expression() default "";

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
