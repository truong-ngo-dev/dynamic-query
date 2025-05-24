package vn.truongngo.lib.dynamicquery.projection.descriptor;

import java.lang.reflect.Field;

/**
 * Represents a component that can be selected in the SELECT clause of a query.
 * <p>
 * Implementations of this interface provide metadata for columns, expressions, or computed values
 * that are projected in the result set.
 * </p>
 *
 * <p>
 * Common implementations include:
 * <ul>
 *     <li>{@link ColumnDescriptor} - for direct column references</li>
 *     <li>{@link AggregateDescriptor} - for aggregate functions such as {@code SUM}, {@code COUNT}</li>
 *     <li>{@link ArithmeticDescriptor} - for arithmetic expressions like {@code price * quantity}</li>
 *     <li>{@link ExpressionDescriptor} - for raw expression strings (e.g. SQL fragments)</li>
 *     <li>{@link SubqueryDescriptor} - for scalar subqueries used as select items</li>
 * </ul>
 * </p>
 *
 * <p>
 * The alias returned by {@link #getAlias()} should uniquely identify the selection and is used
 * for referencing in clauses such as {@code ORDER BY} or {@code GROUP BY}.
 * </p>
 *
 * @author Truong Ngo
 * @version 2.0.0
 */
public interface SelectDescriptor {

    /**
     * Returns the alias of the selected element in the query (name of projection field).
     * This alias is used for referencing the selected value in other parts of the query.
     *
     * @return the alias of the selected column or expression
     */
    String getAlias();

    /**
     * Returns the index of the selection in the projection class declaration.
     * This helps preserve the original order in the final SELECT clause.
     *
     * @return the zero-based index position
     */
    Integer getIndex();

    /**
     * Returns the {@link Field} instance representing the projection field associated
     * with this selection.
     * <p>
     * This allows access to the underlying Java reflection metadata for the field,
     * including name, type, annotations, and value retrieval from an instance of
     * the projection class.
     * </p>
     * <p>
     * Implementations may return {@code null} if the selection does not directly map
     * to a physical field (e.g. computed expressions or raw SQL fragments).
     * </p>
     *
     * @return the reflection {@code Field} object for this selection's projection field,
     *         or {@code null} if not applicable
     */
    Field getField();

    /**
     * Returns the identifier used to reference this selection in clauses like ORDER BY or GROUP BY.
     * <p>
     * By default, this method returns the alias, but implementations may override it to provide
     * a fallback value (e.g., the original column name) when alias is not present.
     * This ensures that referencing always resolves correctly during query building.
     * </p>
     *
     * @return the reference name used for ordering or grouping, typically the alias or a fallback
     */
    default String getReference() {
        return getAlias();
    }

}
