package vn.truongngo.lib.dynamicquery.core.expression;

import lombok.Getter;
import vn.truongngo.lib.dynamicquery.core.builder.Visitor;
import vn.truongngo.lib.dynamicquery.core.enumerate.ArithmeticOperator;

/**
 * Represents an arithmetic expression, consisting of a left operand, an operator, and a right operand.
 * This class supports arithmetic operations such as addition, subtraction, multiplication, and division.
 *
 * <p>The expression follows the format: left operator right, e.g., "a + b".</p>
 *
 * @author Truong Ngo
 * @version 2.0.0
 */
@Getter
public class ArithmeticExpression extends AbstractAlias<ArithmeticExpression> implements Selection {

    /**
     * The left operand of the arithmetic expression (e.g., "a" in "a + b").
     */
    private final Selection left;

    /**
     * The arithmetic operator applied between the operands (e.g., {@link ArithmeticOperator#ADD} for addition).
     */
    private final ArithmeticOperator operator;

    /**
     * The right operand of the arithmetic expression (e.g., "b" in "a + b").
     */
    private final Selection right;

    /**
     * Constructs an {@link ArithmeticExpression} with the given operands and operator.
     *
     * @param left the left operand (e.g., "a" in "a + b")
     * @param operator the arithmetic operator (e.g., {@link ArithmeticOperator#ADD} for addition)
     * @param right the right operand (e.g., "b" in "a + b")
     */
    public ArithmeticExpression(Selection left, ArithmeticOperator operator, Selection right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    /**
     * Accepts a visitor that performs specific operations on this {@link ArithmeticExpression}.
     *
     * @param visitor the visitor that will process this expression
     * @param context the context passed to the visitor
     * @param <R> the return type of the visitor's operations
     * @param <C> the type of the context object
     * @return the result of the visitor's operation
     */
    @Override
    public <R, C> R accept(Visitor<R, C> visitor, C context) {
        return visitor.visit(this, context);
    }

    /**
     * Returns a new instance of the {@link Builder} class for constructing an {@link ArithmeticExpression}.
     * <p>
     * This static method provides a fluent interface to build an {@link ArithmeticExpression} by using the
     * builder pattern. It allows users to easily create and configure an instance of {@link ArithmeticExpression}.
     * </p>
     *
     * @return a new instance of the Builder class to construct an {@link ArithmeticExpression}
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder class for constructing an {@link ArithmeticExpression}.
     * <p>
     * This builder allows the fluent construction of an {@link ArithmeticExpression} with left operand,
     * operator, and right operand.
     * </p>
     */
    public static class Builder {

        private Selection left;
        private ArithmeticOperator operator;
        private Selection right;

        /**
         * Sets the left operand for the arithmetic expression.
         *
         * @param left the left operand
         * @return the current builder instance
         */
        public Builder left(Selection left) {
            this.left = left;
            return this;
        }

        /**
         * Sets the operator for the arithmetic expression.
         *
         * @param operator the arithmetic operator
         * @return the current builder instance
         */
        public Builder operator(ArithmeticOperator operator) {
            this.operator = operator;
            return this;
        }

        /**
         * Sets the right operand for the arithmetic expression.
         *
         * @param right the right operand
         * @return the current builder instance
         */
        public Builder right(Selection right) {
            this.right = right;
            return this;
        }

        /**
         * Builds the {@link ArithmeticExpression} with the set left operand, operator, and right operand.
         * Throws an {@link IllegalArgumentException} if any required fields are missing.
         *
         * @return a new {@link ArithmeticExpression}
         * @throws IllegalArgumentException if any required field is missing
         */
        public ArithmeticExpression build() {
            if (left == null || operator == null || right == null) {
                throw new IllegalArgumentException("Left, operator, and right must be provided.");
            }
            return new ArithmeticExpression(this.left, this.operator, this.right);
        }
    }
}
