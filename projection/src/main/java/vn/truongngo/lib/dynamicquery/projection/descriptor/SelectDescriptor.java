package vn.truongngo.lib.dynamicquery.projection.descriptor;

/**
 * Represents a component that can be selected in the SELECT clause of a query.
 * <p>
 * Implementations of this interface provide metadata for columns, expressions, or computed values
 * that are projected in the result set.
 * </p>
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
     * Returns the alias of the selected element in the query.
     * This alias is used for referencing the selected value in other parts of the query.
     *
     * @return the alias of the selected column or expression
     */
    String getAlias();

}
