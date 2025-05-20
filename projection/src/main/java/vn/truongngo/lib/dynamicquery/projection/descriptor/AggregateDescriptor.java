package vn.truongngo.lib.dynamicquery.projection.descriptor;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import vn.truongngo.lib.dynamicquery.core.enumerate.AggregateFunction;

/**
 * Descriptor representing an aggregate function selection in a query projection.
 * <p>
 * This class is typically constructed based on {@code @Aggregate} annotations,
 * and is used to describe aggregate expressions such as {@code SUM}, {@code COUNT}, etc.,
 * to be included in the SELECT clause.
 * </p>
 *
 * <h2>Example</h2>
 * <blockquote><pre>
 * {@code @Aggregate}(function = "SUM", column = "amount", alias = "totalAmount")
 * private BigDecimal totalAmount;
 * </pre></blockquote>
 *
 * <p>
 * The {@code selection} represents the target column or expression being aggregated.
 * The {@code alias} defines how the result should be referenced in the result set.
 * </p>
 *
 * @author Truong Ngo
 * @version 2.0.0
 */
@Getter
@Setter
@SuperBuilder
public class AggregateDescriptor extends AbstractSelectDescriptor {

    /**
     * The aggregate function to apply (e.g., SUM, COUNT, AVG).
     */
    private AggregateFunction function;

    /**
     * The descriptor representing the column or expression to be aggregated.
     */
    private ColumnDescriptor column;

    /**
     * Indicates whether the aggregation should apply only to distinct values.
     */
    private boolean distinct;

}
