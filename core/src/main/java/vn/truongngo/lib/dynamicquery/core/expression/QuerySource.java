package vn.truongngo.lib.dynamicquery.core.expression;

/**
 * Represents a source of data in a query, typically used in the {@code FROM} clause.
 * <p>
 * A {@code QuerySource} is a type of {@link Expression} that provides rows of data to the query.
 * It may represent a table, a subquery, a join, or other data-producing constructs.
 * </p>
 *
 * <p>Example usage:</p>
 * <blockquote><pre>
 * QuerySource source = new EntityReference(User.class);
 * </pre></blockquote>
 *
 * <p>Implementations may include:</p>
 * <ul>
 *     <li>Table references</li>
 *     <li>Aliased subqueries</li>
 *     <li>Joined sources (INNER, LEFT, etc.)</li>
 * </ul>
 *
 * @author Truong Ngo
 * @version 2.0.0
 */
public interface QuerySource extends Expression {
    // Marker interface
}
