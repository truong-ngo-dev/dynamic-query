package vn.truongngo.lib.dynamicquery.core.expression.v2;

/**
 * Represents an {@link Expression} that can be aliased, commonly used in {@code SELECT} and {@code FROM} clauses.
 * <p>
 * An alias is a secondary name that can be assigned to an expression for readability or reference in later parts
 * of the query (e.g., ordering, grouping, or filtering).
 * </p>
 *
 * <p>Example usage:</p>
 * <blockquote><pre>
 * ColumnReferenceExpression column = new ColumnReferenceExpression("name", User.class);
 * Aliasable&lt;Expression&gt; aliasable = column.as("username");
 * String alias = aliasable.getAlias(); // "username"
 * </pre></blockquote>
 *
 * @param <T> the type of expression that supports aliasing
 *
 * @see Expression
 * @see Selection
 * @see QuerySource
 *
 * @author Truong Ngo
 * @version 2.0.0
 */
public interface Aliasable<T extends Expression> {

    /**
     * Returns the alias assigned to this expression, if any.
     *
     * @return the alias string, or {@code null} if no alias is set
     */
    String getAlias();

    /**
     * Assigns an alias to this expression and returns the updated expression.
     *
     * @param alias the alias name to assign
     * @return a new instance or updated reference with the alias applied
     */
    T as(String alias);

    /**
     * Checks whether this expression has an alias assigned.
     *
     * @return {@code true} if an alias is present; {@code false} otherwise
     */
    boolean hasAlias();

}
