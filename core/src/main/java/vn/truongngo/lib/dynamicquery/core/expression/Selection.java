package vn.truongngo.lib.dynamicquery.core.expression;

/**
 * Represents a selectable expression in a query, typically used in the {@code SELECT} clause.
 * <p>
 * This interface extends {@link Expression} to indicate that any selection is also an expression,
 * and can be evaluated or transformed as part of a dynamic query construction.
 * </p>
 *
 * <p>Example usage:</p>
 * <blockquote><pre>
 * Selection selection = new ColumnReferenceExpression("name", User.class);
 * </pre></blockquote>
 *
 * <p>Implementations of this interface may include column references, aggregate functions,
 * case expressions, subqueries, or any other valid query element that can appear in the select list.</p>
 *
 * @author Truong Ngo
 * @version 2.0.0
 */
public interface Selection extends Expression {

    /**
     * Assigns an alias to this expression and returns the updated expression.
     *
     * @param alias the alias name to assign
     * @return a new instance or updated reference with the alias applied
     */
    Selection as(String alias);

    /**
     * Returns the alias assigned to this expression, if any.
     *
     * @return the alias string, or {@code null} if no alias is set
     */
    String getAlias();

}
