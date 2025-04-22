package vn.truongngo.lib.dynamicquery.core.expression;

import lombok.Getter;
import vn.truongngo.lib.dynamicquery.core.builder.Visitor;

import java.util.Arrays;
import java.util.List;

/**
 * Represents a function call expression in a dynamic query.
 * <p>
 * This class models expressions such as {@code COUNT(id)}, {@code CONCAT(firstName, ' ', lastName)},
 * or custom SQL functions. It supports multiple input parameters and an optional alias for the result.
 * </p>
 *
 * <blockquote><pre>
 *     // Example usage:
 *     new FunctionExpression("COUNT", "total", new ColumnReferenceExpression(User.class, "id"));
 *     new FunctionExpression("CONCAT", null,
 *         new ColumnReferenceExpression(User.class, "firstName"),
 *         new ConstantExpression(" "),
 *         new ColumnReferenceExpression(User.class, "lastName")
 *     );
 * </pre></blockquote>
 *
 * @author Truong Ngo
 * @version 1.0
 */
@Getter
public class FunctionExpression extends AbstractExpression {

    /** The name of the SQL function (e.g., COUNT, SUM, CONCAT). */
    private final String functionName;


    /** The list of parameters passed to the function. */
    private final List<Expression> parameters;


    /**
     * Constructs a function expression with the given name, alias, and parameters.
     *
     * @param functionName the name of the function to invoke (e.g., COUNT, SUM)
     * @param alias        the alias for the resulting expression, may be {@code null}
     * @param parameters   the parameters (arguments) to the function
     */
    public FunctionExpression(String functionName, String alias, Expression... parameters) {
        super(alias);
        this.functionName = functionName;
        this.parameters = Arrays.asList(parameters);
    }


    /**
     * Constructs a function expression with the given name and parameters (no alias).
     *
     * @param functionName the name of the function to invoke (e.g., COUNT, SUM)
     * @param parameters   the parameters (arguments) to the function
     */
    public FunctionExpression(String functionName, Expression... parameters) {
        this(functionName, null, parameters);
    }


    /**
     * Accepts a visitor to process this expression.
     *
     * @param visitor the visitor to accept
     * @param context the additional context for the visitor
     * @param <R>     the return type of the visitor
     * @param <C>     the type of the visitor context
     * @return the result of the visitor operation
     */
    @Override
    public <R, C> R accept(Visitor<R, C> visitor, C context) {
        return visitor.visit(this, context);
    }
}


