package vn.truongngo.lib.dynamicquery.core.expression.v2;

import lombok.Getter;
import vn.truongngo.lib.dynamicquery.core.builder.v2.Visitor;
import vn.truongngo.lib.dynamicquery.core.enumerate.Operator;

/**
 * ComparisonPredicate represents a comparison between two expressions using an operator.
 * This predicate can be used to express conditions like equality, inequality, greater than, etc.
 *
 * <p>The predicate holds two expressions (left and right) and an operator that determines how
 * these expressions should be compared. It is part of the query building process where conditions
 * need to be applied to the query results.</p>
 *
 * @version 2.0.0
 * @author Truong Ngo
 */
@Getter
public class ComparisonPredicate extends AbstractPredicate {

    private final Selection left;
    private final Operator operator;
    private final Selection right;

    /**
     * Constructs a ComparisonPredicate with the specified left expression, operator, and right expression.
     *
     * @param left the left side expression of the comparison
     * @param operator the operator used for the comparison (e.g., {@link Operator#EQUAL}, {@link Operator#GREATER_THAN}, etc.)
     * @param right the right side expression of the comparison
     */
    public ComparisonPredicate(Selection left, Operator operator, Selection right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    /**
     * Accepts a visitor to perform a specific operation on this predicate.
     * This method is part of the Visitor design pattern and is used to dispatch
     * operations based on the type of the predicate.
     *
     * @param visitor the visitor to accept
     * @param context additional context passed to the visitor
     * @param <R> the result type of the visitor's operation
     * @param <C> the type of the additional context
     * @return the result of the visitor's operation on this predicate
     */
    @Override
    public <R, C> R accept(Visitor<R, C> visitor, C context) {
        return visitor.visit(this, context);
    }

    /**
     * Builder class for constructing a {@link ComparisonPredicate}.
     */
    public static class Builder {

        private Selection left;
        private Operator operator;
        private Selection right;

        /**
         * Sets the left expression for the comparison.
         *
         * @param left the left side expression of the comparison
         * @return this builder instance for chaining
         */
        public Builder left(Selection left) {
            this.left = left;
            return this;
        }

        /**
         * Sets the operator to be used in the comparison (e.g., {@link Operator#EQUAL}, {@link Operator#GREATER_THAN}, etc.).
         *
         * @param operator the operator for the comparison
         * @return this builder instance for chaining
         */
        public Builder operator(Operator operator) {
            this.operator = operator;
            return this;
        }

        /**
         * Sets the right expression for the comparison.
         *
         * @param right the right side expression of the comparison
         * @return this builder instance for chaining
         */
        public Builder right(Selection right) {
            this.right = right;
            return this;
        }

        /**
         * Builds the {@link ComparisonPredicate} instance.
         * <p>If any of the required properties (left, operator, right) are missing, an {@link IllegalArgumentException}
         * will be thrown.</p>
         *
         * @return a new {@link ComparisonPredicate} instance
         * @throws IllegalArgumentException if left, operator, or right are not provided
         */
        public ComparisonPredicate build() {
            if (left == null || operator == null || right == null) {
                throw new IllegalArgumentException("Left, operator, and right must be provided.");
            }
            return new ComparisonPredicate(this.left, this.operator, this.right);
        }
    }

    /**
     * Returns a new instance of the {@link Builder} for {@link ComparisonPredicate}.
     * <p>This provides a convenient way to construct a {@link ComparisonPredicate} with a fluent API.</p>
     *
     * @return a new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

}
