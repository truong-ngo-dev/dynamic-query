package vn.truongngo.lib.dynamicquery.projection.descriptor;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import vn.truongngo.lib.dynamicquery.core.enumerate.ArithmeticOperator;

/**
 * Descriptor representing an arithmetic expression in a query projection.
 * <p>
 * This class models an arithmetic operation (such as addition, subtraction, multiplication, or division)
 * between two columns or expressions, to be included in the SELECT clause.
 * </p>
 *
 * <p>
 * The {@code operator} defines the arithmetic operation to apply.
 * The {@code left} and {@code right} fields represent the operands of the operation.
 * The {@code alias} specifies how the result of the arithmetic expression should be referenced in the result set.
 * </p>
 *
 * <h2>Example</h2>
 * <blockquote><pre>
 * // Represents: (price * quantity) AS totalCost
 * ArithmeticDescriptor descriptor = new ArithmeticDescriptor();
 * descriptor.setOperator(ArithmeticOperator.MULTIPLY);
 * descriptor.setLeft(priceColumnDescriptor);
 * descriptor.setRight(quantityColumnDescriptor);
 * descriptor.setAlias("totalCost");
 * </pre></blockquote>
 *
 * @author Truong Ngo
 * @version 2.0.0
 */
@Getter
@Setter
@SuperBuilder
public class ArithmeticDescriptor extends AbstractSelectDescriptor {

    /**
     * The arithmetic operator to apply (e.g., ADD, SUBTRACT, MULTIPLY, DIVIDE).
     */
    private ArithmeticOperator operator;

    /**
     * The left operand of the arithmetic operation, typically a column or expression.
     */
    private ColumnDescriptor left;

    /**
     * The right operand of the arithmetic operation, typically a column or expression.
     */
    private ColumnDescriptor right;

}
