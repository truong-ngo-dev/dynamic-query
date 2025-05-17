package vn.truongngo.lib.dynamicquery.core.expression;

import lombok.Getter;
import vn.truongngo.lib.dynamicquery.core.builder.Visitor;
import vn.truongngo.lib.dynamicquery.core.expression.modifier.OrderExpression;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a SQL window function expression with optional arguments, partitioning, ordering, and aliasing.
 * <p>
 * This class supports fluent construction via the {@link Builder}.
 *
 * <p><strong>Example usage:</strong></p>
 * <blockquote><pre>
 * WindowFunctionExpression rowNumber = WindowFunctionExpression.builder()
 *     .name("ROW_NUMBER")
 *     .partitionBy(column("department_id", Employee.class))
 *     .orderBy(asc(column("salary", Employee.class)))
 *     .as("row_num")
 *     .build();
 * </pre></blockquote>
 *
 * This produces an expression like:
 * <pre>{@code
 * ROW_NUMBER() OVER (PARTITION BY department_id ORDER BY salary ASC) AS row_num
 * }</pre>
 *
 * @author Truong Ngo
 * @version 2.0.0
 */
@Getter
public class WindowFunctionExpression extends AbstractAlias<WindowFunctionExpression> implements Selection {

    private final String functionName;
    private final List<Selection> arguments;
    private final List<Selection> partitionBy;
    private final List<OrderExpression> orderBy;

    /**
     * Constructs a window function expression.
     *
     * @param functionName the name of the SQL window function (e.g., "ROW_NUMBER", "RANK", "SUM")
     * @param arguments the arguments passed to the function
     * @param partitionBy the partition-by expressions
     * @param orderBy the order-by expressions
     */
    public WindowFunctionExpression(String functionName, List<Selection> arguments, List<Selection> partitionBy, List<OrderExpression> orderBy) {
        this.functionName = functionName;
        this.arguments = arguments;
        this.partitionBy = partitionBy;
        this.orderBy = orderBy;
    }

    /**
     * Accepts a visitor to process or transform this expression.
     *
     * @param visitor the visitor instance
     * @param context the context passed to the visitor
     * @return result of the visitor operation
     * @param <R> return type
     * @param <C> context type
     */
    @Override
    public <R, C> R accept(Visitor<R, C> visitor, C context) {
        return visitor.visit(this, context);
    }

    /**
     * Returns a builder for {@link WindowFunctionExpression}.
     *
     * @return the builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder class for {@link WindowFunctionExpression}.
     */
    public static class Builder {
        private String functionName;
        private final List<Selection> arguments = new ArrayList<>();
        private final List<Selection> partitionBy = new ArrayList<>();
        private final List<OrderExpression> orderBy = new ArrayList<>();
        private String as;

        /**
         * Sets the window function name.
         *
         * @param functionName the function name (e.g., "RANK", "SUM")
         * @return this builder
         */
        public Builder name(String functionName) {
            this.functionName = functionName;
            return this;
        }

        /**
         * Adds a single argument to the function.
         *
         * @param argument the argument
         * @return this builder
         */
        public Builder argument(Selection argument) {
            this.arguments.add(argument);
            return this;
        }

        /**
         * Adds multiple arguments to the function.
         *
         * @param arguments the list of arguments
         * @return this builder
         */
        public Builder arguments(List<Selection> arguments) {
            this.arguments.addAll(arguments);
            return this;
        }

        /**
         * Adds a single partition-by expression.
         *
         * @param expression the expression
         * @return this builder
         */
        public Builder partitionBy(Selection expression) {
            this.partitionBy.add(expression);
            return this;
        }

        /**
         * Adds multiple partition-by expressions.
         *
         * @param expressions the list of expressions
         * @return this builder
         */
        public Builder partitionBy(List<Selection> expressions) {
            this.partitionBy.addAll(expressions);
            return this;
        }

        /**
         * Adds one or more order-by expressions.
         *
         * @param orderExpression the order expressions
         * @return this builder
         */
        public Builder orderBy(OrderExpression... orderExpression) {
            this.orderBy.addAll(List.of(orderExpression));
            return this;
        }

        /**
         * Adds multiple order-by expressions.
         *
         * @param orderExpressions the list of order expressions
         * @return this builder
         */
        public Builder orderBy(List<OrderExpression> orderExpressions) {
            this.orderBy.addAll(orderExpressions);
            return this;
        }

        /**
         * Sets an alias for the window function expression.
         *
         * @param as the alias name
         * @return this builder
         */
        public Builder as(String as) {
            this.as = as;
            return this;
        }

        /**
         * Builds and returns the {@link WindowFunctionExpression} instance.
         *
         * @return the built instance
         */
        public WindowFunctionExpression build() {
            WindowFunctionExpression wdf = new WindowFunctionExpression(functionName, arguments, partitionBy, orderBy);
            if (as != null) wdf = wdf.as(as);
            return wdf;
        }
    }
}
