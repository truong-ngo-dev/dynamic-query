package vn.truongngo.lib.dynamicquery.core.expression;

import lombok.Getter;
import vn.truongngo.lib.dynamicquery.core.builder.Visitor;

import java.util.List;

/**
 * Represents a Common Table Expression (CTE) in a query.
 * <p>
 * A CTE allows you to define a temporary named result set that can be referenced
 * within a SELECT, INSERT, UPDATE, or DELETE statement. It can also be recursive.
 * </p>
 *
 * <p>
 * This class holds:
 * <ul>
 *     <li>{@code name} - the name of the CTE</li>
 *     <li>{@code columns} - optional column names exposed by the CTE</li>
 *     <li>{@code subquery} - the underlying subquery the CTE wraps</li>
 *     <li>{@code recursive} - whether the CTE is recursive</li>
 * </ul>
 * </p>
 *
 * @author Truong Ngo
 * @version 2.0.0
 */
@Getter
public class CommonTableExpression extends AbstractAlias<CommonTableExpression> implements QuerySource {

    private final String name;
    private final List<String> columns;
    private final SubqueryExpression subquery;
    private final boolean recursive;

    /**
     * Constructs a CommonTableExpression with the specified parameters.
     *
     * @param name      the name of the CTE
     * @param columns   the list of column names exposed by the CTE; can be null or empty
     * @param subquery  the subquery representing the body of the CTE
     * @param recursive whether the CTE is recursive
     */
    public CommonTableExpression(String name, List<String> columns, SubqueryExpression subquery, boolean recursive) {
        this.name = name;
        this.columns = columns;
        this.subquery = subquery;
        this.recursive = recursive;
    }

    /**
     * Accepts a visitor to perform an operation on this CTE expression.
     *
     * @param visitor the visitor
     * @param context additional context
     * @param <R>     the result type
     * @param <C>     the context type
     * @return the result of the visitor operation
     */
    @Override
    public <R, C> R accept(Visitor<R, C> visitor, C context) {
        return visitor.visit(this, context);
    }

    /**
     * Returns a builder to construct a {@link CommonTableExpression}.
     *
     * @return a new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for {@link CommonTableExpression}.
     * <p>
     * This builder helps to fluently construct a {@link CommonTableExpression}
     * by setting its name, columns, subquery, and whether it is recursive. Example usage:
     * <blockquote>
     * <pre>
     * CommonTableExpression cte = CommonTableExpression.builder()
     *         .name("cte_name")
     *         .columns(List.of("col1", "col2"))
     *         .subquery(new SubqueryExpression(queryMetadata))
     *         .recursive(true)
     *         .build();
     * </pre>
     * </blockquote>
     */
    public static class Builder {

        private String name;
        private List<String> columns;
        private SubqueryExpression subquery;
        private boolean recursive = false;

        /**
         * Sets the name of the CTE.
         *
         * @param name the CTE name
         * @return this builder instance
         */
        public Builder with(String name) {
            this.name = name;
            return this;
        }

        /**
         * Sets the column names exposed by the CTE.
         *
         * @param columns the list of column names
         * @return this builder instance
         */
        public Builder columns(List<String> columns) {
            this.columns = columns;
            return this;
        }

        /**
         * Sets the subquery that defines the body of the CTE.
         *
         * @param subquery the subquery expression
         * @return this builder instance
         */
        public Builder subquery(SubqueryExpression subquery) {
            this.subquery = subquery;
            return this;
        }

        /**
         * Marks the CTE as recursive or not.
         *
         * @param recursive true if recursive, false otherwise
         * @return this builder instance
         */
        public Builder recursive(boolean recursive) {
            this.recursive = recursive;
            return this;
        }

        /**
         * Builds the {@link CommonTableExpression}.
         *
         * @return the constructed CTE instance
         * @throws IllegalArgumentException if name or subquery is null
         */
        public CommonTableExpression build() {
            if (name == null || subquery == null) {
                throw new IllegalArgumentException("Both name and subquery must be provided.");
            }
            return new CommonTableExpression(name, columns, subquery, recursive);
        }
    }
}
