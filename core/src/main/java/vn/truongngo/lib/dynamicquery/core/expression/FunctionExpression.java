package vn.truongngo.lib.dynamicquery.core.expression;

import lombok.Getter;
import lombok.Setter;
import vn.truongngo.lib.dynamicquery.core.builder.Visitor;

import java.util.*;

/**
 * Represents a function expression in a query.
 * <p>
 * This expression is used to represent SQL functions (e.g., SUM, AVG, COUNT, etc.) and can include various options
 * like distinct values, metadata for special behavior, and different types of functions (standard, special, or DB-specific).
 * </p>
 *
 * <p>
 * Example usage:
 * <blockquote><pre>
 * new FunctionExpression("SUM", Arrays.asList(new ColumnReferenceExpression(table, "amount")), false).as("total_amount")
 * </pre></blockquote>
 * </p>
 *
 * <p>
 * The distinct flag can be used for functions like {@code COUNT(DISTINCT ...)} and {@code SUM(DISTINCT ...)}.
 * </p>
 *
 * <p>
 * The metadata field is reserved for special behavior or additional information about the function. It could be used
 * to specify database-specific hints or other properties that modify the function's behavior.
 * </p>
 *
 * @author Truong Ngo
 * @version 2.0.0
 */
@Getter
public class FunctionExpression extends AbstractAlias<FunctionExpression> implements Selection {

    /**
     * The name of the function (e.g., "SUM", "AVG", "COUNT").
     */
    private final String functionName;

    /**
     * The list of parameters to be passed to the function.
     */
    private final List<Selection> parameters;

    /**
     * If true, the function will operate on distinct values (e.g., {@code COUNT(DISTINCT ...)}, {@code SUM(DISTINCT ...)}).
     */
    private final boolean distinct;

    /**
     * Metadata for special behavior or additional information about the function.
     * <p>
     * This field can store arbitrary data like database-specific hints or custom configurations
     * to modify how the function is executed or interpreted.
     * </p>
     */
    private final Map<String, Object> metadata;

    /**
     * The type of the function (e.g., standard SQL functions, special functions, or database-specific ones).
     */
    private final Type type;


    /**
     * Constructs a function expression.
     *
     * @param functionName the name of the function
     * @param parameters   the parameters for the function
     * @param distinct     whether the function should operate on distinct values
     * @param metadata     any metadata associated with the function (e.g., database-specific behavior)
     * @param type         the type of function (standard, special, or DB-specific)
     */
    public FunctionExpression(String functionName, List<Selection> parameters, boolean distinct,
                              Map<String, Object> metadata, Type type) {
        this.functionName = functionName;
        this.parameters = parameters;
        this.distinct = distinct;
        this.metadata = metadata;
        this.type = type;
    }


    /**
     * Accepts a visitor to process this expression.
     * This method allows the expression to be visited by different types of visitors (e.g., SQL builder, parameter binder).
     *
     * @param visitor the visitor to accept
     * @param context the context to pass along to the visitor
     * @param <R>     the result type of the visitor
     * @param <C>     the context type
     * @return the result of visiting this expression
     */
    @Override
    public <R, C> R accept(Visitor<R, C> visitor, C context) {
        return visitor.visit(this, context);
    }


    /**
     * Creates a new {@link Builder} instance for constructing a {@link FunctionExpression}
     * using a fluent API style.
     *
     * <p>
     * This method provides a convenient way to build a {@code FunctionExpression} by chaining
     * configuration methods, such as setting the function name, parameters, options, and type.
     * </p>
     *
     * <pre>{@code
     * FunctionExpression expression = FunctionExpression.builder()
     *     .name("CAST")
     *     .parameter(column("price"))
     *     .option("AS", "VARCHAR")
     *     .build();
     * }</pre>
     *
     * @return a new {@link Builder} instance
     */
    public static Builder builder() {
        return new Builder();
    }


    /**
     * Enum representing different types of functions.
     */
    public enum Type {

        /**
         * Standard SQL functions (e.g., COUNT, SUM, AVG).
         */
        STANDARD,

        /**
         * Special functions, often database-specific or custom-defined.
         */
        SPECIAL,

        /**
         * Database-specific functions (e.g., Oracle's NVL, MySQL's GROUP_CONCAT).
         */
        DB_SPECIFIC

    }

    /**
     * Builder class for constructing {@link FunctionExpression} instances
     * using a fluent API.
     *
     * <p>This builder supports setting the function name, parameters, whether the function
     * should apply the {@code DISTINCT} modifier, optional keyword-based arguments (such as
     * {@code AS} in {@code CAST(... AS VARCHAR)}), and the function type.</p>
     *
     */
    @Setter
    public static class Builder {

        private String functionName;
        private final List<Selection> parameters = new ArrayList<>();
        private boolean distinct = false;
        private final Map<String, Object> metadata = new LinkedHashMap<>();
        private Type type = Type.STANDARD;

        /**
         * Sets the name of the SQL function.
         *
         * @param name the name of the function (e.g., {@code "CAST"}, {@code "SUM"})
         * @return this builder instance
         */
        public Builder name(String name) {
            this.functionName = name;
            return this;
        }


        /**
         * Adds the given parameters (arguments) to the function.
         *
         * @param parameters one or more {@link Selection} expressions
         * @return this builder instance
         */
        public Builder parameters(Selection... parameters) {
            this.parameters.addAll(Arrays.asList(parameters));
            return this;
        }

        /**
         * Adds the given parameters (arguments) to the function.
         *
         * @param parameters one or more {@link Selection} expressions
         * @return this builder instance
         */
        public Builder parameters(List<Selection> parameters) {
            this.parameters.addAll(parameters);
            return this;
        }


        /**
         * Marks the function as applying the {@code DISTINCT} modifier.
         *
         * @param distinct {@code true} if {@code DISTINCT} should be applied
         * @return this builder instance
         */
        public Builder distinct(boolean distinct) {
            this.distinct = distinct;
            return this;
        }


        /**
         * Adds a keyword-based option to the function, useful for special SQL functions.
         *
         * <p>For example, for {@code CAST(price AS VARCHAR)}, use:</p>
         * <pre>{@code
         * .option("AS", "VARCHAR")
         * }</pre>
         *
         * @param name  the option name (e.g., {@code "AS"}, {@code "FROM"})
         * @param value the corresponding value
         * @return this builder instance
         */
        public Builder option(String name, Object value) {
            this.metadata.put(name, value);
            return this;
        }


        /**
         * Sets the function type, used for internal rendering or dialect-specific logic.
         *
         * @param type the {@link FunctionExpression.Type} of the function
         * @return this builder instance
         */
        public Builder type(Type type) {
            this.type = type;
            return this;
        }


        /**
         * Builds the configured {@link FunctionExpression} instance.
         *
         * @return a new {@code FunctionExpression} object
         */
        public FunctionExpression build() {
            return new FunctionExpression(functionName, parameters, distinct, metadata, type);
        }
    }
}
