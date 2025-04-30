package vn.truongngo.lib.dynamicquery.core.expression;

import lombok.Getter;
import vn.truongngo.lib.dynamicquery.core.builder.v2.Visitor;

import java.sql.PreparedStatement;

/**
 * Represents a constant value in a query expression.
 * <p>
 * This expression can be used in various parts of a query such as
 * <ul>
 *     <li><b>SELECT</b>: {@code SELECT ? AS status}</li>
 *     <li><b>WHERE</b>: {@code WHERE status = ?}</li>
 *     <li><b>VALUES</b>: {@code VALUES (?)} </li>
 *     <li><b>CASE</b>: {@code CASE WHEN is_active THEN ? ELSE ? END}</li>
 * </ul>
 * When used with a SQL builder, the constant value will be treated as a parameter
 * and a placeholder will be used in the SQL string. The actual value will be bound
 * at runtime via the {@link PreparedStatement} or similar.
 * </p>
 *
 * <p>
 * Example usage:
 * <blockquote><pre>
 * new ConstantExpression("active").as("status")
 * </pre></blockquote>
 * </p>
 *
 * @author Truong Ngo
 * @version 2.0.0
 */
@Getter
public class ConstantExpression extends AbstractAlias<ConstantExpression> implements Selection {

    private final Object value;

    /**
     * Constructs a constant expression holding the specified value.
     * The value will be treated as a parameter in the SQL query.
     *
     * @param value the constant value, which will be used as a parameter in the query.
     */
    public ConstantExpression(Object value) {
        this.value = value;
    }


    /**
     * Accepts a visitor to process this expression.
     * The visitor can be used to build SQL query strings or bind parameters.
     *
     * @param visitor the visitor
     * @param context the context object passed along the visit chain
     * @param <R>     the result type of the visitor
     * @param <C>     the context type
     * @return the result from the visitor
     */
    @Override
    public <R, C> R accept(Visitor<R, C> visitor, C context) {
        return visitor.visit(this, context);
    }

}
