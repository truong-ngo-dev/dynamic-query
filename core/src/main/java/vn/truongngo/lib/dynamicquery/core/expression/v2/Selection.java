package vn.truongngo.lib.dynamicquery.core.expression.v2;

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
    // Marker interface
}
