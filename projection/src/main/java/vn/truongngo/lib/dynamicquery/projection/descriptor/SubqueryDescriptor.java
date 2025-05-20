package vn.truongngo.lib.dynamicquery.projection.descriptor;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Represents a scalar subquery selection in the SELECT clause.
 * <p>
 * This descriptor models a subquery that returns a single column from a projected target,
 * allowing its value to be included as a field in the parent query result.
 * </p>
 *
 * <h2>Example Use Case</h2>
 * <blockquote><pre>
 * {@code @Subquery}(target = ProductStats.class, column = "averagePrice", alias = "avgPrice")
 * private BigDecimal avgPrice;
 * </pre></blockquote>
 * <p>
 * This will translate into a scalar subquery that selects the {@code averagePrice}
 * column from the {@code ProductStats} projection.
 * </p>
 *
 * @author Truong Ngo
 * @version 2.0.0
 */
@Getter
@Setter
@SuperBuilder
public class SubqueryDescriptor extends AbstractSelectDescriptor {

    /**
     * The projection class that defines the subquery target.
     * This class should be annotated to describe how to build the subquery.
     */
    private Class<?> targetProjection;

    /**
     * The column within the subquery result to select and project into the parent query.
     */
    private String column;

}
