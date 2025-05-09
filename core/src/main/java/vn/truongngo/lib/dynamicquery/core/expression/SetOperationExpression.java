package vn.truongngo.lib.dynamicquery.core.expression;

import lombok.Getter;
import vn.truongngo.lib.dynamicquery.core.builder.Visitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents a set operation (e.g., UNION, INTERSECT, EXCEPT, UNION ALL) between multiple subqueries.
 * This expression combines the results of multiple subqueries into a single result set based on the specified operation.
 *
 * <p>All subqueries must return the same structure (same number and compatible types of columns) for the set operation to be valid.</p>
 *
 * <p>The supported operations include:
 * <ul>
 *   <li>{@code UNION}: Combines results from all subqueries, removing duplicates.</li>
 *   <li>{@code UNION ALL}: Combines results from all subqueries, retaining duplicates.</li>
 *   <li>{@code INTERSECT}: Returns only rows that are common to all subqueries.</li>
 *   <li>{@code EXCEPT}: Returns rows from the first subquery that are not found in any subsequent subqueries.</li>
 * </ul>
 *
 * <p>This class implements the {@link QuerySource} interface and supports the Visitor pattern
 * to allow transformation or interpretation depending on the query backend.</p>
 *
 * @author Truong Ngo
 * @version 2.0.0
 */
@Getter
public class SetOperationExpression extends AbstractAlias<SetOperationExpression> implements QuerySource {

    private final SetOperator operator;
    private final List<QuerySource> queries;


    /**
     * Constructs a new SetOperationExpression with the given operator and subqueries.
     *
     * @param operator the type of set operation to perform
     * @param queries  the list of subqueries to participate in the set operation; must contain at least two subqueries
     * @throws IllegalArgumentException if {@code queries} is null or has fewer than two elements
     */
    public SetOperationExpression(SetOperator operator, List<QuerySource> queries) {
        if (queries == null || queries.size() < 2) {
            throw new IllegalArgumentException("At least two subqueries are required for set operation");
        }
        this.operator = operator;
        this.queries = List.copyOf(queries);
    }


    /**
     * Accepts a visitor to perform an operation on this expression.
     *
     * @param visitor the visitor to accept
     * @param context the context to pass to the visitor
     * @param <R>     the return type of the visitor
     * @param <C>     the type of the context object
     * @return the result of the visitor's operation
     */
    @Override
    public <R, C> R accept(Visitor<R, C> visitor, C context) {
        return visitor.visit(this, context);
    }


    /**
     * Enum to represent supported SQL set operations.
     */
    public enum SetOperator {
        /** UNION: Removes duplicates between subquery results. */
        UNION,

        /** INTERSECT: Returns only the common rows between subquery results. */
        INTERSECT,

        /** EXCEPT: Returns rows from the first subquery that are not present in others. */
        EXCEPT,

        /** UNION ALL: Combines all rows from subqueries including duplicates. */
        UNION_ALL
    }


    /**
     * Creates a new builder instance for {@link SetOperationExpression}.
     *
     * @return a new {@code Builder}
     */
    public static Builder builder() {
        return new Builder();
    }


    /**
     * Builder class to construct {@link SetOperationExpression} instances using a fluent API.
     *
     * <p>Example usage:</p>
     * <blockquote><pre>
     * SetOperationExpression expression = SetOperationExpression.builder()
     *     .operator(SetOperator.UNION_ALL)
     *     .addQuery(query1)
     *     .addQuery(query2)
     *     .alias("merged")
     *     .build();
     * </pre></blockquote>
     */
    public static class Builder {

        private SetOperator operator;
        private final List<QuerySource> queries = new ArrayList<>();
        private String alias;

        /**
         * Sets the set operation type.
         *
         * @param operator the operation type (e.g., UNION, INTERSECT)
         * @return this builder
         */
        public Builder operator(SetOperator operator) {
            this.operator = operator;
            return this;
        }

        /**
         * Adds one or more subqueries to the set operation.
         *
         * @param query one or more subqueries
         * @return this builder
         */
        public Builder addQuery(QuerySource... query) {
            this.queries.addAll(List.of(query));
            return this;
        }

        /**
         * Adds a collection of subqueries to the set operation.
         *
         * @param queries the subqueries to add
         * @return this builder
         */
        public Builder addQueries(Collection<QuerySource> queries) {
            this.queries.addAll(queries);
            return this;
        }

        /**
         * Sets the alias for the resulting expression.
         *
         * @param alias the alias name
         * @return this builder
         */
        public Builder alias(String alias) {
            this.alias = alias;
            return this;
        }

        /**
         * Builds the {@link SetOperationExpression} instance.
         *
         * @return a new {@code SetOperationExpression}
         * @throws IllegalStateException if the operator is null or less than 2 subqueries are provided
         */
        public SetOperationExpression build() {
            if (operator == null || queries.size() < 2) {
                throw new IllegalStateException("Operator must not be null and at least 2 queries are required");
            }
            SetOperationExpression sop = new SetOperationExpression(operator, queries);
            return (alias != null) ? sop.as(alias) : sop;
        }
    }
}
