package vn.truongngo.lib.dynamicquery.core.expression;

import lombok.Getter;
import vn.truongngo.lib.dynamicquery.core.builder.v2.Visitor;

/**
 * Represents a set operation (e.g., UNION, INTERSECT, EXCEPT, UNION ALL) between two or more subqueries.
 * This expression is used to combine the results of multiple queries into a single result set.
 * The set operation will merge, intersect, or exclude results based on the operation type.
 *
 * <p>This expression typically involves two or more subqueries with the same structure (same number and type of columns).
 * The set operation then performs one of the following actions on the subquery results:
 * <ul>
 *   <li>{@code UNION}: Combines results from multiple queries, removing duplicates.</li>
 *   <li>{@code INTERSECT}: Returns only the common results between multiple queries.</li>
 *   <li>{@code EXCEPT}: Returns results from the first query that do not exist in the subsequent queries.</li>
 *   <li>{@code UNION ALL}: Combines results from multiple queries without removing duplicates.</li>
 * </ul>
 *
 * @version 2.0.0
 * @author  Truong Ngo
 */
@Getter
public class SetOperationExpression extends AbstractAlias<SetOperationExpression> implements QuerySource {

    private final SetOperator operator;          // The set operator (UNION, INTERSECT, EXCEPT, UNION ALL)
    private final SubqueryExpression leftQuery;  // The left side subquery
    private final SubqueryExpression rightQuery; // The right side subquery

    /**
     * Constructs a SetOperationExpression with the specified operator and queries.
     *
     * @param operator the set operation type (e.g., {@link SetOperator#UNION}, {@link SetOperator#INTERSECT}, etc.)
     * @param leftQuery the left side subquery to participate in the set operation
     * @param rightQuery the right side subquery to participate in the set operation
     */
    public SetOperationExpression(SetOperator operator, SubqueryExpression leftQuery, SubqueryExpression rightQuery) {
        this.operator = operator;
        this.leftQuery = leftQuery;
        this.rightQuery = rightQuery;
    }

    /**
     * Accepts a visitor to perform a specific operation on this set operation expression.
     * This method is part of the Visitor design pattern and is used to dispatch
     * operations based on the type of the expression.
     *
     * @param visitor the visitor to accept
     * @param context additional context passed to the visitor
     * @param <R> the result type of the visitor's operation
     * @param <C> the type of the additional context
     * @return the result of the visitor's operation on this expression
     */
    @Override
    public <R, C> R accept(Visitor<R, C> visitor, C context) {
        return visitor.visit(this, context);
    }

    /**
     * Enum to represent the set operation types used in set operations such as UNION, INTERSECT, EXCEPT, and UNION ALL.
     * These operations allow combining results from multiple queries, removing duplicates or retaining all results.
     */
    public enum SetOperator {

        /** The UNION operation, which combines results from two queries and removes duplicates. */
        UNION,

        /** The INTERSECT operation, which returns only the common results between two queries. */
        INTERSECT,

        /** The EXCEPT operation, which returns the results from the first query that do not exist in the second query. */
        EXCEPT,

        /** The UNION ALL operation, which combines results from two queries without removing duplicates. */
        UNION_ALL

    }

    /**
     * Creates a new {@link Builder} instance to construct a {@link SetOperationExpression}.
     * <p>
     * This static factory method provides a convenient way to begin building a set operation expression
     * using a fluent API style.
     *
     * @return a new {@code Builder} instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder class for constructing instances of {@link SetOperationExpression}.
     * <p>
     * This builder provides a fluent API to configure a set operation between two queries (e.g., UNION, UNION ALL).
     * You can specify the left and right queries, the set operator, and an optional alias for the resulting expression.
     *
     * <p>Example usage:
     * <blockquote><pre>
     * SetOperationExpression expression = SetOperationExpression.builder()
     *     .left(query1)
     *     .right(query2)
     *     .operator(SetOperator.UNION_ALL)
     *     .alias("mergedResult")
     *     .build();
     * </pre></blockquote>
     */
    public static class Builder {

        private SetOperator operator;
        private SubqueryExpression leftQuery;
        private SubqueryExpression rightQuery;
        private String alias;

        /**
         * Sets the set operator (e.g., UNION, UNION ALL) to be used in the set operation.
         *
         * @param operator the set operator
         * @return the builder instance
         */
        public Builder operator(SetOperator operator) {
            this.operator = operator;
            return this;
        }

        /**
         * Sets the left-hand side query source for the set operation.
         *
         * @param leftQuery the left query
         * @return the builder instance
         */
        public Builder left(SubqueryExpression leftQuery) {
            this.leftQuery = leftQuery;
            return this;
        }

        /**
         * Sets the right-hand side query source for the set operation.
         *
         * @param rightQuery the right query
         * @return the builder instance
         */
        public Builder right(SubqueryExpression rightQuery) {
            this.rightQuery = rightQuery;
            return this;
        }

        /**
         * Sets the alias to be used for the resulting set operation expression.
         *
         * @param alias the alias name
         * @return the builder instance
         */
        public Builder alias(String alias) {
            this.alias = alias;
            return this;
        }

        /**
         * Builds a {@link SetOperationExpression} instance using the configured values.
         *
         * @return a new instance of {@code SetOperationExpression}
         * @throws IllegalStateException if any of the required fields (operator, left, right) are missing
         */
        public SetOperationExpression build() {
            if (operator == null || leftQuery == null || rightQuery == null) {
                throw new IllegalStateException("Operator, leftQuery, and rightQuery must not be null");
            }
            SetOperationExpression sop = new SetOperationExpression(this.operator, leftQuery, rightQuery);
            return (alias != null) ? sop.as(alias) : sop;
        }
    }
}
