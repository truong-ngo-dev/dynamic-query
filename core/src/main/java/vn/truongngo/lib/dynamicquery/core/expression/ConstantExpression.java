package vn.truongngo.lib.dynamicquery.core.expression;

import lombok.Getter;
import vn.truongngo.lib.dynamicquery.core.builder.Visitor;

/**
 * Represents a constant (literal) value in a query expression.
 * <p>
 * A {@code ConstantExpression} is used to embed fixed values into queries,
 * such as numbers, strings, or booleans. It does not refer to any entity or column.
 *
 * <p>Example usage:
 * <blockquote><pre>
 * Expression expr = new ConstantExpression(42);
 * // Represents a constant value of 42 in the query
 * </pre></blockquote>
 *
 * @see Expression
 * @see Visitor
 * @author Truong Ngo
 * @version 1.0
 */
@Getter
public class ConstantExpression extends AbstractExpression {

    /**
     * The constant value represented by this expression.
     */
    private final Object value;


    /**
     * Constructs a new constant expression.
     *
     * @param value the literal value (e.g., String, Integer, Boolean)
     */
    public ConstantExpression(final Object value) {
        super(null);
        this.value = value;
    }


    /**
     * Accepts a visitor to process this constant expression.
     *
     * @param visitor the visitor instance
     * @param context the context for the visitor
     * @param <R>     the return type of the visitor
     * @param <C>     the type of the context
     * @return the result from the visitor
     */
    @Override
    public <R, C> R accept(Visitor<R, C> visitor, C context) {
        return visitor.visit(this, context);
    }

}
