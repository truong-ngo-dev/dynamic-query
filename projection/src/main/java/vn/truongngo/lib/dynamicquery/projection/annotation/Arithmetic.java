package vn.truongngo.lib.dynamicquery.projection.annotation;

import vn.truongngo.lib.dynamicquery.core.enumerate.ArithmeticOperator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents a basic arithmetic operation between two direct operands in a query projection.
 * <p>
 * This annotation defines a flat (non-nested) arithmetic expression involving two elements,
 * such as column names or constant values. Nested arithmetic (i.e., chaining expressions via aliases)
 * is not supported by this annotation.
 * </p>
 *
 * <h2>Usage Example</h2>
 * <blockquote><pre>
 * {@code @Arithmetic}(
 *     left = "price",
 *     leftSource = "invoice",
 *     right = "quantity",
 *     rightSource = "item",
 *     operator = ArithmeticOperator.MULTIPLY,
 *     alias = "totalCost"
 * )
 * </pre></blockquote>
 * <p>
 * This expression will be resolved into an arithmetic operation in the final query using the
 * provided sources and operands, and projected with the specified alias.
 * </p>
 *
 * @author Truong Ngo
 * @version 2.0.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Arithmetic {

    /**
     * The left operand of the arithmetic expression.
     * Must be a column name or a constant value.
     *
     * @return the left operand
     */
    String left();

    /**
     * Optional source (such as a table alias or entity name) for the left operand.
     * Useful in cases where multiple sources are joined in the query and disambiguation is needed.
     *
     * @return the source alias for the left operand, or empty string if not specified
     */
    String leftSource() default "";

    /**
     * The right operand of the arithmetic expression.
     * Must be a column name or a constant value.
     *
     * @return the right operand
     */
    String right();

    /**
     * Optional source (such as a table alias or entity name) for the right operand.
     * Useful in cases where multiple sources are joined in the query and disambiguation is needed.
     *
     * @return the source alias for the right operand, or empty string if not specified
     */
    String rightSource() default "";

    /**
     * The arithmetic operator to apply between the left and right operands.
     *
     * @return the arithmetic operator
     */
    ArithmeticOperator operator();

    /**
     * The alias to assign to the result of this arithmetic expression.
     * This alias is used as the name of the projected field in the final result set.
     *
     * @return the alias of the resulting expression
     */
    String alias();

}
