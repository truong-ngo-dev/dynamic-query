package vn.truongngo.lib.dynamicquery.projection.descriptor;

/**
 * Descriptor representing a raw expression used in the SELECT clause of a query.
 * <p>
 * This descriptor is used when a projection field is annotated with {@code @Expression},
 * allowing you to define arbitrary expressions (e.g., mathematical operations, functions).
 * </p>
 *
 * <h2>Usage example:</h2>
 * <blockquote><pre>
 * {@code @Expression}(value = "price * quantity", alias = "totalPerItem")
 * private BigDecimal total;
 * </pre></blockquote>
 *
 * <p>
 * The {@code expression} field contains the raw string that will later be parsed and converted
 * into a structured query expression. The {@code alias} is the name used to reference this
 * expression in other parts of the query, such as {@code ORDER BY}, {@code GROUP BY}, or {@code HAVING}.
 * </p>
 *
 * @author Truong Ngo
 * @version 2.0.0
 */
public class ExpressionDescriptor {

    /**
     * The raw string expression (e.g., "price * quantity", "LOWER(name)", etc.).
     */
    private String expression;

    /**
     * The alias assigned to the expression in the result set.
     * This alias can be referenced by other clauses in the query.
     */
    private String alias;

}
